/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.rates.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.rates.RateEntity;

/**
 * Look up strategy for purchase lineitem rates.
 */
public interface PurchaseLineItemRateLookupStrategy {

	/**
	 * Gets the rate of a line item.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @param decodedLineItemId the decoded line item id
	 * @return the result
	 */
	ExecutionResult<RateEntity> getLineItemRate(String scope, String decodedPurchaseId, String decodedLineItemId);
}
