/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.promotions.integration.AppliedPurchaseCouponPromotionsLookupStrategy;

/**
 * Looks up promotions associated with a coupon.
 */
@Singleton
@Named("appliedPurchaseCouponPromotionsLookupStrategy")
public class AppliedPurchaseCouponPromotionsLookupStrategyImpl implements AppliedPurchaseCouponPromotionsLookupStrategy {

	private final OrderRepository orderRepository;
	private final CouponRepository couponRepository;
	private final PromotionRepository promotionRepository;

	/**
	 * Constructs.
	 * @param orderRepository orderRepository.
	 * @param couponRepository couponRepository.
	 * @param promotionRepository promotionRepository.
	 */
	@Inject
	public AppliedPurchaseCouponPromotionsLookupStrategyImpl(
			@Named("orderRepository")
			final OrderRepository orderRepository,
			@Named("couponRepository")
			final CouponRepository couponRepository,
			@Named("promotionRepository")
			final PromotionRepository promotionRepository) {
		this.orderRepository = orderRepository;
		this.couponRepository = couponRepository;
		this.promotionRepository = promotionRepository;
	}

	@Override
	public ExecutionResult<Collection<String>> getAppliedPromotionsForCoupon(final String scope, final String decodedCouponId,
																			final String decodedPurchaseId) {
		Order order = Assign.ifSuccessful(orderRepository.findByGuid(scope, decodedPurchaseId));

		Coupon coupon = Assign.ifSuccessful(couponRepository.findByCouponCode(decodedCouponId));

		Collection<String> appliedPromotions
				= promotionRepository.getAppliedPromotionsForCoupon(order, coupon);
		return ExecutionResultFactory.createReadOK(appliedPromotions);
	}
}
