/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.link.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.google.common.collect.Iterables;

import org.mockito.Mock;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.resource.promotions.CartPromotionsLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test class for {@link LinkPromotionsToCartStrategy}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class LinkPromotionsToCartStrategyTest extends AbstractLinkAppliedPromotionsContractTest<CartEntity> {

	@Mock
	private CartPromotionsLookup cartPromotionsLookup;
	@Mock
	private ResourceState<CartEntity> resourceState;

	@Override
	public LinkPromotionsToCartStrategy createLinkStrategyUnderTest() {
		return new LinkPromotionsToCartStrategy(promotionsLinkCreator, cartPromotionsLookup);
	}

	@Test
	public void testCreateLinksWhenPossibleAndAppliedPromotionsPresent() {
		ResourceLink expectedAppliedPromotionsLink = buildExpectedAppliedPromotionsLink();
		ResourceLink expectedPossiblePromotionsLink = buildExpectedPossiblePromotionsLink();
		arrangeLookupToReturnPromotionsPresent();

		Iterable<ResourceLink> links = linkStrategy.getLinks(testRepresentation);


		assertEquals("Should contain both possible promotions and promotions links", 2, Iterables.size(links));
		assertTrue("Should contain possible promotions", Iterables.contains(links, expectedPossiblePromotionsLink));
		assertTrue("Should contain applied promotions", Iterables.contains(links, expectedAppliedPromotionsLink));
	}

	@Test
	public void testCreateLinksWhenPossiblePromotionsNotFoundFailure() {
		ResourceLink expectedPromotionsLink = buildExpectedAppliedPromotionsLink();
		arrangeLookupToReturnNotFoundFailureOnHasPossiblePromotions();

		Iterable<ResourceLink> links = linkStrategy.getLinks(testRepresentation);

		assertEquals("Result should only contain 1 link.", 1, Iterables.size(links));
		assertEquals("Result resource link does not match expected.", expectedPromotionsLink, links.iterator().next());
	}

	@Override
	ResourceState<CartEntity> createLinkingRepresentationUnderTest() {
		Self self = SelfFactory.createSelf(SOURCE_URI);
		when(resourceState.getSelf()).thenReturn(self);
		when(resourceState.getScope()).thenReturn(SCOPE);
		return resourceState;
	}

	private void arrangeLookupToReturnPromotionsPresent() {
		when(cartPromotionsLookup.cartHasPossiblePromotions(testRepresentation))
				.thenReturn(ExecutionResultFactory.createReadOK(true));
	}

	@Override
	protected void arrangeLookupToReturnNoPossiblePromotions() {
		when(cartPromotionsLookup.cartHasPossiblePromotions(testRepresentation))
				.thenReturn(ExecutionResultFactory.createReadOK(false));
	}

	private void arrangeLookupToReturnNotFoundFailureOnHasPossiblePromotions() {
		when(cartPromotionsLookup.cartHasPossiblePromotions(testRepresentation))
				.thenReturn(ExecutionResultFactory.<Boolean>createNotFound());
	}

}
