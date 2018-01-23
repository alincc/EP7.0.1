/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.carts.lineitems.LineItemWriter;
import com.elasticpath.rest.resource.carts.lineitems.integration.LineItemWriterStrategy;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;

/**
 * Creates or updates line items.
 */
@Singleton
@Named("lineItemWriter")
public final class LineItemWriterImpl implements LineItemWriter {

	private static final String QUANTITY_IS_EITHER_MISSING_OR_IS_NOT_AN_INTEGER = "Quantity is either missing or is not an integer";
	private final LineItemWriterStrategy lineItemWriterStrategy;
	private final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param lineItemWriterStrategy         the line item writer strategy
	 * @param cartLineItemsUriBuilderFactory the cart line items uri builder factory
	 */
	@Inject
	LineItemWriterImpl(
			@Named("lineItemWriterStrategy")
			final LineItemWriterStrategy lineItemWriterStrategy,
			@Named("cartLineItemsUriBuilderFactory")
			final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory) {

		this.lineItemWriterStrategy = lineItemWriterStrategy;
		this.cartLineItemsUriBuilderFactory = cartLineItemsUriBuilderFactory;
	}


	@Override
	public ExecutionResult<Void> remove(final ResourceState<CartEntity> cartState, final String lineItemId) {
		LineItemEntity entity = LineItemEntity.builder()
				.withCartId(Base32Util.decode(cartState.getEntity()
						.getCartId()))
				.withLineItemId(Base32Util.decode(lineItemId))
				.build();
		return lineItemWriterStrategy.deleteLineItemFromCart(cartState.getScope(), entity);
	}

	@Override
	public ExecutionResult<Void> removeAll(final ResourceState<CartEntity> cartRepresentation) {
		String cartId = Base32Util.decode(cartRepresentation.getEntity().getCartId());
		return lineItemWriterStrategy.deleteAllLineItemsFromCart(cartRepresentation.getScope(), cartId);
	}

	@Override
	public ExecutionResult<ResourceState<ResourceEntity>> addLineItemToCart(
			final ResourceState<CartEntity> cartRepresentation,
			final String itemId,
			final LineItemEntity postedLineItemEntity) {


		Integer quantity = postedLineItemEntity.getQuantity();
		Ensure.notNull(quantity, OnFailure.returnBadRequestBody(QUANTITY_IS_EITHER_MISSING_OR_IS_NOT_AN_INTEGER));

		LineItemEntity lineItemEntity = LineItemEntity.builder()
				.withCartId(Base32Util.decode(cartRepresentation.getEntity()
						.getCartId()))
				.withItemId(itemId)
				.withQuantity(quantity)
				.withConfiguration(postedLineItemEntity.getConfiguration())
				.build();

		ExecutionResult<LineItemEntity> addResult = lineItemWriterStrategy.addToCart(cartRepresentation.getScope(), lineItemEntity);
		LineItemEntity updatedLineItem = Assign.ifSuccessful(addResult);

		String lineItemUri = createLineItemUri(cartRepresentation, updatedLineItem.getLineItemId());
		return ExecutionResultFactory.createCreateOK(lineItemUri, lineItemExists(addResult));
	}

	@Override
	public ExecutionResult<Void> update(final ResourceState<CartEntity> cartRepresentation,
										final String lineItemId,
										final LineItemEntity updatedLineItemEntity) {

		Integer quantity = updatedLineItemEntity.getQuantity();
		Ensure.notNull(quantity, OnFailure.returnBadRequestBody(QUANTITY_IS_EITHER_MISSING_OR_IS_NOT_AN_INTEGER));

		LineItemEntity entity = LineItemEntity.builder()
				.withCartId(Base32Util.decode(cartRepresentation.getEntity()
						.getCartId()))
				.withLineItemId(Base32Util.decode(lineItemId))
				.withQuantity(updatedLineItemEntity.getQuantity())
				.withConfiguration(updatedLineItemEntity.getConfiguration())
				.build();

		return ExecutionResultFactory.asIs(lineItemWriterStrategy.updateLineItem(cartRepresentation.getScope(), entity));
	}

	private boolean lineItemExists(final ExecutionResult<LineItemEntity> addResult) {
		return addResult.getResourceStatus() == ResourceStatus.READ_OK;
	}

	private String createLineItemUri(final ResourceState<?> cartRepresentation, final String decodedLineItemId) {

		return cartLineItemsUriBuilderFactory.get()
				.setSourceUri(cartRepresentation.getSelf()
						.getUri())
				.setLineItemId(Base32Util.encode(decodedLineItemId))
				.build();
	}
}
