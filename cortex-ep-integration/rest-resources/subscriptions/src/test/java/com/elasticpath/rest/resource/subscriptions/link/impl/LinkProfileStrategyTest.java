/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.subscriptions.link.impl;

import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.resource.subscriptions.rel.SubscriptionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests for {@link com.elasticpath.rest.resource.subscriptions.link.impl.LinkProfileStrategy}.
 */
public class LinkProfileStrategyTest {

	private static final String RESOURCE_NAME = "subscriptions";
	private static final String SCOPE = "scope";

	private final LinkProfileStrategy strategy = new LinkProfileStrategy(RESOURCE_NAME);

	/**
	 * Tests creating a link for profile.
	 */
	@Test
	public void testCreateLinksForProfile() {
		ResourceState<ProfileEntity> profileRepresentation = ResourceState.Builder.create(ProfileEntity.builder().build())
				.withScope(SCOPE)
				.build();

		Collection<ResourceLink> links = strategy.getLinks(profileRepresentation);

		assertThat("LinkProfileStrategy is expected to create one and only one link", links, Matchers.hasSize(1));

		String expectedURI = URIUtil.format(RESOURCE_NAME, SCOPE);
		ResourceLink expectedLink = ResourceLinkFactory.createNoRev(expectedURI,
				CollectionsMediaTypes.LINKS.id(),
				SubscriptionResourceRels.SUBSCRIPTIONS_REL);

		assertThat("The expected link should be contained within the collection of links.", links, Matchers.hasItem(expectedLink));
	}

}
