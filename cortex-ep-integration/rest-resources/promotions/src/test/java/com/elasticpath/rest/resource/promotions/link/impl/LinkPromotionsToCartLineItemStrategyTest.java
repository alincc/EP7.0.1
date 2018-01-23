/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.link.impl;

import static org.mockito.Mockito.when;

import org.mockito.Mock;

import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test class for {@link com.elasticpath.rest.resource.promotions.link.impl.LinkPromotionsToCartLineItemStrategy}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class LinkPromotionsToCartLineItemStrategyTest	extends AbstractLinkAppliedPromotionsContractTest<LineItemEntity> {

	@Mock
	private ResourceState<LineItemEntity> resourceState;

	@Override
	LinkPromotionsToCartLineItemStrategy createLinkStrategyUnderTest() {
		return new LinkPromotionsToCartLineItemStrategy(promotionsLinkCreator);
	}

	@Override
	ResourceState<LineItemEntity> createLinkingRepresentationUnderTest() {
		Self self = SelfFactory.createSelf(SOURCE_URI);
		when(resourceState.getSelf()).thenReturn(self);
		when(resourceState.getScope()).thenReturn(SCOPE);
		return resourceState;
	}
}
