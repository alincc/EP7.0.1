/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.promotions.integration.AppliedCartPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.AppliedItemPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.AppliedOrderCouponPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.AppliedPurchaseCouponPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.AppliedPurchasePromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.AppliedShippingOptionPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.PossibleCartPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.PossibleItemPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.PromotionsLookupStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;

/**
 * Tests promotions bean wiring.
 */
@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class PromotionsResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "promotionsLookupStrategy")
	private PromotionsLookupStrategy promotionsLookupStrategy;

	@ReplaceWithMock(beanName = "appliedCartPromotionsLookupStrategy")
	private AppliedCartPromotionsLookupStrategy appliedCartPromotionsLookupStrategy;

	@ReplaceWithMock(beanName = "possibleCartPromotionsLookupStrategy")
	private PossibleCartPromotionsLookupStrategy possibleCartPromotionsLookupStrategy;

	@ReplaceWithMock(beanName = "appliedItemPromotionsLookupStrategy")
	private AppliedItemPromotionsLookupStrategy appliedItemPromotionsLookupStrategy;

	@ReplaceWithMock(beanName = "possibleItemPromotionsLookupStrategy")
	private PossibleItemPromotionsLookupStrategy possibleItemPromotionsLookupStrategy;

	@ReplaceWithMock(beanName = "appliedShippingOptionPromotionsLookupStrategy")
	private AppliedShippingOptionPromotionsLookupStrategy appliedShippingOptionPromotionsLookupStrategy;

	@ReplaceWithMock(beanName = "appliedPurchasePromotionsLookupStrategy")
	private AppliedPurchasePromotionsLookupStrategy appliedPurchasePromotionsLookupStrategy;

	@ReplaceWithMock(beanName = "appliedPurchaseCouponPromotionsLookupStrategy")
	private AppliedPurchaseCouponPromotionsLookupStrategy appliedPurchaseCouponPromotionsLookupStrategy;

	@ReplaceWithMock(beanName = "appliedOrderCouponPromotionsLookupStrategy")
	private AppliedOrderCouponPromotionsLookupStrategy appliedOrderCouponPromotionsLookupStrategy;
}
