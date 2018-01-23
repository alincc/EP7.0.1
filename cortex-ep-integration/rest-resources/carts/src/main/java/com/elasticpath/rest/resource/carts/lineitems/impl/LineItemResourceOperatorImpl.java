/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.impl;

import static com.elasticpath.rest.definition.carts.CartsMediaTypes.CART;

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
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;
import com.elasticpath.rest.resource.carts.lineitems.LineItemLookup;
import com.elasticpath.rest.resource.carts.lineitems.LineItemWriter;
import com.elasticpath.rest.resource.carts.lineitems.LineItems;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;

/**
 * Line Item resource operator.
 */
@Singleton
@Named("lineItemResourceOperator")
@Path({AnyResourceUri.PATH_PART, LineItems.PATH_PART})
public final class LineItemResourceOperatorImpl implements ResourceOperator {

	private final LineItemLookup lineItemLookup;
	private final LineItemWriter lineItemWriter;
	private final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param lineItemLookup                 the {@link LineItemLookup}
	 * @param lineItemWriter                 the {@link LineItemWriter}
	 * @param cartLineItemsUriBuilderFactory the {@link CartLineItemsUriBuilderFactory}
	 */
	@Inject
	LineItemResourceOperatorImpl(
			@Named("lineItemLookup")
			final LineItemLookup lineItemLookup,
			@Named("lineItemWriter")
			final LineItemWriter lineItemWriter,
			@Named("cartLineItemsUriBuilderFactory")
			final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory) {

		this.lineItemLookup = lineItemLookup;
		this.lineItemWriter = lineItemWriter;
		this.cartLineItemsUriBuilderFactory = cartLineItemsUriBuilderFactory;
	}

	/**
	 * Handles CREATE form operation.
	 *
	 * @param cartRepresentation the cart {@link ResourceState}
	 * @param itemId             the item id String
	 * @param operation          the resource operation
	 * @return the operation result
	 */
	@Path({ResourceName.PATH_PART, Scope.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.CREATE)
	public OperationResult processCreateLineItem(
			@AnyResourceUri
			final ResourceState<CartEntity> cartRepresentation,
			@ResourceId
			final String itemId,
			final ResourceOperation operation) {

		//This should be handled by the integration
		ExecutionResult<ResourceState<ResourceEntity>> result = lineItemWriter.addLineItemToCart(
				cartRepresentation,
				itemId,
				getPostedEntity(operation)
		);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles DELETE operation for all line items in the cart.
	 *
	 * @param cartResourceState the cart {@link ResourceState}
	 * @param operation         the resource operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.DELETE)
	public OperationResult processDeleteLineItems(
			@AnyResourceUri
			final ResourceState<CartEntity> cartResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<ResourceEntity>> result = ExecutionResultFactory.asIs(lineItemWriter.removeAll(cartResourceState));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles DELETE operation for specific line item.
	 *
	 * @param cartResourceState the cart {@link ResourceState}
	 * @param lineItemId        the line item ID
	 * @param operation         the resource operation
	 * @return the operation result
	 */
	@Path(ResourceId.PATH_PART)
	@OperationType(Operation.DELETE)
	public OperationResult processDeleteLineItem(
			@AnyResourceUri
			final ResourceState<CartEntity> cartResourceState,
			@ResourceId
			final String lineItemId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<ResourceEntity>> result = ExecutionResultFactory.asIs(lineItemWriter.remove(cartResourceState, lineItemId));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles READ operations for line items.
	 *
	 * @param cart      the cart {@link ResourceState}
	 * @param operation the Resource Operation
	 * @return the operation result.
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadLineItems(
			@AnyResourceUri
			final ResourceState<CartEntity> cart,
			final ResourceOperation operation) {


		Self cartSelf = cart.getSelf();
		String selfUri = cartLineItemsUriBuilderFactory.get()
				.setSourceUri(cartSelf.getUri())
				.build();
		LinksEntity linksEntity = LinksEntity.builder()
				.withElementListId(cart.getEntity()
						.getCartId())
				.withElementListType(CART.id())
				.build();
		ResourceState<LinksEntity> lineItemLinks = ResourceState.Builder
				.create(linksEntity)
				.withSelf(SelfFactory.createSelf(selfUri))
				.withScope(cart.getScope())
				.build();
		ExecutionResult<ResourceState<LinksEntity>> result = ExecutionResultFactory.createReadOK(lineItemLinks);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles READ operation for specific line item.
	 *
	 * @param cartRepresentation the cart {@link ResourceState}
	 * @param lineItemId         the line item ID
	 * @param operation          the Resource Operation
	 * @return the operation result.
	 */
	@Path(ResourceId.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadLineItem(
			@AnyResourceUri
			final ResourceState<CartEntity> cartRepresentation,
			@ResourceId
			final String lineItemId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LineItemEntity>> result = lineItemLookup.find(cartRepresentation, lineItemId);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles Update operation.
	 *
	 * @param cartRepresentation the cart {@link ResourceState}
	 * @param lineItemId         the line item id
	 * @param operation          the resource operation
	 * @return the operation result
	 */
	@Path(ResourceId.PATH_PART)
	@OperationType(Operation.UPDATE)
	public OperationResult processUpdateLineItem(
			@SingleResourceUri
			final ResourceState<CartEntity> cartRepresentation,
			@ResourceId
			final String lineItemId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<ResourceEntity>> result = ExecutionResultFactory.asIs(lineItemWriter.update(cartRepresentation,
				lineItemId,
				getPostedEntity(operation)));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	private LineItemEntity getPostedEntity(final ResourceOperation operation) {
		ResourceState<?> resourceState = Assign.ifNotNull(operation.getResourceState(),
				OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		return ResourceTypeFactory.adaptResourceEntity(resourceState.getEntity(), LineItemEntity.class);
	}
}
