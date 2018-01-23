/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.billinginfo;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Lookup strategy for billing info.
 */
public interface BillingInfoLookupStrategy {

	/**
	 * Gets the ID of the preferred billing address.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order ID
	 * @return the preferred billing address ID.
	 */
	ExecutionResult<String> getBillingAddress(String scope, String decodedOrderId);
}
