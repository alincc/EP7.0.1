/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.OrderEntity;

/**
 * Lookup strategy for orders.
 */
public interface OrderLookupStrategy {

	/**
	 * Gets the cart order dto.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order ID
	 * @return the cart order dto
	 */
	ExecutionResult<OrderEntity> getOrder(String scope, String decodedOrderId);

	/**
	 * Gets the cart order dto for the given cart guid.
	 *
	 * @param scope the scope
	 * @param decodedCartId the decoded cart ID
	 * @return the cart order dto
	 */
	ExecutionResult<OrderEntity> getOrderForCart(String scope, String decodedCartId);

	/**
	 * Gets the Order IDs for the user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the collection of Order IDs
	 */
	ExecutionResult<Collection<String>> getOrderIds(String scope, String userId);
}
