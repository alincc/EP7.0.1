/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Queries for reading an order's billing information.
 */
public interface BillingInfoLookup {

	/**
	 * Find the selected billing address ID for the order.
	 *
	 * @param scope the scope
	 * @param orderId the order ID
	 * @return execution result with the selected billing address ID
	 */
	ExecutionResult<String> findAddressForOrder(String scope, String orderId);
}
