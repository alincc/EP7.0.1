/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * The Interface PurchaseWriterStrategy.
 */
public interface PurchaseWriterStrategy {

	/**
	 * Creates the purchase.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order id
	 * @return the created purchase ID
	 */
	ExecutionResult<String> createPurchase(String scope, String decodedOrderId);
}