/**
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;

/**
 * The facade for pricing snapshot related operations.
 */
@Singleton
@Named("pricingSnapshotRepository")
public class PricingSnapshotRepositoryImpl implements PricingSnapshotRepository {

	private final PricingSnapshotService pricingSnapshotService;
	private final TaxSnapshotService taxSnapshotService;

	/**
	 * Constructor.
	 *
	 * @param pricingSnapshotService the pricing snapshot service
	 * @param taxSnapshotService the tax snapshot service
	 */
	@Inject
	public PricingSnapshotRepositoryImpl(
		@Named("pricingSnapshotService") final PricingSnapshotService pricingSnapshotService,
		@Named("taxSnapshotService") final TaxSnapshotService taxSnapshotService) {

		this.pricingSnapshotService = pricingSnapshotService;
		this.taxSnapshotService = taxSnapshotService;
	}

	@Override
	@CacheResult
	public ExecutionResult<ShoppingCartPricingSnapshot> getShoppingCartPricingSnapshot(final ShoppingCart shoppingCart) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				return ExecutionResultFactory.createReadOK(pricingSnapshotService.getPricingSnapshotForCart(shoppingCart));
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<ShoppingItemPricingSnapshot> getPricingSnapshotForOrderSku(final OrderSku orderSku) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				return ExecutionResultFactory.createReadOK(pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku));
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<ShoppingCartTaxSnapshot> getShoppingCartTaxSnapshot(final ShoppingCart shoppingCart) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				final ShoppingCartPricingSnapshot pricingSnapshot = Assign.ifSuccessful(getShoppingCartPricingSnapshot(shoppingCart));
				return ExecutionResultFactory.createReadOK(taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot));
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<ShoppingItemTaxSnapshot> getTaxSnapshotForOrderSku(final OrderSku orderSku) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				final ShoppingItemPricingSnapshot pricingSnapshot = Assign.ifSuccessful(getPricingSnapshotForOrderSku(orderSku));
				return ExecutionResultFactory.createReadOK(taxSnapshotService.getTaxSnapshotForOrderSku(orderSku, pricingSnapshot));
			}
		}.execute();
	}
}
