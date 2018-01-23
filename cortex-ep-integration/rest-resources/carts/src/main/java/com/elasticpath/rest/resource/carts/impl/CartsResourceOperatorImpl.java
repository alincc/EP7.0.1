/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.carts.CartLookup;
import com.elasticpath.rest.resource.carts.lineitems.Memberships;
import com.elasticpath.rest.resource.carts.lineitems.integration.LineItemLookupStrategy;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Processes the resource operations on carts.
 */
@Singleton
@Named("cartsResourceOperator")
@Path(ResourceName.PATH_PART)
public final class CartsResourceOperatorImpl implements ResourceOperator {

	private final CartLookup cartLookup;
	private final LineItemLookupStrategy lineItemLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param cartLookup the {@link CartLookup}
	 * @param lineItemLookupStrategy the {@link LineItemLookupStrategy}
	 */
	@Inject
	CartsResourceOperatorImpl(
		@Named("cartLookup")
			final CartLookup cartLookup,
		@Named("lineItemLookupStrategy")
			final LineItemLookupStrategy lineItemLookupStrategy) {
		this.cartLookup = cartLookup;
		this.lineItemLookupStrategy = lineItemLookupStrategy;
	}

	/**
	 * Handles READ operations for cart.
	 *
	 * @param scope     the scope
	 * @param cartId    the cart ID
	 * @param operation the resource operation.
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processCartRead(
			@Scope
			final String scope,
			@ResourceId
			final String cartId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<CartEntity>> result = cartLookup.findCart(scope, cartId);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles READ add to cart form operation for a cart.
	 *
	 * @param itemRepresentation the {@link ResourceState}
	 * @param operation          the resource operation
	 * @return the operation result
	 */
	@Path({SingleResourceUri.PATH_PART, Form.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadForm(
			@SingleResourceUri
			final ResourceState<ItemEntity> itemRepresentation,
			final ResourceOperation operation) {

		final String itemId = itemRepresentation.getEntity().getItemId();
		final LineItemConfigurationEntity config = lineItemLookupStrategy.getItemConfiguration(itemRepresentation.getScope(), itemId).getData();

		Self formSelf = SelfFactory.createSelf(operation.getUri());
		LineItemEntity lineItemEntity = LineItemEntity.builder()
				.withQuantity(0)
				.withItemId(itemId)
				.withConfiguration(config)
				.build();

		ResourceState.Builder<LineItemEntity> cartLineItemFormBuilder = ResourceState.Builder
				.create(lineItemEntity)
				.withScope(itemRepresentation.getScope())
				.withSelf(formSelf);

		ExecutionResult<ResourceState<LineItemEntity>> result = ExecutionResultFactory.createReadOK(cartLineItemFormBuilder.build());

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles READ of carts memberships. This checks for carts that contain a given item.
	 * @param itemRepresentation the item to check for in carts
	 * @param operation the operation
	 * @return a collection of links to carts containing the item.
	 */
	@Path({Memberships.PATH_PART, SingleResourceUri.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadMemberships(
		@SingleResourceUri
		final ResourceState<ItemEntity> itemRepresentation,
		final ResourceOperation operation) {
			ItemEntity item = itemRepresentation.getEntity();

			Self self = SelfFactory.createSelf(operation.getUri());
			Collection<ResourceLink> resourceLinks = Assign.ifSuccessful(
					cartLookup.getCartMemberships(itemRepresentation.getScope(), item.getItemId()));

			ResourceState<LinksEntity> result = ResourceState.Builder.create(ResourceTypeFactory.createResourceEntity(LinksEntity.class))
					.withLinks(resourceLinks)
					.withSelf(self)
					.build();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(ExecutionResultFactory.createReadOK(result), operation);
	}
}
