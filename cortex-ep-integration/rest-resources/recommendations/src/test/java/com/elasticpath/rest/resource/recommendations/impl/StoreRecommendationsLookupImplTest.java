/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
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
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.recommendations.integration.StoreRecommendationsLookupStrategy;
import com.elasticpath.rest.resource.recommendations.transformer.RecommendedItemsTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * Tests {@link StoreRecommendationsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class StoreRecommendationsLookupImplTest {

	private static final String SCOPE = "SCOPE";
	public static final String RECOMMENDATION_GROUP = "updownleftrightsells";
	public static final int PAGE_NUM = 1;

	@Mock
	private TransformToResourceState<LinksEntity, Collection<RecommendationsEntity>> recommendationGroupsTransformer;
	@Mock
	private RecommendedItemsTransformer recommendedItemsTransformer;
	@Mock
	private StoreRecommendationsLookupStrategy storeRecommendationsLookupStrategy;
	@InjectMocks
	private StoreRecommendationsLookupImpl recommendationsLookup;

	@Mock
	ResourceState<PaginatedLinksEntity> paginatedLinks;
	@Mock
	ResourceState<LinksEntity> mockLinksRepresentation;

	@Before
	public void setUp() {

		when(recommendedItemsTransformer.transformToRepresentation(anyString(), any(PaginationDto.class), anyString()))
				.thenReturn(paginatedLinks);

		when(recommendationGroupsTransformer.transform(anyString(), Matchers.<Collection<RecommendationsEntity>>any()))
				.thenReturn(mockLinksRepresentation);
	}


	@Test
	public void testSuccessfullyFindingStoreRecommendations() {
		RecommendationsEntity mockEntity = mock(RecommendationsEntity.class);
		when(storeRecommendationsLookupStrategy.getRecommendations(SCOPE))
				.thenReturn(ExecutionResultFactory.<Collection<RecommendationsEntity>>createReadOK(Collections.singleton(mockEntity)));

		ExecutionResult<ResourceState<LinksEntity>> result = recommendationsLookup.getRecommendations(SCOPE);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(mockLinksRepresentation);
	}

	@Test
	public void testSuccessfullyFindingRecommendedItemsFromGroup() {
		PaginationDto mockEntity = mock(PaginationDto.class);
		when(storeRecommendationsLookupStrategy.getRecommendedItemsFromGroup(SCOPE, RECOMMENDATION_GROUP, PAGE_NUM))
				.thenReturn(ExecutionResultFactory.createReadOK(mockEntity));

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				recommendationsLookup.getRecommendedItemsFromGroup(SCOPE, RECOMMENDATION_GROUP, PAGE_NUM);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(paginatedLinks);
	}

	@Test
	public void testInvalidRecommendationsGroupNameReturnsServerError() {
		when(storeRecommendationsLookupStrategy.getRecommendations(SCOPE)).thenReturn(ExecutionResultFactory
				.<Collection<RecommendationsEntity>>createReadOK(Collections.singleton(mock(RecommendationsEntity.class))));
		when(recommendationGroupsTransformer.transform(anyString(), Matchers.<Collection<RecommendationsEntity>>any()))
				.thenThrow(new IllegalStateException());

		ExecutionResult<ResourceState<LinksEntity>> result = recommendationsLookup.getRecommendations(SCOPE);

		assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}
}
