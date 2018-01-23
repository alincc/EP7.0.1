/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.promotions.integration.AppliedCartPromotionsLookupStrategy;

/**
 * Service that provides lookup of applied line item promotions data from external systems.
 */
@Singleton
@Named("appliedCartPromotionsLookupStrategy")
public class AppliedCartPromotionsLookupStrategyImpl implements AppliedCartPromotionsLookupStrategy {

	private final PromotionRepository promotionRepository;
	private final CartOrderRepository cartOrderRepository;
	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Inject dependencies.
	 *
	 * @param promotionRepository promotion repository
	 * @param cartOrderRepository cart order repository
	 * @param pricingSnapshotRepository pricing snapshot repository
	 */
	@Inject
	public AppliedCartPromotionsLookupStrategyImpl(
			@Named("promotionRepository")
			final PromotionRepository promotionRepository,
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("pricingSnapshotRepository")
			final PricingSnapshotRepository pricingSnapshotRepository) {
		this.promotionRepository = promotionRepository;
		this.cartOrderRepository = cartOrderRepository;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Override
	public ExecutionResult<Collection<String>> getAppliedPromotionsForItemInCart(
			final String scope, final String decodedCartId, final String decodedLineItemId, final int quantity) {

		ShoppingCart shoppingCart = Assign.ifSuccessful(
					cartOrderRepository.getEnrichedShoppingCart(scope, decodedCartId, CartOrderRepository.FindCartOrder.BY_CART_GUID));
		ShoppingCartPricingSnapshot pricingSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart));

		Collection<String> appliedCartLineitemPromotions
				= promotionRepository.getAppliedCartLineitemPromotions(shoppingCart, pricingSnapshot, decodedLineItemId);
		return ExecutionResultFactory.createReadOK(appliedCartLineitemPromotions);
	}

	@Override
	public ExecutionResult<Collection<String>> getAppliedPromotionsForCart(final String scope, final String decodedCartId) {
		ShoppingCart shoppingCart = Assign.ifSuccessful(
					cartOrderRepository.getEnrichedShoppingCart(scope, decodedCartId, CartOrderRepository.FindCartOrder.BY_CART_GUID));
		ShoppingCartPricingSnapshot pricingSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart));
		Collection<String> appliedCartPromotions = promotionRepository.getAppliedCartPromotions(pricingSnapshot);
		return ExecutionResultFactory.createReadOK(appliedCartPromotions);
	}

}