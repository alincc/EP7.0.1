/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.alias.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.carts.CartLookup;
import com.elasticpath.rest.resource.carts.alias.integration.DefaultCartLookupStrategy;
import com.elasticpath.rest.resource.carts.lineitems.LineItemWriter;
import com.elasticpath.rest.resource.carts.lineitems.LineItems;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Line Item resource operator.
 */
@Singleton
@Named("defaultLineItemsResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, Default.PATH_PART, LineItems.PATH_PART})
public final class DefaultLineItemsResourceOperatorImpl implements ResourceOperator {
	private final CartLookup cartLookup;
	private final LineItemWriter lineItemWriter;
	private final DefaultCartLookupStrategy defaultCartLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param cartLookup                the {@link CartLookup}
	 * @param lineItemWriter            the {@link LineItemWriter}
	 * @param defaultCartLookupStrategy the {@link DefaultCartLookupStrategy}
	 */
	@Inject
	DefaultLineItemsResourceOperatorImpl(
			@Named("cartLookup")
			final CartLookup cartLookup,
			@Named("lineItemWriter")
			final LineItemWriter lineItemWriter,
			@Named("defaultCartLookupStrategy")
			final DefaultCartLookupStrategy defaultCartLookupStrategy) {
		this.cartLookup = cartLookup;
		this.lineItemWriter = lineItemWriter;
		this.defaultCartLookupStrategy = defaultCartLookupStrategy;
	}

	/**
	 * Handles CREATE form operation.
	 *
	 * @param scope     the scope
	 * @param itemId    the item {@link ResourceState}
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path({ResourceName.PATH_PART, Scope.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.CREATE)
	public OperationResult processCreateLineItem(
			@Scope
			final String scope,
			@ResourceId
			final String itemId,
			final ResourceOperation operation) {

		String defaultCartId = Assign.ifSuccessful(defaultCartLookupStrategy.getDefaultCartId(scope));
		ResourceState<CartEntity> cartRepresentation =
				Assign.ifSuccessful(cartLookup.findCart(scope, Base32Util.encode(defaultCartId)));

		ExecutionResult<ResourceState<ResourceEntity>> result = lineItemWriter.addLineItemToCart(cartRepresentation, itemId,
				getPostedEntity(operation));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	private LineItemEntity getPostedEntity(final ResourceOperation operation) {
		ResourceState<?> resourceState = Assign.ifNotNull(operation.getResourceState(),
				OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		return ResourceTypeFactory.adaptResourceEntity(resourceState.getEntity(), LineItemEntity.class);
	}
}
