/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.link.impl;

import static org.mockito.Mockito.when;

import org.mockito.Mock;

import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test class for {@link LinkPromotionsToCouponStrategy}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class LinkPromotionsToCouponStrategyTest extends AbstractLinkAppliedPromotionsContractTest<CouponEntity> {

	@Mock
	private ResourceState<CouponEntity> resourceState;

	@Override
	ResourceStateLinkHandler<CouponEntity> createLinkStrategyUnderTest() {
		return new LinkPromotionsToCouponStrategy(promotionsLinkCreator);
	}

	@Override
	ResourceState<CouponEntity> createLinkingRepresentationUnderTest() {
		Self self = SelfFactory.createSelf(SOURCE_URI);
		when(resourceState.getSelf()).thenReturn(self);
		when(resourceState.getScope()).thenReturn(SCOPE);
		return resourceState;
	}
}
