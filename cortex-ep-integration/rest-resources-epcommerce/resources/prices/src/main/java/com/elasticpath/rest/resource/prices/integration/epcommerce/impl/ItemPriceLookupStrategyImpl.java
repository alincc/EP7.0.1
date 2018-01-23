/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.prices.integration.ItemPriceLookupStrategy;

/**
 * Lookup from core class for item price.
 */
@Singleton
@Named("itemPriceLookupStrategy")
public class ItemPriceLookupStrategyImpl implements ItemPriceLookupStrategy {

	private final ItemRepository itemRepository;
	private final PriceRepository priceRepository;
	private final MoneyTransformer moneyTransformer;


	/**
	 * Constructor.
	 *
	 * @param itemRepository   the item repository
	 * @param priceRepository  the price repository
	 * @param moneyTransformer the money transformer
	 */
	@Inject
	ItemPriceLookupStrategyImpl(
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("priceRepository")
			final PriceRepository priceRepository,
			@Named("moneyTransformer") final MoneyTransformer moneyTransformer) {

		this.itemRepository = itemRepository;
		this.priceRepository = priceRepository;
		this.moneyTransformer = moneyTransformer;
	}


	@Override
	public ExecutionResult<Boolean> priceExists(final String scope, final String itemId) {
		return priceRepository.priceExists(scope, itemId);
	}

	@Override
	public ExecutionResult<ItemPriceEntity> getItemPrice(final String storeCode, final String itemId) {
		String skuCode = Assign.ifSuccessful(itemRepository.getSkuCodeForItemId(itemId));
		Price price = Assign.ifSuccessful(priceRepository.getPrice(storeCode, skuCode));

		CostEntity purchaseCostEntity = moneyTransformer.transformToEntity(price.getLowestPrice());
		CostEntity listCostEntity = moneyTransformer.transformToEntity(price.getListPrice());

		return ExecutionResultFactory.createReadOK(
				ItemPriceEntity.builder()
						.addingPurchasePrice(purchaseCostEntity)
						.addingListPrice(listCostEntity)
						.build()
		);
	}

	@Override
	public ExecutionResult<PriceRangeEntity> getItemPriceRange(final String storeCode, final String itemId) {
		Price price = Assign.ifSuccessful(priceRepository.getLowestPrice(storeCode, itemId));
		CostEntity purchaseCostEntity = moneyTransformer.transformToEntity(price.getLowestPrice());

		return ExecutionResultFactory.createReadOK(
				PriceRangeEntity.builder()
						.addingFromPrice(purchaseCostEntity)
						.build()
		);
	}

}
