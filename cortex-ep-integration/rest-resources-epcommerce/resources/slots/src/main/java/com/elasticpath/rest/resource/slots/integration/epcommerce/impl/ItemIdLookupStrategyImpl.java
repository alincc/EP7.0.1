/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.slots.integration.ItemIdLookupStrategy;

/**
 * The EP implementation of Item For ProductLookup Strategy.
 */
@Singleton
@Named("itemIdLookupStrategy")
public class ItemIdLookupStrategyImpl implements ItemIdLookupStrategy {

	private final ItemRepository itemRepository;
	private final StoreProductRepository storeProductRepository;

	/**
	 * Constructor for injection.
	 *
	 * @param itemRepository         the item repository
	 * @param storeProductRepository a product repository
	 */
	@Inject
	public ItemIdLookupStrategyImpl(
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("storeProductRepository")
			final StoreProductRepository storeProductRepository) {

		this.itemRepository = itemRepository;
		this.storeProductRepository = storeProductRepository;
	}


	@Override
	public ExecutionResult<String> getDefaultItemIdForProduct(final String storeCode, final String productCode) {

		Product product = Assign.ifNotNull(getProduct(productCode),
				OnFailure.returnNotFound("no item found"));

		return itemRepository.getDefaultItemIdForProduct(product);
	}

	private Product getProduct(final String productCode) {
		return storeProductRepository.findByGuid(productCode);
	}
}
