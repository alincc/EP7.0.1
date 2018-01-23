/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.transformer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.navigations.NavigationsMediaTypes;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.recommendations.impl.RecommendationsUriBuilderImpl;
import com.elasticpath.rest.resource.recommendations.rel.RecommendationsResourceRels;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilderFactory;
import com.elasticpath.rest.test.AssertResourceState;

/**
 * Tests for {@link RfoRecommendationGroupsTransformer}.
 */
public class RfoRecommendationGroupsTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "recommendations";
	private static final String RECOMMENDATIONS_NAME = "top100";
	private static final String RESOURCE_ID = "12345";
	private static final String SCOPE = "telcooperative";
	private static final String OTHER_URI = "/some/other/uri";

	private static final String SELF_URI =
			new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME).setSourceUri(OTHER_URI).build();

	private static final String LINK_URI =
			new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME).setSourceUri(OTHER_URI).setRecommendationGroup(RECOMMENDATIONS_NAME).build();

	private final ResourceState<ResourceEntity> navigation = createNavigationRepresentation();

	private RfoRecommendationGroupsTransformer recommendationsTransformer;

	@Before
	public void setUp() {
		RecommendationsUriBuilderFactory rfoRecommendationsUriBuilderFactory = mock(RecommendationsUriBuilderFactory.class);
		when(rfoRecommendationsUriBuilderFactory.get()).thenAnswer(invocation -> new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME));
		recommendationsTransformer = new RfoRecommendationGroupsTransformer(rfoRecommendationsUriBuilderFactory);
	}


	@Test
	public void testSelfWhenConvertingEntityToRepresentationWhenOtherSourceUriProvided() {
		Collection<RecommendationsEntity> recommendationsEntities = buildCollectionOfOneRecommendations(RECOMMENDATIONS_NAME);
		Self expectedSelf = SelfFactory.createSelf(SELF_URI);

		ResourceState<LinksEntity> actualState = recommendationsTransformer.transform(recommendationsEntities, navigation);

		AssertResourceState.assertResourceState(actualState)
			.resourceInfoMaxAge(RecommendationsResourceRels.MAX_AGE)
			.self(expectedSelf);
	}


	@Test
	public void testLinksWhenConvertingEntityToRepresentationWhenOtherSourceUriProvided() {
		Collection<RecommendationsEntity> recommendationsEntities = buildCollectionOfOneRecommendations(RECOMMENDATIONS_NAME);
		ResourceLink expectedLink = ResourceLinkFactory.createNoRev(LINK_URI, CollectionsMediaTypes.PAGINATED_LINKS.id(), RECOMMENDATIONS_NAME);

		ResourceState<LinksEntity> actualState = recommendationsTransformer.transform(recommendationsEntities, navigation);

		AssertResourceState.assertResourceState(actualState)
			.resourceInfoMaxAge(RecommendationsResourceRels.MAX_AGE)
			.containsLinks(expectedLink);
	}

	@Test(expected = IllegalStateException.class)
	public void testInvalidRecommendationsGroupNameReturnsException() {
		Collection<RecommendationsEntity> recommendationsEntities = buildCollectionOfOneRecommendations("InvalidName*&$");

		recommendationsTransformer.transform(recommendationsEntities, navigation);
	}

	private Collection<RecommendationsEntity> buildCollectionOfOneRecommendations(final String recommendationGroup) {
		RecommendationsEntity recommendationsEntity = RecommendationsEntity.builder()
														.withName(recommendationGroup)
														.withRecommendationsId(RESOURCE_ID)
														.build();
		return Collections.singletonList(recommendationsEntity);
	}

	private ResourceState<ResourceEntity> createNavigationRepresentation() {
		Self self = SelfFactory.createSelf(OTHER_URI, NavigationsMediaTypes.NAVIGATION.id());

		ResourceEntity navigationEntity = NavigationEntity.builder().build();
		return ResourceState.Builder.create(navigationEntity)
				.withSelf(self)
				.withScope(SCOPE)
				.build();
	}
}
