/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup for order delivery.
 */
public interface DeliveryLookup {

	/**
	 * Find by id and order id.
	 *
	 * @param scope the scope
	 * @param orderId the order id
	 * @param deliveryId the delivery id
	 * @return the execution result
	 */
	ExecutionResult<ResourceState<DeliveryEntity>> findByIdAndOrderId(String scope, String orderId, String deliveryId);

	/**
	 * Gets the delivery IDs for an order.
	 *
	 * @param scope the scope
	 * @param orderId the order id
	 * @return the deliveries
	 */
	ExecutionResult<Collection<String>> getDeliveryIds(String scope, String orderId);
}
