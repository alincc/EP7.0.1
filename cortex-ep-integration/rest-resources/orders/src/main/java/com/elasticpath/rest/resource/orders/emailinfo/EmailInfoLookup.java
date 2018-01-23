/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Queries for reading an order's email information.
 */
public interface EmailInfoLookup {

	/**
	 * Find the selected email address ID for an order.
	 *
	 * @param scope the scope
	 * @param orderId the order ID
	 * @return execution result with the selected email address ID
	 */
	ExecutionResult<String> findEmailIdForOrder(String scope, String orderId);
}
