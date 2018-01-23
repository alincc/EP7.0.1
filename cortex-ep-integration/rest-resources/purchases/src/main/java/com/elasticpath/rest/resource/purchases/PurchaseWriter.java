/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Writer interface for updating Purchase data.
 */
public interface PurchaseWriter {

	/**
	 * Create purchase.
	 *
	 * @param scope the scope
	 * @param orderId the order ID
	 * @return the ID of the newly created purchase
	 */
	ExecutionResult<String> createPurchase(String scope, String orderId);
}
