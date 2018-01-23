/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.coupon.transformer;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transform CE coupon to coupon entity.
 */
@Named("couponTransformer")
@Singleton
public class CouponTransformer extends AbstractDomainTransformer<Coupon, CouponEntity> {

	@Override
	public Coupon transformToDomain(final CouponEntity resourceEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public CouponEntity transformToEntity(final Coupon coupon, final Locale locale) {
		return CouponEntity.builder()
				.withCode(coupon.getCouponCode())
				.withCouponId(coupon.getCouponCode())
				.build();
	}

}
