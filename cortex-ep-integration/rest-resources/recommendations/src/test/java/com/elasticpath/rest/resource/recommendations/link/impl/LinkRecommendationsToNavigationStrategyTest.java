/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.link.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.navigations.NavigationsMediaTypes;
import com.elasticpath.rest.resource.recommendations.NavigationRecommendationsLookup;
import com.elasticpath.rest.resource.recommendations.impl.RecommendationsUriBuilderImpl;
import com.elasticpath.rest.resource.recommendations.rel.RecommendationsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;

/**
 * Test class for (@link LinkRecommendationsToNavigationStrategy).
 */
@RunWith(MockitoJUnitRunner.class)
public final class LinkRecommendationsToNavigationStrategyTest {

	private static final String SCOPE = "scope";
	private static final String RESOURCE_ID = "Id";
	private static final String RESOURCE_URI = "/Uri";
	private static final String RESOURCE_SERVER_NAME = "recomendations";

	@Mock
	private NavigationRecommendationsLookup mockRecommendationsLookup;

	@Mock
	private ResourceState<NavigationEntity> navigation;

	@Mock
	private NavigationEntity navigationEntity;

	private final Self navigationSelf = SelfFactory.createSelf(RESOURCE_URI, NavigationsMediaTypes.NAVIGATION.id());
	private LinkRecommendationsToNavigationStrategy navigationLinkStrategy;


	@Before
	public void setUp() {

		when(navigation.getScope()).thenReturn(SCOPE);
		when(navigation.getSelf()).thenReturn(navigationSelf);
		when(navigation.getEntity()).thenReturn(navigationEntity);
		when(navigationEntity.getNodeId()).thenReturn(RESOURCE_ID);

		navigationLinkStrategy = new LinkRecommendationsToNavigationStrategy(RESOURCE_SERVER_NAME, mockRecommendationsLookup);
	}


	@Test
	public void testAddRecommendationsLinkToNavigationWhenLookupSuccessful() {
		ResourceLink expectedLink = ResourceLinkFactory.createNoRev(
				new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME).setSourceUri(RESOURCE_URI).build(),
				CollectionsMediaTypes.LINKS.id(),
				RecommendationsResourceRels.RECOMMENDATIONS_REL);
		when(mockRecommendationsLookup.hasRecommendations(navigation.getEntity(), navigation.getScope()))
				.thenReturn(ExecutionResultFactory.createReadOK(true));

		Iterable<ResourceLink> result = navigationLinkStrategy.getLinks(navigation);

		assertThat("The a link should have been created.", result, Matchers.hasItems(expectedLink));
	}


	@Test
	public void testAddRecommendationsLinkToNavigationWhenLookupReturnsFalse() {
		when(mockRecommendationsLookup.hasRecommendations(navigation.getEntity(), navigation.getScope()))
				.thenReturn(ExecutionResultFactory.createReadOK(false));

		Iterable<ResourceLink> result = navigationLinkStrategy.getLinks(navigation);

		assertFalse("The result should be an empty collection", result.iterator().hasNext());
	}


	@Test
	public void testAddRecommendationsLinkToNavigationWhenLookupReturnsNotFound() {
		when(mockRecommendationsLookup.hasRecommendations(navigationEntity, navigation.getScope()))
				.thenReturn(ExecutionResultFactory.<Boolean>createNotFound());

		Iterable<ResourceLink> result = navigationLinkStrategy.getLinks(navigation);

		assertFalse("The result should be an empty collection", result.iterator().hasNext());
	}
}
