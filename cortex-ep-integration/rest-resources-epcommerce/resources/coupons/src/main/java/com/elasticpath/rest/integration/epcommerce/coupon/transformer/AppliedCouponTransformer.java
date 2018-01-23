/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.coupon.transformer;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transform CE applied coupon to coupon entity.
 */
@Named("appliedCouponTransformer")
@Singleton
public class AppliedCouponTransformer extends AbstractDomainTransformer<AppliedCoupon, CouponEntity> {

	@Override
	public AppliedCoupon transformToDomain(final CouponEntity resourceEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public CouponEntity transformToEntity(final AppliedCoupon appliedCoupon, final Locale locale) {
		return CouponEntity.builder()
				.withCode(appliedCoupon.getCouponCode())
				.withCouponId(appliedCoupon.getCouponCode())
				.build();
	}

}
