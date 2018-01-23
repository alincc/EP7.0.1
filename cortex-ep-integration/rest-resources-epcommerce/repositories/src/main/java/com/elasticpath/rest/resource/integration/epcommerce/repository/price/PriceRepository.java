/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price;

import java.util.Set;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Repository for working with the Price domain.
 */
public interface PriceRepository {

	/**
	 * Returns the price for the sku in the given store.
	 *
	 * @param storeCode the store code
	 * @param skuCode   the product sku code
	 * @return the price
	 */
	ExecutionResult<Price> getPrice(String storeCode, String skuCode);

	/**
	 * Returns the lowest price of all skus of the given product in the given store.
	 *
	 * @param storeCode the store code
	 * @param itemId    the item ID
	 * @return the lowest price
	 */
	ExecutionResult<Price> getLowestPrice(String storeCode, String itemId);

	/**
	 * Returns the lowest price of all skus of the given product in the given store. This method will modify the passed in ruleTracker and add
	 * all rules that contributed to the price calculation.
	 *
	 * @param storeCode the store code
	 * @param itemId    the item id
	 * @return the promotion rules
	 */
	ExecutionResult<Set<Long>> getLowestPriceRules(String storeCode, String itemId);

	/**
	 * Checks if a price exists for the sku in the given store.
	 *
	 * @param storeCode the store code
	 * @param itemId    the item id
	 * @return true if a price exists for the sku in the store, false otherwise
	 */
	ExecutionResult<Boolean> priceExists(String storeCode, String itemId);
}
