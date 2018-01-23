/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;

/**
 * Queries for the Price information.
 */
public interface ItemPriceLookupStrategy {

	/**
	 * Determines if a price exists for a given item.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return true if a price exists, false if not
	 */
	ExecutionResult<Boolean> priceExists(String scope, String itemId);

	/**
	 * Gets the price of an item.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return the result
	 */
	ExecutionResult<ItemPriceEntity> getItemPrice(String scope, String itemId);

	/**
	 * Gets the price range of an item.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return the result
	 */
	ExecutionResult<PriceRangeEntity> getItemPriceRange(String scope, String itemId);
}
