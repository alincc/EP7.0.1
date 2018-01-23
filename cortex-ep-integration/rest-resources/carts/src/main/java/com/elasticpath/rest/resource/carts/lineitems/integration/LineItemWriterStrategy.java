/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.LineItemEntity;

/**
 * Strategy for cart lineitem writer.
 */
public interface LineItemWriterStrategy {

	/**
	 * Deletes a line item.
	 *
	 * @param scope the scope
	 * @param entity the {@link LineItemEntity}
	 * @return nothing, but allows checking for success or failure of operation
	 */
	ExecutionResult<Void> deleteLineItemFromCart(String scope, LineItemEntity entity);

	/**
	 * Deletes all line items in the cart.
	 *
	 * @param scope the scope
	 * @param cartId the cart Id
	 * @return nothing, but allows checking for success or failure of operation
	 */
	ExecutionResult<Void> deleteAllLineItemsFromCart(String scope, String cartId);

	/**
	 * Adds an item to the cart.
	 *
	 * @param scope the scope
	 * @param entity the {@link LineItemEntity}
	 * @return the {@link LineItemEntity} of the added item or failure
	 */
	ExecutionResult<LineItemEntity> addToCart(String scope, LineItemEntity entity);

	/**
	 * Updates a line item to the cart.
	 *
	 * @param scope the scope
	 * @param entity the {@link LineItemEntity}
	 * @return the update success or failure.
	 */
	ExecutionResult<Void> updateLineItem(String scope, LineItemEntity entity);
}
