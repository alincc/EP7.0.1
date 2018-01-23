/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.rates.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.rates.RateEntity;

/**
 * Queries for the rates information.
 */
public interface CartLineItemRateLookupStrategy {

	/**
	 * Gets the rate of a line item.
	 *
	 * @param scope the scope
	 * @param decodedCartId the decoded cart id
	 * @param decodedLineItemId the decoded line item id
	 * @return the result
	 */
	ExecutionResult<RateEntity> getLineItemRate(String scope, String decodedCartId, String decodedLineItemId);
}
