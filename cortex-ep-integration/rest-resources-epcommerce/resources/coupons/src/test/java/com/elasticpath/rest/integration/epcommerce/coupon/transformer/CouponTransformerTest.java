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

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.definition.coupons.CouponEntity;

/**
 * Unit Tests for {@link CouponTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CouponTransformerTest {

	private static final String COUPON_CODE = "COUPON_CODE";

	@Mock
	private Coupon coupon;
	
	@InjectMocks
	private CouponTransformer transformer;
	
	@Before
	public void setUp() {
		when(coupon.getCouponCode()).thenReturn(COUPON_CODE);
	}
	
	@Test
	public void testCouponCodeOnEntityIsTransformedFromCouponCodeOnCoupon() {
		CouponEntity entity = transformer.transformToEntity(coupon);
		
		String couponCode = entity.getCode();
		assertEquals("coupon code should be " + COUPON_CODE, COUPON_CODE, couponCode);
	}
	
	@Test
	public void testCouponIdOnCouponEntityIsTransformedFromCouponCodeOnCoupon() {
		CouponEntity entity = transformer.transformToEntity(coupon);
		
		String couponId = entity.getCouponId();
		assertEquals("coupon id should be " + COUPON_CODE, COUPON_CODE, couponId);
	}

}
