/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wiring;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.coupons.integration.OrderCouponWriterStrategy;
import com.elasticpath.rest.resource.coupons.integration.OrderCouponsLookupStrategy;
import com.elasticpath.rest.resource.coupons.integration.PurchaseCouponsLookupStrategy;
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;

/**
 * Tests coupons resource wiring.
 */
@ContextConfiguration
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases"})
public class CouponResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "orderCouponWriterStrategy")
	private OrderCouponWriterStrategy orderCouponWriterStrategy;

	@ReplaceWithMock(beanName = "orderCouponsLookupStrategy")
	private OrderCouponsLookupStrategy orderCouponsLookupStrategy;

	@ReplaceWithMock(beanName = "ordersUriBuilderFactory")
	private OrdersUriBuilderFactory ordersUriBuilderFactory;

	@ReplaceWithMock(beanName = "purchaseCouponsLookupStrategy")
	private PurchaseCouponsLookupStrategy purchaseCouponsLookupStrategy;

	@ReplaceWithMock(beanName = "purchaseUriBuilderFactory")
	private PurchaseUriBuilderFactory purchaseUriBuilderFactory;
}
