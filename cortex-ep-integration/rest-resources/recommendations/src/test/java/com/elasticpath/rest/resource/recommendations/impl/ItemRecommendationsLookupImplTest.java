/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.recommendations.integration.ItemRecommendationsLookupStrategy;
import com.elasticpath.rest.resource.recommendations.transformer.RfoRecommendedItemsTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Tests {@link ItemRecommendationsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemRecommendationsLookupImplTest {

	private static final String SCOPE = "SCOPE";
	private static final String SOURCE_ID = "SOURCE_ID";
	public static final String RECOMMENDATION_GROUP = "updownleftrightsells";
	public static final int PAGE_NUM = 1;

	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<RecommendationsEntity>, ItemEntity> recommendationGroupsTransformer;
	@Mock
	private RfoRecommendedItemsTransformer recommendedItemsTransformer;
	@Mock
	private ItemRecommendationsLookupStrategy recommendationsLookupStrategy;
	@InjectMocks
	private ItemRecommendationsLookupImpl recommendationsLookup;

	private final ResourceState<ItemEntity> item = createItemRepresentation();
	@Mock
	ResourceState<PaginatedLinksEntity> paginatedLinks;
	@Mock
	ResourceState<LinksEntity> mockLinksRepresentation;

	@Before
	public void setUp() {
		when(recommendedItemsTransformer.transformToRepresentation(any(PaginationDto.class), any(ResourceState.class), anyString()))
				.thenReturn(paginatedLinks);

		when(recommendationGroupsTransformer.transform(
				Matchers.<Collection<RecommendationsEntity>>any(), eq(item))).thenReturn(mockLinksRepresentation);
	}

	@Test
	public void testSuccessfullyRetrieveRecommendationsBySourceIdAndType() {
		when(recommendationsLookupStrategy.getRecommendations(SCOPE, SOURCE_ID)).thenReturn(ExecutionResultFactory
					.<Collection<RecommendationsEntity>>createReadOK(Collections.singleton(mock(RecommendationsEntity.class))));

		ExecutionResult<ResourceState<LinksEntity>> result = recommendationsLookup.getRecommendations(item);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(mockLinksRepresentation);
	}

	@Test
	public void testSuccessfullyFindingRecommendedItemsFromGroup() {
		PaginationDto mockEntity = mock(PaginationDto.class);
		when(recommendationsLookupStrategy.getRecommendedItemsFromGroup(SCOPE, SOURCE_ID, RECOMMENDATION_GROUP, PAGE_NUM))
				.thenReturn(ExecutionResultFactory.createReadOK(mockEntity));

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				recommendationsLookup.getRecommendedItemsFromGroup(item, RECOMMENDATION_GROUP, PAGE_NUM);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(paginatedLinks);
	}

	@Test
	public void testInvalidRecommendationsGroupNameReturnsServerError() {
		when(recommendationsLookupStrategy.getRecommendations(SCOPE, SOURCE_ID)).thenReturn(ExecutionResultFactory
				.<Collection<RecommendationsEntity>>createReadOK(Collections.singleton(mock(RecommendationsEntity.class))));
		when(recommendationGroupsTransformer.transform(
				Matchers.<Collection<RecommendationsEntity>>any(), eq(item)))
						.thenThrow(new IllegalStateException());

		ExecutionResult<ResourceState<LinksEntity>> result = recommendationsLookup.getRecommendations(item);

		assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	private ResourceState<ItemEntity> createItemRepresentation() {
		ItemEntity itemEntity = ItemEntity.builder()
				.withItemId(SOURCE_ID)
				.build();
		return ResourceState.Builder.create(itemEntity)
				.withScope(SCOPE)
				.build();
	}
}
