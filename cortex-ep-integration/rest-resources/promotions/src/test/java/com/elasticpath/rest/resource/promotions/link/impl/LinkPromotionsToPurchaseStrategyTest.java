/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.link.impl;

import static org.mockito.Mockito.when;

import org.mockito.Mock;

import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test class for {@link LinkPromotionsToPurchaseStrategy}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class LinkPromotionsToPurchaseStrategyTest extends AbstractLinkAppliedPromotionsContractTest<PurchaseEntity> {

	@Mock
	private ResourceState<PurchaseEntity> resourceState;

	@Override
	public LinkPromotionsToPurchaseStrategy createLinkStrategyUnderTest() {
		return new LinkPromotionsToPurchaseStrategy(promotionsLinkCreator);
	}

	@Override
	ResourceState<PurchaseEntity> createLinkingRepresentationUnderTest() {
		Self self = SelfFactory.createSelf(SOURCE_URI, PurchasesMediaTypes.PURCHASE.id());
		when(resourceState.getSelf()).thenReturn(self);
		when(resourceState.getScope()).thenReturn(SCOPE);
		return resourceState;
	}
}
