/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.transformer;

import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.recommendations.impl.RecommendationsUriBuilderImpl;
import com.elasticpath.rest.resource.recommendations.rel.RecommendationsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilderFactory;
import com.elasticpath.rest.test.AssertResourceState;

/**
 * Tests for {@link RecommendationGroupsTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RecommendationGroupsTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "recommendations";
	private static final String RECOMMENDATION_GROUP = "updownleftrightsells";
	private static final String SCOPE = "telcooperative";

	@Mock
	private RecommendationsUriBuilderFactory recommendationsUriBuilderFactory;

	private RecommendationGroupsTransformer recommendationsTransformer;

	@Before
	public void setUp() {
		when(recommendationsUriBuilderFactory.get()).thenAnswer(invocation -> new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME));
		recommendationsTransformer =  new RecommendationGroupsTransformer(recommendationsUriBuilderFactory);
	}

	@Test
	public void testSelfCorrectWhenConvertingEntityToRepresentation() {
		Collection<RecommendationsEntity> recommendationsEntities = buildCollectionOfOneRecommendations(RECOMMENDATION_GROUP);
		Self expectedSelf = buildExpectedSelfForRootRead();

		ResourceState<LinksEntity> actualState = recommendationsTransformer.transform(SCOPE, recommendationsEntities);

		AssertResourceState.assertResourceState(actualState)
			.resourceInfoMaxAge(RecommendationsResourceRels.MAX_AGE)
			.linkCount(1)
			.self(expectedSelf);
	}

	@Test
	public void testLinksWhenCreationConvertingEntityToRepresentation() {
		Collection<RecommendationsEntity> recommendationsEntities = buildCollectionOfOneRecommendations(RECOMMENDATION_GROUP);
		ResourceLink expectedLink = buildExpectedRecommendationsLink();

		ResourceState<LinksEntity> actualState = recommendationsTransformer.transform(SCOPE, recommendationsEntities);

		AssertResourceState.assertResourceState(actualState)
			.resourceInfoMaxAge(RecommendationsResourceRels.MAX_AGE)
			.containsLinks(expectedLink);
	}

	@Test(expected = IllegalStateException.class)
	public void testInvalidRecommendationsGroupNameReturnsException() {
		Collection<RecommendationsEntity> recommendationsEntities = buildCollectionOfOneRecommendations("InvalidName*&$");

		recommendationsTransformer.transform(SCOPE, recommendationsEntities);
	}

	private Collection<RecommendationsEntity> buildCollectionOfOneRecommendations(final String recommendationGroup) {
		RecommendationsEntity recommendationsEntity = RecommendationsEntity.builder()
														.withName(recommendationGroup)
														.build();
		return Collections.singletonList(recommendationsEntity);
	}

	private ResourceLink buildExpectedRecommendationsLink() {
		String uri = new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setScope(SCOPE)
				.setRecommendationGroup(RECOMMENDATION_GROUP)
				.build();
		return ResourceLinkFactory.createNoRev(uri, CollectionsMediaTypes.PAGINATED_LINKS.id(), RECOMMENDATION_GROUP);
	}

	private Self buildExpectedSelfForRootRead() {
		String uri = new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME).setScope(SCOPE).build();
		return SelfFactory.createSelf(uri);
	}
}
