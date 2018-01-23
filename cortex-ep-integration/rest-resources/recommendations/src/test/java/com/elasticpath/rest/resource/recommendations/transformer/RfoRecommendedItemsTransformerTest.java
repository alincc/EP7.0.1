/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.pagination.transform.PaginatedLinksTransformer;
import com.elasticpath.rest.resource.recommendations.impl.RecommendationsUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilder;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilderFactory;

/**
 * Tests for {@link com.elasticpath.rest.resource.recommendations.transformer.RecommendedItemsTransformer}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PaginatedLinksTransformer.class)
public class RfoRecommendedItemsTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "recommendations";
	private static final String RECOMMENDATION_GROUP = "updownleftrightsells";
	private static final String SCOPE = "scope";
	private static final String OTHER_URI = "/some/other/uri";
	private final ResourceState<NavigationEntity> navigation = createNavigation();
	private final RecommendationsUriBuilder recommendationsUriBuilder = new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME);
	@Mock
	private RecommendationsUriBuilderFactory recommendationsUriBuilderFactory;
	@Mock
	private PaginatedLinksTransformer paginatedLinksTransformer;

	@Mock
	private ResourceState<PaginatedLinksEntity> expectedResourceState;

	private RfoRecommendedItemsTransformer rfoRecommendedItemsTransformer;

	@Before
	public void setUp() {
		when(recommendationsUriBuilderFactory.get()).thenReturn(recommendationsUriBuilder);
		rfoRecommendedItemsTransformer =  new RfoRecommendedItemsTransformerImpl(paginatedLinksTransformer, recommendationsUriBuilderFactory);
	}


	@Test
	public void testConvertingDtoToRepresentation() {
		PaginationDto paginationDto = ResourceTypeFactory.createResourceEntity(PaginationDto.class);
		String expectedURI = new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(OTHER_URI)
				.setRecommendationGroup(RECOMMENDATION_GROUP)
				.build();
		when(paginatedLinksTransformer.transformToResourceState(paginationDto, SCOPE, expectedURI))
				.thenReturn(expectedResourceState);

		ResourceState<PaginatedLinksEntity> resourceState =
				rfoRecommendedItemsTransformer.transformToRepresentation(paginationDto, navigation, RECOMMENDATION_GROUP);

		assertEquals("Representation Should Match Expected", expectedResourceState, resourceState);
	}

	private ResourceState<NavigationEntity> createNavigation() {
		Self self = SelfFactory.createSelf(OTHER_URI);
		return ResourceState.Builder.create(ResourceTypeFactory.createResourceEntity(NavigationEntity.class))
				.withSelf(self)
				.withScope(SCOPE)
				.build();
	}
}
