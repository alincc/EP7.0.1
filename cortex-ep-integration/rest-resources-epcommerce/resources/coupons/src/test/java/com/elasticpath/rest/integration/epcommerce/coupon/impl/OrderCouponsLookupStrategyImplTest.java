/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.coupon.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Test Lookup strategy for coupons {@link OrderCouponsLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderCouponsLookupStrategyImplTest {

	private static final String STORE_CODE = "STORE_CODE";

	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";

	private static final String COUPON_CODE = "COUPON_CODE";

	private static final String COUPON_CODE2 = "COUPON_CODE2";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private AbstractDomainTransformer<Coupon, CouponEntity> couponTransformer;

	@InjectMocks
	public OrderCouponsLookupStrategyImpl lookup;

	private final Set<String> couponCodes = new HashSet<>();

	@Test
	public void testSuccessfulGetCouponDetails() {
		couponCodes.add(COUPON_CODE);
		allowingCartOrderToReturnCouponCodes();
		Coupon coupon = mock(Coupon.class);
		when(couponRepository.findByCouponCode(COUPON_CODE)).thenReturn(ExecutionResultFactory.createReadOK(coupon));
		CouponEntity couponEntity = mock(CouponEntity.class);
		when(couponTransformer.transformToEntity(coupon)).thenReturn(couponEntity);
		
		ExecutionResult<CouponEntity> executionResult = lookup.getCouponDetailsForOrder(STORE_CODE, CART_ORDER_GUID, COUPON_CODE);
		
		AssertExecutionResult.assertExecutionResult(executionResult)
			.isSuccessful()
			.data(couponEntity);		
	}
	
	@Test
	public void testGetCouponsDetailsNotFoundForCoupon() {
		allowingCartOrderToReturnCouponCodes();
		
		ExecutionResult<CouponEntity> executionResult = lookup.getCouponDetailsForOrder(STORE_CODE, CART_ORDER_GUID, COUPON_CODE);
		
		AssertExecutionResult.assertExecutionResult(executionResult)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testGetCouponDetailsNotFoundForOrder() {
		couponCodes.add(COUPON_CODE);
		allowingCartOrderToReturnCouponCodes();
		when(couponRepository.findByCouponCode(COUPON_CODE)).thenReturn(ExecutionResultFactory.<Coupon>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookup.getCouponDetailsForOrder(STORE_CODE, CART_ORDER_GUID, COUPON_CODE);
	}

	@Test
	public void testFindingNoCouponIdsForOrder() {
		allowingCartOrderToReturnCouponCodes();
		
		ExecutionResult<Collection<String>> executionResult = lookup.findCouponIdsForOrder(STORE_CODE, CART_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(executionResult)
			.isSuccessful()
			.data(couponCodes);
	}

	@Test
	public void testFindingOneCouponIdForOrder() {
		couponCodes.add(COUPON_CODE);
		allowingCartOrderToReturnCouponCodes();
		ExecutionResult<Collection<String>> executionResult = lookup.findCouponIdsForOrder(STORE_CODE, CART_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(executionResult)
			.isSuccessful()
			.data(couponCodes);
	}

	@Test
	public void testFindingMultipleCouponIdsForOrder() {
		couponCodes.add(COUPON_CODE);
		couponCodes.add(COUPON_CODE2);
		allowingCartOrderToReturnCouponCodes();
		
		ExecutionResult<Collection<String>> executionResult = lookup.findCouponIdsForOrder(STORE_CODE, CART_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(executionResult)
			.isSuccessful()
			.data(couponCodes);
	}

	@Test
	public void testFindCouponIdsNotFoundForOrder() {
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(
				ExecutionResultFactory.<CartOrder> createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookup.findCouponIdsForOrder(STORE_CODE, CART_ORDER_GUID);
	}

	private void allowingCartOrderToReturnCouponCodes() {
		CartOrder cartOrder = mock(CartOrder.class);

		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		when(cartOrder.getCouponCodes()).thenReturn(couponCodes);
	}
}
