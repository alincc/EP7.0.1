/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.coupon.impl;

import static com.elasticpath.rest.ResourceStatus.NOT_FOUND;
import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.coupons.integration.PurchaseCouponsLookupStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Coupons look up for purchases.
 */
@Singleton
@Named("purchaseCouponsLookupStrategy")
public class PurchaseCouponsLookupStrategyImpl implements PurchaseCouponsLookupStrategy {

	private final OrderRepository orderRepository;
	private final AbstractDomainTransformer<AppliedCoupon, CouponEntity> appliedCouponTransformer;

	/**
	 * Purchase coupons look up constructor.
	 *
	 * @param orderRepository   order repository.
	 * @param appliedCouponTransformer coupon transformer.
	 */
	@Inject
	public PurchaseCouponsLookupStrategyImpl(
			@Named("orderRepository")
			final OrderRepository orderRepository,
			@Named("appliedCouponTransformer")
			final AbstractDomainTransformer<AppliedCoupon, CouponEntity> appliedCouponTransformer) {
		this.orderRepository = orderRepository;
		this.appliedCouponTransformer = appliedCouponTransformer;
	}

	@Override
	public Iterable<String> getCouponsForPurchase(final String scope,
													final String decodedPurchaseId) {
		Collection<AppliedCoupon> appliedCoupons = getAppliedCoupons(scope, decodedPurchaseId);
		Collection<String> couponIds = new ArrayList<>();
		for (AppliedCoupon coupon: appliedCoupons) {
			couponIds.add(coupon.getCouponCode());
		}
		return couponIds;
	}

	@Override
	public ExecutionResult<CouponEntity> getCouponDetailsForPurchase(final String scope,
																	final String decodedPurchaseId,
																	final String decodedCouponId) {

		Collection<AppliedCoupon> appliedCoupons = getAppliedCoupons(scope, decodedPurchaseId);
		AppliedCoupon appliedCoupon =  findMatchingAppliedCoupon(appliedCoupons, decodedCouponId);

		if (appliedCoupon == null) {
			return ExecutionResultFactory.createNotFound("Coupon is not found for order.");
		}

		CouponEntity couponEntity = appliedCouponTransformer.transformToEntity(appliedCoupon);
		return ExecutionResultFactory.createReadOK(couponEntity);
	}

	private AppliedCoupon findMatchingAppliedCoupon(final Collection<AppliedCoupon> appliedCoupons, final String decodedCouponId) {
		for (AppliedCoupon coupon : appliedCoupons) {
			if (coupon.getCouponCode().equals(decodedCouponId)) {
				return coupon;
			}
		}
		return null;
	}

	private Collection<AppliedCoupon> getAppliedCoupons(final String scope, final String decodedPurchaseId) {
		ExecutionResult<Order> orderResult = orderRepository.findByGuid(scope, decodedPurchaseId);

		if (orderResult.getResourceStatus() == NOT_FOUND) {
			return newArrayList();
		}

		Order order = orderResult.getData();
		Collection<AppliedCoupon> appliedCoupons = new ArrayList<>();
		for (AppliedRule rule : order.getAppliedRules()) {
			for (AppliedCoupon coupon : rule.getAppliedCoupons()) {
				appliedCoupons.add(coupon);
			}
		}
		return appliedCoupons;
	}
}
