/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Coupon;

/**
 * This predicates matches Applied Rules that contain the same rule code
 * as the coupon configs rule code.
 */
public class CouponAppliedRulePredicate implements RulePredicate<AppliedRule> {
	private final Coupon coupon;

	/**
	 * Constructor.
	 * @param coupon coupon which to check applied rule against.
	 */
	public CouponAppliedRulePredicate(final Coupon coupon) {
		this.coupon = coupon;
	}

	@Override
	public boolean isSatisfied(final AppliedRule rule) {
		for (AppliedCoupon appliedCoupon : rule.getAppliedCoupons()) {
			if (appliedCoupon.getCouponCode().equals(coupon.getCouponCode())) {
				return true;
			}
		}
		return false;
	}
}
