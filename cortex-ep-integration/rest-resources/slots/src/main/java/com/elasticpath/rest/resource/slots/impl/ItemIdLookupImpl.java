/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.slots.ItemIdLookup;
import com.elasticpath.rest.resource.slots.integration.ItemIdLookupStrategy;

/**
 * The item ID lookup implementation for obtaining the default item ID corresponding to the specified product.
 */
@Singleton
@Named("itemIdLookup")
public final class ItemIdLookupImpl implements ItemIdLookup {

	private final ItemIdLookupStrategy itemIdLookupStrategy;


	/**
	 * Constructor for the lookup.
	 *
	 * @param itemIdLookupStrategy the item id lookup strategy
	 */
	@Inject
	public ItemIdLookupImpl(
			@Named("itemIdLookupStrategy")
			final ItemIdLookupStrategy itemIdLookupStrategy) {

		this.itemIdLookupStrategy = itemIdLookupStrategy;
	}

	@Override
	public ExecutionResult<String> getDefaultItemIdForProduct(final String scope, final String productId) {

		String decodedProductId = Base32Util.decode(productId);
		String defaultItemId = Assign.ifSuccessful(
				itemIdLookupStrategy.getDefaultItemIdForProduct(scope, decodedProductId));

		return ExecutionResultFactory.createReadOK(defaultItemId);
	}
}
