/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.billinginfo;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Writer strategy for billing info.
 */
public interface BillingInfoWriterStrategy {

	/**
	 * Sets the billing address for an order.
	 *
	 * @param scope the order's scope
	 * @param decodedAddressId the decoded address ID
	 * @param decodedOrderId the decoded target order ID
	 * @return true if the order already had a billing address, false if not, or error
	 */
	ExecutionResult<Boolean> setBillingAddress(String scope, String decodedAddressId, String decodedOrderId);
}
