/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.link.impl;


import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * The Tests for LinkProfileStrategy.
 */
public final class LinkProfileStrategyTest {

	private static final String RESOURCE_NAME = "purchases";
	private static final String SCOPE = "mobee";

	private final LinkProfileStrategy strategy = new LinkProfileStrategy(RESOURCE_NAME);

	/**
	 * Tests creating a link for profile.
	 */
	@Test
	public void testCreateLinksForProfile() {
		Collection<ResourceLink> links = strategy.getLinks(createValidProfileRepresentation());

		assertThat("LinkProfileStrategy is expected to create one and only one link", links, Matchers.hasSize(1));
		String expectedURI = URIUtil.format(RESOURCE_NAME, SCOPE);
		ResourceLink expectedLink = ResourceLinkFactory.createNoRev(expectedURI, CollectionsMediaTypes.LINKS.id(),
				PurchaseResourceRels.PURCHASES_REL);
		assertThat("The expected link should be contained within the collection of links.", links, Matchers.hasItem(expectedLink));
	}

	private ResourceState<ProfileEntity> createValidProfileRepresentation() {
		return ResourceState.Builder
				.create(ProfileEntity.builder().build())
				.withScope(SCOPE)
				.build();
	}
}
