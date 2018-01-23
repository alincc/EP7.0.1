/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Writer class for create/update operations on billing information data.
 */
public interface BillingInfoWriter {

	/**
	 * Set selected billing address for order.
	 *
	 * @param scope the order scope
	 * @param orderId the order ID
	 * @param addressId the address ID
	 * @return an execution result indicating success or failure
	 */
	ExecutionResult<Boolean> setAddressForOrder(String scope, String orderId, String addressId);
}
