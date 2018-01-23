/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.recommendations.integration.NavigationRecommendationsLookupStrategy;
import com.elasticpath.rest.resource.recommendations.transformer.RfoRecommendedItemsTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Tests {@link NavigationRecommendationsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class NavigationRecommendationsLookupImplTest {

	private static final String SCOPE = "SCOPE";
	private static final String DECODED_SOURCE_ID =  "SOURCE_ID";
	private static final String SOURCE_ID = Base32Util.encode(DECODED_SOURCE_ID);
	public static final String RECOMMENDATION_GROUP = "updownleftrightsells";
	public static final int PAGE_NUM = 1;

	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<RecommendationsEntity>, NavigationEntity> recommendationGroupsTransformer;
	@Mock
	private RfoRecommendedItemsTransformer rfoRecommendedItemsTransformer;
	@Mock
	private NavigationRecommendationsLookupStrategy recommendationsLookupStrategy;
	@InjectMocks
	private NavigationRecommendationsLookupImpl recommendationsLookup;

	private final ResourceState<NavigationEntity> navigation = createNavigationRepresentation();
	@Mock
	ResourceState<PaginatedLinksEntity> paginatedLinksEntity;
	@Mock
	ResourceState<LinksEntity> mockLinksRepresentation;

	@Before
	public void setUp() {
		when(rfoRecommendedItemsTransformer.transformToRepresentation(any(PaginationDto.class), any(ResourceState.class), anyString()))
				.thenReturn(paginatedLinksEntity);

		when(recommendationGroupsTransformer.transform(
				Matchers.<Collection<RecommendationsEntity>>any(),
				anyNavigationEntity())).thenReturn(mockLinksRepresentation);
	}

	@Test
	public void testSuccessfullyRetrieveRecommendationsBySourceIdAndType() {
		when(recommendationsLookupStrategy.getRecommendations(SCOPE, DECODED_SOURCE_ID)).thenReturn(ExecutionResultFactory
						.<Collection<RecommendationsEntity>>createReadOK(Collections.singleton(mock(RecommendationsEntity.class))));

		ExecutionResult<ResourceState<LinksEntity>> result = recommendationsLookup.getRecommendations(navigation);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(mockLinksRepresentation);
	}

	@Test
	public void testHasRecommendations() {
		when(recommendationsLookupStrategy.hasRecommendations(SCOPE, DECODED_SOURCE_ID))
			.thenReturn(ExecutionResultFactory.createReadOK(true));

		ExecutionResult<Boolean> result = recommendationsLookup.hasRecommendations(navigation.getEntity(), navigation.getScope());

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK);

		assertTrue("Result should return boolean 'true'.", result.getData());
	}

	@Test
	public void testSuccessfullyFindingRecommendedItemsFromGroup() {
		PaginationDto mockEntity = mock(PaginationDto.class);
		when(recommendationsLookupStrategy.getRecommendedItemsFromGroup(SCOPE, DECODED_SOURCE_ID, RECOMMENDATION_GROUP, PAGE_NUM))
				.thenReturn(ExecutionResultFactory.createReadOK(mockEntity));

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				recommendationsLookup.getRecommendedItemsFromGroup(navigation, RECOMMENDATION_GROUP, PAGE_NUM);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(paginatedLinksEntity);
	}

	@Test
	public void testInvalidRecommendationsGroupNameReturnsServerError() {
		when(recommendationsLookupStrategy.getRecommendations(SCOPE, DECODED_SOURCE_ID)).thenReturn(ExecutionResultFactory
				.<Collection<RecommendationsEntity>>createReadOK(Collections.singleton(mock(RecommendationsEntity.class))));
		when(recommendationGroupsTransformer.transform(
				Matchers.<Collection<RecommendationsEntity>>any(), anyNavigationEntity()))
				.thenThrow(new IllegalStateException());

		ExecutionResult<ResourceState<LinksEntity>> result = recommendationsLookup.getRecommendations(navigation);

		assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	private ResourceState<NavigationEntity> createNavigationRepresentation() {
		NavigationEntity navigationEntity = NavigationEntity.builder()
				.withNodeId(SOURCE_ID)
				.build();
		return ResourceState.Builder.create(navigationEntity)
				.withScope(SCOPE)
				.build();
	}

	private ResourceState<NavigationEntity> anyNavigationEntity() {
		return any();
	}
}
