/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.promotions.integration.AppliedOrderCouponPromotionsLookupStrategy;

/**
 * Looks up promotions associated with a coupon.
 */
@Singleton
@Named("appliedOrderCouponPromotionsLookupStrategy")
public class AppliedOrderCouponPromotionsLookupStrategyImpl implements AppliedOrderCouponPromotionsLookupStrategy {

	private final CartOrderRepository cartOrderRepository;
	private final PricingSnapshotRepository pricingSnapshotRepository;
	private final PromotionRepository promotionRepository;
	private final CouponRepository couponRepository;

	/**
	 * Constructs.
	 * @param cartOrderRepository cartOrderRepository.
	 * @param pricingSnapshotRepository pricing snapshot repository
	 * @param promotionRepository promotionRepository.
	 * @param couponRepository couponRepository.
	 */
	@Inject
	public AppliedOrderCouponPromotionsLookupStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("pricingSnapshotRepository")
			final PricingSnapshotRepository pricingSnapshotRepository,
			@Named("promotionRepository")
			final PromotionRepository promotionRepository,
			@Named("couponRepository")
			final CouponRepository couponRepository) {
		this.cartOrderRepository = cartOrderRepository;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
		this.promotionRepository = promotionRepository;
		this.couponRepository = couponRepository;
	}

	@Override
	public ExecutionResult<Collection<String>> getAppliedPromotionsForCoupon(final String scope, final String decodedCouponId,
																			final String decodedCartOrderId) {

		ShoppingCart shoppingCart = Assign.ifSuccessful(
					cartOrderRepository.getEnrichedShoppingCart(scope, decodedCartOrderId, CartOrderRepository.FindCartOrder.BY_ORDER_GUID));

		ShoppingCartPricingSnapshot pricingSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart));

		Coupon coupon = Assign.ifSuccessful(couponRepository.findByCouponCode(decodedCouponId));

		Collection<String> appliedPromotions
				= promotionRepository.getAppliedPromotionsForCoupon(pricingSnapshot, coupon);
		return ExecutionResultFactory.createReadOK(appliedPromotions);
	}

}
