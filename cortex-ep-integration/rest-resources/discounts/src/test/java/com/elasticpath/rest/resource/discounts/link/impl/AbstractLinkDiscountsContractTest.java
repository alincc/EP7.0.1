/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.link.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.discounts.DiscountsMediaTypes;
import com.elasticpath.rest.resource.discounts.impl.DiscountsUriBuilderImpl;
import com.elasticpath.rest.resource.discounts.rel.DiscountsResourceRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.DiscountsUriBuilderFactory;

/**
 * Contract Test for Promotions Linking Strategies.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractLinkDiscountsContractTest<E extends ResourceEntity> {

	static final String SOURCE_URI = "/source/uri";
	static final String RESOURCE_SERVER_NAME = "discounts";

	ResourceState<E> testRepresentation;

	@Mock
	DiscountsUriBuilderFactory discountsUriBuilderFactory;
	DiscountsLinkCreator discountsLinkCreator;
	ResourceStateLinkHandler<E> linkStrategy;

	@Before
	public void setUp() {

		when(discountsUriBuilderFactory.get()).thenAnswer(invocation -> new DiscountsUriBuilderImpl(RESOURCE_SERVER_NAME));
		discountsLinkCreator = new DiscountsLinkCreator(discountsUriBuilderFactory);
		linkStrategy = createLinkStrategyUnderTest();
		testRepresentation = createLinkingRepresentationUnderTest();
	}

	@Test
	public void testCreateLink() {

		ResourceLink expectedResourceLink = buildExpectedDiscountsLink();

		Iterable<ResourceLink> links = linkStrategy.getLinks(testRepresentation);

		assertEquals("Result should only contain 1 link.", 1, Iterables.size(links));
		assertEquals("Result resource link does not match expected.", expectedResourceLink, Iterables.getFirst(links, null));
	}

	ResourceLink buildExpectedDiscountsLink() {

		String expectedUri = new DiscountsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(SOURCE_URI)
				.build();
		return ResourceLinkFactory.createNoRev(expectedUri, DiscountsMediaTypes.DISCOUNT
				.id(), DiscountsResourceRels.DISCOUNT_REL);
	}

	abstract ResourceState<E> createLinkingRepresentationUnderTest();

	abstract ResourceStateLinkHandler<E> createLinkStrategyUnderTest();

}
