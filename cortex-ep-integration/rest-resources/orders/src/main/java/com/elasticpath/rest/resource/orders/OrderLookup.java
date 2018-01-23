/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Order lookup.
 */
public interface OrderLookup {

	/**
	 * Finds an order by its order ID.
	 *
	 * @param scope the scope
	 * @param orderId the order ID
	 * @return execution result containing order information
	 */
	ExecutionResult<ResourceState<OrderEntity>> findOrderByOrderId(String scope, String orderId);

	/**
	 * Finds an order by the cart ID it is pointing to.
	 *
	 * @param scope the scope
	 * @param cartId the cart ID
	 * @return execution result containing order information
	 */
	ExecutionResult<ResourceState<OrderEntity>> findOrderByCartId(String scope, String cartId);

	/**
	 * Finds all order IDs for the user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return execution result containing order IDs
	 */
	ExecutionResult<Collection<String>> findOrderIds(String scope, String userId);

}
