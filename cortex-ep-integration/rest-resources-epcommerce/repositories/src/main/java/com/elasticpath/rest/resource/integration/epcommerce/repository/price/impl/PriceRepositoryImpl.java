/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;

/**
 * Repository that consolidates access to price domain concepts.
 */
@Singleton
@Named("priceRepository")
public class PriceRepositoryImpl implements PriceRepository {
	private static final String PRODUCT_PRICE_NOT_FOUND = "No price found for product with guid = '%s'";
	private static final String SKU_PRICE_NOT_FOUND = "No price found for sku with code = '%s'";
	private static final int SINGLE_QTY = 1;
	/**
	 * cache identifier.
	 */
	public static final String CACHE_PRICE_REPO_KEY_PREFIX = "priceRepository";


	private final ShoppingItemDtoFactory shoppingItemDtoFactory;
	private final StoreRepository storeRepository;
	private final CustomerSessionRepository customerSessionRepository;
	private final PriceLookupFacade priceLookupFacade;
	private final ItemRepository itemRepository;


	/**
	 * Creates instance with needed services wired in.
	 *
	 * @param shoppingItemDtoFactory    shopping item dto factory
	 * @param storeRepository           store repository
	 * @param customerSessionRepository customer session repository
	 * @param priceLookupFacade         price lookup facade
	 * @param itemRepository            the item repository
	 */
	@Inject
	PriceRepositoryImpl(
			@Named("shoppingItemDtoFactory")
			final ShoppingItemDtoFactory shoppingItemDtoFactory,
			@Named("storeRepository")
			final StoreRepository storeRepository,
			@Named("customerSessionRepository")
			final CustomerSessionRepository customerSessionRepository,
			@Named("priceLookupFacade")
			final PriceLookupFacade priceLookupFacade,
			@Named("itemRepository")
			final ItemRepository itemRepository) {

		this.shoppingItemDtoFactory = shoppingItemDtoFactory;
		this.storeRepository = storeRepository;
		this.customerSessionRepository = customerSessionRepository;
		this.priceLookupFacade = priceLookupFacade;
		this.itemRepository = itemRepository;
	}


	@Override
	@CacheResult
	public ExecutionResult<Price> getPrice(final String storeCode, final String skuCode) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CustomerSession customerSession = Assign.ifSuccessful(customerSessionRepository.findOrCreateCustomerSession());
				Shopper shopper = customerSession.getShopper();
				Store store = Assign.ifSuccessful(storeRepository.findStore(storeCode));

				//We want the calculated price of the item, as it will be when added to the cart, but without cart promotions.
				ShoppingItemDto shoppingItemDto = shoppingItemDtoFactory.createDto(skuCode, SINGLE_QTY);
				Price price = Assign.ifNotNull(priceLookupFacade.getShoppingItemDtoPrice(shoppingItemDto, store, shopper),
						OnFailure.returnNotFound(SKU_PRICE_NOT_FOUND, skuCode));
				return ExecutionResultFactory.createReadOK(price);
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "lowestPrice")
	public ExecutionResult<Price> getLowestPrice(final String storeCode, final String itemId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Price price = getLowestPriceImpl(itemId, true).getPrice();
				return ExecutionResultFactory.createReadOK(price);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Set<Long>> getLowestPriceRules(final String storeCode, final String itemId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Set<Long> rules = getLowestPriceImpl(itemId, false).getRules();
				return ExecutionResultFactory.createReadOK(rules);
			}
		}.execute();
	}

	/**
	 * Named immutable lowest price pair.
	 */
	static class LowestPrice {
		private final Price price;
		private final Set<Long> rules;

		/**
		 * constructor.
		 *
		 * @param price price
		 * @param rules rules
		 */
		LowestPrice(final Price price, final Set<Long> rules) {
			this.price = price;
			this.rules = rules;
		}

		public Set<Long> getRules() {
			return rules;
		}

		public Price getPrice() {

			return price;
		}
	}

	private LowestPrice getLowestPriceImpl(final String itemId, final boolean validateMultipleSkus) {
		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
		Product product = productSku.getProduct();
		assert product != null : "product must not be null.";
		assert product.getGuid() != null : "product guid must not be null.";
		if (validateMultipleSkus) {
			Ensure.isTrue(product.hasMultipleSkus(), OnFailure.returnNotFound());
		}

		CustomerSession customerSession = Assign.ifSuccessful(customerSessionRepository.findOrCreateCustomerSession());
		Shopper shopper = customerSession.getShopper();
		Store store = Assign.ifSuccessful(storeRepository.findStore(shopper.getStoreCode()));
		Price price = Assign.ifNotNull(priceLookupFacade.getPromotedPriceForProduct(product, store, shopper),
				OnFailure.returnNotFound(PRODUCT_PRICE_NOT_FOUND, product.getGuid()));

		final Collection<DiscountRecord> discountRecords = price.getDiscountRecords();

		final Set<Long> appliedRules = ImmutableSet.copyOf(
				Iterables.transform(discountRecords, createDiscountRecordAppliedRulesFunction()));

		return new LowestPrice(price, appliedRules);
	}

	@Override
	@CacheResult
	public ExecutionResult<Boolean> priceExists(final String storeCode, final String itemId) {
		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {

				final String skuCode = Assign.ifSuccessful(itemRepository.getSkuCodeForItemId(itemId));
				final Price price = getPrice(storeCode, skuCode).getData();

				final boolean priceExists = price != null;

				return ExecutionResultFactory.createReadOK(priceExists);
			}
		}.execute();
	}

	/**
	 * Factory method for creating a function to transform from a {@link DiscountRecord} to an applied rule ID.
	 *
	 * @return a function to transform from a {@link DiscountRecord} to an applied rule ID.
	 */
	protected Function<? super DiscountRecord, Long> createDiscountRecordAppliedRulesFunction() {
		return new DiscountRecordToAppliedRuleIdFunction();
	}

	/**
	 * A function to transform from a {@link DiscountRecord} to an applied rule ID.
	 */
	private static final class DiscountRecordToAppliedRuleIdFunction implements Function<DiscountRecord, Long> {
		@Override
		public Long apply(final DiscountRecord input) {
			return input.getRuleId();
		}
	}

}
