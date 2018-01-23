/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;

/**
 * Lookup class for item price.
 */
public interface ItemPriceLookup {

	/**
	 * Checks for the existence of a price for an item.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return true if a price exists
	 */
	ExecutionResult<Boolean> priceExists(String scope, String itemId);

	/**
	 * Gets the price information for a given item in a given scope.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return the {@link com.elasticpath.rest.definition.prices.ItemPriceEntity} for item
	 */
	ExecutionResult<ItemPriceEntity> getItemPrice(String scope, String itemId);

	/**
	 * Gets the price range information.
	 *
	 * @param scope the scope
	 * @param itemId the item id.
	 * @return the {@link com.elasticpath.rest.definition.prices.PriceRangeEntity} for item definition.
	 */
	ExecutionResult<PriceRangeEntity> getItemPriceRange(String scope, String itemId);
}
