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
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.pagination.transform.PaginatedLinksTransformer;
import com.elasticpath.rest.resource.recommendations.impl.RecommendationsUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilder;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilderFactory;

/**
 * Tests for {@link RecommendedItemsTransformer}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PaginatedLinksTransformer.class)
public class RecommendedItemsTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "recommendations";
	private static final String RECOMMENDATION_GROUP = "updownleftrightsells";
	private static final String SCOPE = "telcooperative";

	@Mock
	private RecommendationsUriBuilderFactory recommendationsUriBuilderFactory;
	@Mock
	private PaginatedLinksTransformer paginatedLinksTransformer;

	@Mock
	private ResourceState<PaginatedLinksEntity> expectedResourceState;

	private RecommendedItemsTransformer recommendedItemsTransformer;
	private final RecommendationsUriBuilder recommendationsUriBuilder = new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME);

	@Before
	public void setUp() {
		when(recommendationsUriBuilderFactory.get()).thenReturn(recommendationsUriBuilder);
		recommendedItemsTransformer =  new RecommendedItemsTransformerImpl(paginatedLinksTransformer, recommendationsUriBuilderFactory);
	}


	/**
	 * Tests converting a dto to representation.
	 */
	@Test
	public void testConvertingDtoToRepresentation() {
		PaginationDto paginationDto = ResourceTypeFactory.createResourceEntity(PaginationDto.class);
		String expectedURI = new RecommendationsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setScope(SCOPE)
				.setRecommendationGroup(RECOMMENDATION_GROUP)
				.build();

		when(paginatedLinksTransformer.transformToResourceState(paginationDto, SCOPE, expectedURI))
				.thenReturn(expectedResourceState);

		ResourceState<PaginatedLinksEntity> representation =
				recommendedItemsTransformer.transformToRepresentation(SCOPE, paginationDto, RECOMMENDATION_GROUP);

		assertEquals("Representation Should Match Expected", expectedResourceState, representation);
	}


}
