/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.coupon.specifications.PotentialCouponUse;
import com.elasticpath.domain.coupon.specifications.ValidCouponUseSpecification;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.test.AssertExecutionResult;
import com.elasticpath.service.rules.CouponService;

/**
 * The tests for {@link CouponRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CouponRepositoryImplTest {

	private static final String COUPON_CODE = "COUPON_CODE";
	private static final String STORE_CODE = "STORE_CODE";
	private static final String CUSTOMER_EMAIL = "CUSTOMER_EMAIL";
	
	@Mock
	private ValidCouponUseSpecification specification;

	@Mock
	private CouponService couponService;

	@InjectMocks
	public CouponRepositoryImpl couponRepository;

	@Test
	public void testCouponIsValidInStore() {
		setUpCouponServiceToReturnCoupon();
		setUpSpecificationToBeSatisfied(true);
		
		ExecutionResult<Boolean> executionResult = couponRepository.isCouponValidInStore(COUPON_CODE, STORE_CODE, CUSTOMER_EMAIL);
		
		AssertExecutionResult.assertExecutionResult(executionResult)
			.isSuccessful()
			.data(true);
	}

	@Test
	public void testCouponRuleIsInvalidInStore() {
		setUpCouponServiceToReturnCoupon();
		setUpSpecificationToBeSatisfied(false);
		
		ExecutionResult<Boolean> executionResult = couponRepository.isCouponValidInStore(COUPON_CODE, STORE_CODE, CUSTOMER_EMAIL);
		
		AssertExecutionResult.assertExecutionResult(executionResult)
			.isSuccessful()
			.data(false);
	}

	private Coupon setUpCouponServiceToReturnCoupon() {
		Coupon coupon = mock(Coupon.class);
		when(coupon.getCouponCode()).thenReturn(COUPON_CODE);
		when(couponService.findByCouponCode(COUPON_CODE)).thenReturn(coupon);
		return coupon;
	}
	

	private void setUpSpecificationToBeSatisfied(final boolean isSatisfied) {
		when(specification.isSatisfiedBy(any(PotentialCouponUse.class))).thenReturn(isSatisfied);
	}
	
	@Test
	public void testFindCouponCodeReturnsCoupon() {
		Coupon coupon = setUpCouponServiceToReturnCoupon();
		
		ExecutionResult<Coupon> executionResult = couponRepository.findByCouponCode(COUPON_CODE);
		
		AssertExecutionResult.assertExecutionResult(executionResult)
			.isSuccessful()
			.data(coupon);
	}
	
	@Test
	public void testFindCouponCodeReturnsNotFound() {
		when(couponService.findByCouponCode(COUPON_CODE)).thenReturn(null);
		
		ExecutionResult<Coupon> executionResult = couponRepository.findByCouponCode(COUPON_CODE);
		
		AssertExecutionResult.assertExecutionResult(executionResult)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}
}
