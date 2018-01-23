/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.coupon.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.coupons.integration.OrderCouponsLookupStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Lookup strategy for coupons.
 */
@Singleton
@Named("orderCouponsLookupStrategy")
public class OrderCouponsLookupStrategyImpl implements OrderCouponsLookupStrategy {

	private final CartOrderRepository cartOrderRepository;

	private final CouponRepository couponRepository;

	private final AbstractDomainTransformer<Coupon, CouponEntity> couponTransformer;

	/**
	 * Constructor.
	 * @param cartOrderRepository cart order repository.
	 * @param couponRepository coupon repository
	 * @param couponTransformer coupon transformer.
	 */
	@Inject
	public OrderCouponsLookupStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("couponRepository")
			final CouponRepository couponRepository, 
			@Named("couponTransformer")
			final AbstractDomainTransformer<Coupon, CouponEntity> couponTransformer) {
		this.cartOrderRepository = cartOrderRepository;
		this.couponRepository = couponRepository;
		this.couponTransformer = couponTransformer;
	}

	@Override
	public ExecutionResult<CouponEntity> getCouponDetailsForOrder(final String scope, final String cartOrderGuid, final String decodedCouponId) {

		Collection<String> couponCodes = Assign.ifSuccessful(findCouponIdsForOrder(scope, cartOrderGuid));
		if (couponCodes.contains(decodedCouponId)) {
			Coupon coupon = Assign.ifSuccessful(couponRepository.findByCouponCode(decodedCouponId));
			CouponEntity couponEntity = couponTransformer.transformToEntity(coupon);
			return ExecutionResultFactory.createReadOK(couponEntity);
		}
		return ExecutionResultFactory.createNotFound("Coupon is not found for order.");
	}

	@Override
	public ExecutionResult<Collection<String>> findCouponIdsForOrder(final String storeCode, final String cartOrderGuid) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		Collection<String> couponCodes = cartOrder.getCouponCodes();
		return ExecutionResultFactory.createReadOK(couponCodes);
	}
}
