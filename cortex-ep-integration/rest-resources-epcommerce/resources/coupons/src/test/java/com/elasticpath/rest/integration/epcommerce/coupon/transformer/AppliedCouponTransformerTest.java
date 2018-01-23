/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.coupon.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.rest.definition.coupons.CouponEntity;

/**
 * Unit Tests for {@link AppliedCouponTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppliedCouponTransformerTest {

	private static final String COUPON_CODE = "COUPON_CODE";

	@Mock
	private AppliedCoupon appliedCoupon;
	
	@InjectMocks
	private AppliedCouponTransformer transformer;
	
	@Before
	public void setUp() {
		when(appliedCoupon.getCouponCode()).thenReturn(COUPON_CODE);
	}
	
	@Test
	public void testCouponCodeOnEntityIsTransformedFromCouponCodeOnCoupon() {
		CouponEntity entity = transformer.transformToEntity(appliedCoupon);
		
		String couponCode = entity.getCode();
		assertEquals("coupon code should be " + COUPON_CODE, COUPON_CODE, couponCode);
	}
	
	@Test
	public void testCouponIdOnCouponEntityIsTransformedFromCouponCodeOnCoupon() {
		CouponEntity entity = transformer.transformToEntity(appliedCoupon);
		
		String couponId = entity.getCouponId();
		assertEquals("coupon id should be " + COUPON_CODE, COUPON_CODE, couponId);
	}

}
