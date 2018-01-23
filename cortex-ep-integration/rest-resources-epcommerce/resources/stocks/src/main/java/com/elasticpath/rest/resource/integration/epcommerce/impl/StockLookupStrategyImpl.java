/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.stocks.StockEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.stock.integration.StockLookupStrategy;

/**
 * Default implementation of {@link StockLookupStrategy}.
 */
@Singleton
@Named("stockLookupStrategy")
public class StockLookupStrategyImpl implements StockLookupStrategy {

	private final StoreProductRepository storeProductRepository;

	private final ItemRepository itemRepository;

	/**
	 * Constructor taking repositories.
	 *
	 * @param storeProductRepository a StoreProductRepository
	 * @param itemRepository         an ItemRepository
	 */
	@Inject
	public StockLookupStrategyImpl(
			@Named("storeProductRepository")
			final StoreProductRepository storeProductRepository,
			@Named("itemRepository")
			final ItemRepository itemRepository) {
		this.storeProductRepository = storeProductRepository;
		this.itemRepository = itemRepository;
	}

	@Override
	public ExecutionResult<StockEntity> getStockByItemId(final String scope, final String itemId) {

		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
		ExecutionResult<String> skuQuantityRemaining = getSkuQuantityRemaining(scope, productSku);

		Ensure.isTrue(isStockDisplayedForProductSku(productSku), ExecutionResultFactory.createNotFound());
		String quantityRemaining = skuQuantityRemaining.getData();
		StockEntity stockEntity = createStockEntity(itemId, quantityRemaining);

		return ExecutionResultFactory.createReadOK(stockEntity);
	}

	/**
	 * Determines whether a stock level should exist for the given {@link ProductSku}.<br/>
	 * This returns false if the product is unlimited-availability or digital.<br/>
	 * This method differs from {@link #isStockDisplayedForItem} by leveraging an already-found {@link ProductSku}.
	 *
	 * @param productSku the {@link ProductSku}
	 * @return whether a stock level should exist
	 */

	protected Boolean isStockDisplayedForProductSku(final ProductSku productSku) {
		Product product = productSku.getProduct();
		return !(isProductAvailabilityUnlimited(product) || productSku.isDigital());
	}

	/**
	 * Determines whether a product's availability criteria indicates that it is unlimited.
	 *
	 * @param product the {@link Product}
	 * @return whether the product availability is unlimited
	 */
	protected boolean isProductAvailabilityUnlimited(final Product product) {
		AvailabilityCriteria availabilityCriteria = product.getAvailabilityCriteria();
		return availabilityCriteria == AvailabilityCriteria.ALWAYS_AVAILABLE
				|| availabilityCriteria == AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER
				|| availabilityCriteria == AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER;
	}

	/**
	 * Gets the remaining quantity in stock for a {@link ProductSku}.
	 *
	 * @param scope      the {@link ProductSku}'s scope
	 * @param productSku the {@link ProductSku}
	 * @return the result of the lookup
	 */
	protected ExecutionResult<String> getSkuQuantityRemaining(final String scope, final ProductSku productSku) {

		Product product = productSku.getProduct();
		StoreProduct storeProduct = Assign.ifSuccessful(storeProductRepository
						.findDisplayableStoreProductWithAttributesByProductGuid(scope, product.getGuid()));
		SkuInventoryDetails inventoryDetails = storeProduct.getInventoryDetails(productSku.getSkuCode());
		String quantityRemaining = String.valueOf(inventoryDetails.getAvailableQuantityInStock());

		return ExecutionResultFactory.createReadOK(quantityRemaining);
	}

	/**
	 * Creates a populated {@link StockEntity} with the given field values.
	 *
	 * @param itemId            the item ID
	 * @param quantityRemaining the quantity in stock
	 * @return a {@link StockEntity}
	 */
	protected StockEntity createStockEntity(final String itemId, final String quantityRemaining) {
		return StockEntity.builder()
				.withItemId(itemId)
				.withQuantityRemaining(quantityRemaining)
				.build();
	}

	@Override
	public ExecutionResult<Boolean> isStockDisplayedForItem(final String scope, final String itemId) {

		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
		Boolean isStockDisplayedForItem = isStockDisplayedForProductSku(productSku);

		return ExecutionResultFactory.createReadOK(isStockDisplayedForItem);
	}

}
