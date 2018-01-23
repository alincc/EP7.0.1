/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order;

import java.util.Collection;

import com.elasticpath.domain.order.Order;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * The facade for operations with orders.
 */
public interface OrderRepository {

	/**
	 * Find by order GUID.
	 *
	 * @param storeCode the store code
	 * @param orderGuid the order GUID
	 * @return ExecutionResult with the order
	 */
	ExecutionResult<Order> findByGuid(String storeCode, String orderGuid);

	/**
	 * Find all order Ids for Customer GUID.
	 *
	 * @param storeCode the store code
	 * @param customerGuid the customer GUID
	 * @return ExecutionResult with the order Ids
	 */
	ExecutionResult<Collection<String>> findOrderIdsByCustomerGuid(String storeCode, String customerGuid);
}
