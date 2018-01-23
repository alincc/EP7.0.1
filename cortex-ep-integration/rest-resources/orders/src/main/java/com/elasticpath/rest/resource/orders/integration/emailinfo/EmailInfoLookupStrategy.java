/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.emailinfo;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Lookup strategy for email info.
 */
public interface EmailInfoLookupStrategy {

	/**
	 * Gets the ID of the email address for an order.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order ID
	 * @return the email address ID.
	 */
	ExecutionResult<String> getEmailIdForOrder(String scope, String decodedOrderId);
}
