/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.resource.prices.ItemPriceLookup;
import com.elasticpath.rest.resource.prices.integration.ItemPriceLookupStrategy;

/**
 * Lookup from core class for item price.
 */
@Singleton
@Named("itemPriceLookup")
public final class ItemPriceLookupImpl implements ItemPriceLookup {

	private final ItemPriceLookupStrategy itemPriceLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param itemPriceLookupStrategy price lookup strategy
	 */
	@Inject
	ItemPriceLookupImpl(
			@Named("itemPriceLookupStrategy")
			final ItemPriceLookupStrategy itemPriceLookupStrategy) {
		this.itemPriceLookupStrategy = itemPriceLookupStrategy;
	}

	@Override
	public ExecutionResult<Boolean> priceExists(final String scope, final String itemId) {
		return itemPriceLookupStrategy.priceExists(scope, itemId);
	}

	@Override
	public ExecutionResult<ItemPriceEntity> getItemPrice(final String scope, final String itemId) {
		ItemPriceEntity itemPriceEntity = Assign.ifSuccessful(itemPriceLookupStrategy.getItemPrice(scope, itemId));
		return ExecutionResultFactory.createReadOK(itemPriceEntity);
	}

	@Override
	public ExecutionResult<PriceRangeEntity> getItemPriceRange(final String scope, final String itemId) {
		PriceRangeEntity priceRangeEntity = Assign.ifSuccessful(itemPriceLookupStrategy.getItemPriceRange(scope, itemId));
		return ExecutionResultFactory.createReadOK(priceRangeEntity);
	}
}
