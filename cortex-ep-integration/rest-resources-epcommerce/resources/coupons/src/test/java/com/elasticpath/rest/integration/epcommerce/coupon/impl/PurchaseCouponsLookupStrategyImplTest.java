/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.coupon.impl;


import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Coupons look up for purchases.
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
@RunWith(MockitoJUnitRunner.class)
public class PurchaseCouponsLookupStrategyImplTest {

	private static final String SCOPE = "SCOPE";

	private static final String PURCHASE_ID = "PURCHASE_ID";

	private static final String COUPON_CODE = "COUPON_CODE";

	private static final String COUPON_CODE2 = "COUPON_CODE2";

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private AbstractDomainTransformer<AppliedCoupon, CouponEntity> appliedCouponTransformer;

	@InjectMocks
	public PurchaseCouponsLookupStrategyImpl lookupStrategy;

	@Mock
	private Order order;

	@Mock
	private AppliedRule rule;

	@Mock
	private AppliedCoupon appliedCoupon;

	@Mock
	private AppliedCoupon appliedCoupon2;

	private final Set<AppliedCoupon> appliedCoupons = new HashSet<>();

	@Before
	public void setUp() {
		setUpAppliedRulesForOrder();
		setUpAppliedCouponCodes();
		setUpAppliedCouponsForRule();
	}

	@Test
	public void testGetNoCouponsForAPurchase() {
		ExecutionResult<Order> orderResult = ExecutionResultFactory.createReadOK(order);
		setUpToFindOrder(orderResult);

		Iterable<String> couponIds = lookupStrategy.getCouponsForPurchase(SCOPE, PURCHASE_ID);

		assertTrue("No coupons should be returned for purchase.", isEmpty(couponIds));
	}

	@Test
	public void testGetOneCouponForAPurchase() {
		ExecutionResult<Order> orderResult = ExecutionResultFactory.createReadOK(order);
		setUpToFindOrder(orderResult);
		appliedCoupons.add(appliedCoupon);

		Iterable<String> couponIds = lookupStrategy.getCouponsForPurchase(SCOPE, PURCHASE_ID);

		assertEquals("Coupon should be " + COUPON_CODE, 1, size(couponIds));
		assertTrue("Coupon should be " + COUPON_CODE, contains(couponIds, COUPON_CODE));
	}

	@Test
	public void testGettingManyCouponsForAPurchase() {
		ExecutionResult<Order> orderResult = ExecutionResultFactory.createReadOK(order);
		setUpToFindOrder(orderResult);
		appliedCoupons.add(appliedCoupon);
		appliedCoupons.add(appliedCoupon2);

		Iterable<String> couponIds = lookupStrategy.getCouponsForPurchase(SCOPE, PURCHASE_ID);

		assertEquals("Coupon should be " + COUPON_CODE, 2, size(couponIds));
		assertTrue("Coupon should be " + COUPON_CODE, contains(couponIds, COUPON_CODE));
		assertTrue("Coupon should be " + COUPON_CODE, contains(couponIds, COUPON_CODE2));
	}

	@Test
	public void testGetCouponsNotFoundForOrder() {
		ExecutionResult<Order> orderResult = ExecutionResultFactory.createNotFound();
		setUpToFindOrder(orderResult);

		Iterable<String> couponIds = lookupStrategy.getCouponsForPurchase(SCOPE, PURCHASE_ID);

		assertTrue("Coupon should be empty", isEmpty(couponIds));
	}

	@Test
	public void testSuccessfulFindCouponDetailsForPurchase() {
		CouponEntity couponEntity = mock(CouponEntity.class);
		ExecutionResult<Order> orderResult = ExecutionResultFactory.createReadOK(order);
		setUpToFindOrder(orderResult);
		appliedCoupons.add(appliedCoupon);

		when(appliedCouponTransformer.transformToEntity(appliedCoupon)).thenReturn(couponEntity);

		ExecutionResult<CouponEntity> executionResult = lookupStrategy.getCouponDetailsForPurchase(SCOPE, PURCHASE_ID, COUPON_CODE);

		AssertExecutionResult.assertExecutionResult(executionResult)
				.isSuccessful()
				.data(couponEntity);
	}

	@Test
	public void testFindCouponDetailsNotFoundForCoupon() {
		ExecutionResult<Order> orderResult = ExecutionResultFactory.createReadOK(order);
		setUpToFindOrder(orderResult);

		ExecutionResult<CouponEntity> executionResult = lookupStrategy.getCouponDetailsForPurchase(SCOPE, PURCHASE_ID, COUPON_CODE);

		AssertExecutionResult.assertExecutionResult(executionResult)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	private void setUpToFindOrder(final ExecutionResult<Order> orderResult) {
		when(orderRepository.findByGuid(SCOPE, PURCHASE_ID)).thenReturn(orderResult);
	}

	private void setUpAppliedRulesForOrder() {
		Set<AppliedRule> appliedRules = new HashSet<>();
		appliedRules.add(rule);
		when(order.getAppliedRules()).thenReturn(appliedRules);
	}

	private void setUpAppliedCouponsForRule() {
		when(rule.getAppliedCoupons()).thenReturn(appliedCoupons);
	}

	private void setUpAppliedCouponCodes() {
		when(appliedCoupon.getCouponCode()).thenReturn(COUPON_CODE);
		when(appliedCoupon2.getCouponCode()).thenReturn(COUPON_CODE2);
	}
}
