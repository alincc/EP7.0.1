/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.deliveries;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.DeliveryEntity;

/**
 * Lookup strategy for order delivery.
 */
public interface DeliveryLookupStrategy {

	/**
	 * Find by delivery code and order ID.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order ID
	 * @param decodedDeliveryId the decoded delivery ID
	 * @return the execution result
	 */
	ExecutionResult<DeliveryEntity> findByIdAndOrderId(String scope, String decodedOrderId, String decodedDeliveryId);

	/**
	 * Gets the delivery IDs for an order.
	 *
	 * @param scope the scope.
	 * @param decodedOrderId the decoded order ID
	 * @return the delivery IDs
	 */
	ExecutionResult<Collection<String>> getDeliveryIds(String scope, String decodedOrderId);
}
