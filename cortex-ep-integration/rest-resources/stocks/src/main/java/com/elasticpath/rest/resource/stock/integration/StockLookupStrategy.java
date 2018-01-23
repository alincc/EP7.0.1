/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.stock.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.stocks.StockEntity;

/**
 * Provides lookup of stock data from external systems.
 */
public interface StockLookupStrategy {

	/**
	 * Finds stock according to a specific item ID.
	 *
	 * @param scope the scope
	 * @param itemId the item ID
	 * @return An {@link ExecutionResult} from attempting to find the {@link StockEntity}.
	 */
	ExecutionResult<StockEntity> getStockByItemId(String scope, String itemId);

	/**
	 * Determines whether a stock level should exist for the given item.
	 *
	 * @param scope the item's scope
	 * @param itemId the item ID
	 * @return whether a stock level should exist
	 */
	ExecutionResult<Boolean> isStockDisplayedForItem(String scope, String itemId);
}
