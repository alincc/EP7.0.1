/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.link.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.promotions.AppliedPromotions;
import com.elasticpath.rest.resource.promotions.PossiblePromotions;
import com.elasticpath.rest.resource.promotions.impl.PromotionsUriBuilderImpl;
import com.elasticpath.rest.resource.promotions.rel.PromotionsResourceRels;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;

/**
 * Contract Test for Promotions Linking Strategies.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractLinkAppliedPromotionsContractTest<R extends ResourceEntity> {

	static final String SOURCE_URI = "/source/uri";
	static final String RESOURCE_SERVER_NAME = "promotions";
	static final String SCOPE = "scope";

	ResourceState<R> testRepresentation;

	@Mock
	PromotionsUriBuilderFactory promotionsUriBuilderFactory;
	PromotionsLinkCreator promotionsLinkCreator;
	ResourceStateLinkHandler<R> linkStrategy;

	@Before
	public void setUp() {
		when(promotionsUriBuilderFactory.get()).thenAnswer(invocation -> new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME));
		promotionsLinkCreator = new PromotionsLinkCreator(promotionsUriBuilderFactory);
		linkStrategy = createLinkStrategyUnderTest();
		testRepresentation = createLinkingRepresentationUnderTest();
	}

	@Test
	public void testCreateLinksWhenOnlyAppliedPromotionsPresent() {
		ResourceLink expectedResourceLink = buildExpectedAppliedPromotionsLink();
		arrangeLookupToReturnNoPossiblePromotions();
		Iterable<ResourceLink> links = linkStrategy.getLinks(testRepresentation);

		assertEquals("Result should only contain 1 link.", 1, Iterables.size(links));
		assertEquals("Result resource link does not match expected.", expectedResourceLink, links.iterator().next());
	}

	ResourceLink buildExpectedAppliedPromotionsLink() {
		String expectedUri = new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(getSourceUri())
				.setPromotionType(AppliedPromotions.URI_PART)
				.build();
		return ResourceLinkFactory.createNoRev(expectedUri, CollectionsMediaTypes.LINKS.id(), PromotionsResourceRels.PROMOTIONS_REL);
	}

	ResourceLink buildExpectedPossiblePromotionsLink() {
		String expectedUri = new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(getSourceUri())
				.setPromotionType(PossiblePromotions.URI_PART)
				.build();
		return ResourceLinkFactory.createNoRev(expectedUri, CollectionsMediaTypes.LINKS.id(), PromotionsResourceRels.POSSIBLE_REL);
	}

	protected String getSourceUri() {
		return SOURCE_URI;
	}

	abstract ResourceState<R> createLinkingRepresentationUnderTest();

	abstract ResourceStateLinkHandler<R> createLinkStrategyUnderTest();

	protected void arrangeLookupToReturnNoPossiblePromotions() {
		//default is to do nothing. Override to arrange possible promotions to not be present.
	}
}
