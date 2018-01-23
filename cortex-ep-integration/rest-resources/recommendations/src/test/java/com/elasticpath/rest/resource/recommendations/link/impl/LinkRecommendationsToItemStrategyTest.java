/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.link.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.recommendations.impl.RecommendationsUriBuilderImpl;
import com.elasticpath.rest.resource.recommendations.rel.RecommendationsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;

/**
 * Test class for {@link LinkRecommendationsToItemStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class LinkRecommendationsToItemStrategyTest {

	private static final String SCOPE = "scope";
	private static final String RESOURCE_ID = "itemId";
	private static final String RESOURCE_URI = "/itemUri";
	private static final String RESOURCE_SERVER_NAME = "recomendations";

	@Mock
	private ResourceState<ItemEntity> item;

	@Mock
	private ItemEntity itemEntity;

	private final Self itemSelf = SelfFactory.createSelf(RESOURCE_URI, ItemsMediaTypes.ITEM.id());
	private LinkRecommendationsToItemStrategy itemLinkStrategy;


	@Before
	public void setUp() {
		when(item.getScope()).thenReturn(SCOPE);
		when(item.getSelf()).thenReturn(itemSelf);
		when(item.getEntity()).thenReturn(itemEntity);
		when(itemEntity.getItemId()).thenReturn(RESOURCE_ID);

		itemLinkStrategy = new LinkRecommendationsToItemStrategy(RESOURCE_SERVER_NAME);
	}


	@Test
	public void testAddRecommendationsLinkToItemsWhenLookupSuccessful() {
		ResourceLink expectedLink = ResourceLinkFactory.createNoRev(
				new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME).setSourceUri(RESOURCE_URI).build(),
				CollectionsMediaTypes.LINKS.id(),
				RecommendationsResourceRels.RECOMMENDATIONS_REL);

		Iterable<ResourceLink> result = itemLinkStrategy.getLinks(item);

		assertThat("The a link should have been created.", result, Matchers.hasItems(expectedLink));
	}
}
