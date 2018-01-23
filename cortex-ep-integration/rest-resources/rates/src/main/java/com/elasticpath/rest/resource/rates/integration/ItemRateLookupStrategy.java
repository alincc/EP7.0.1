/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.rates.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.rates.RateEntity;

/**
 * Queries for the rate information.
 */
public interface ItemRateLookupStrategy {

	/**
	 * Determines if a rate exists for a given item.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return true if a rate exists, false if not
	 */
	ExecutionResult<Boolean> rateExists(String scope, String itemId);

	/**
	 * Gets the rate of an item.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return the result
	 */
	ExecutionResult<RateEntity> getItemRate(String scope, String itemId);
}
