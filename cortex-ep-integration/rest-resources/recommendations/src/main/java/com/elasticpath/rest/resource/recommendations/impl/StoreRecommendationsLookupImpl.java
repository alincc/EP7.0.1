/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.impl;


import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.recommendations.StoreRecommendationsLookup;
import com.elasticpath.rest.resource.recommendations.integration.StoreRecommendationsLookupStrategy;
import com.elasticpath.rest.resource.recommendations.transformer.RecommendedItemsTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * Implementation of StoreRecommendationsLookup for accessing data from core.
 */
@Singleton
@Named("storeRecommendationsLookup")
public class StoreRecommendationsLookupImpl implements StoreRecommendationsLookup {

	private final RecommendedItemsTransformer recommendedItemsTransformer;
	private final TransformToResourceState<LinksEntity, Collection<RecommendationsEntity>> recommendationGroupsTransformer;
	private final StoreRecommendationsLookupStrategy storeRecommendationsLookupStrategy;

	/**
	 * Default constructor.
	 *
	 * @param recommendedItemsTransformer        the transformer for the list of recommended Items
	 * @param recommendationGroupsTransformer    the transformer for a list of recommendations groups
	 * @param storeRecommendationsLookupStrategy the purchase lookup strategy
	 */
	@Inject
	public StoreRecommendationsLookupImpl(
			@Named("recommendedItemsTransformer")
			final RecommendedItemsTransformer recommendedItemsTransformer,
			@Named("recommendationGroupsTransformer")
			final TransformToResourceState<LinksEntity, Collection<RecommendationsEntity>> recommendationGroupsTransformer,
			@Named("storeRecommendationsLookupStrategy")
			final StoreRecommendationsLookupStrategy storeRecommendationsLookupStrategy) {

		this.recommendedItemsTransformer = recommendedItemsTransformer;
		this.recommendationGroupsTransformer = recommendationGroupsTransformer;
		this.storeRecommendationsLookupStrategy = storeRecommendationsLookupStrategy;
	}


	@Override
	public ExecutionResult<ResourceState<LinksEntity>> getRecommendations(final String scope) {

		Collection<RecommendationsEntity> recommendationsEntities =
				Assign.ifSuccessful(storeRecommendationsLookupStrategy.getRecommendations(scope));

		ExecutionResult<ResourceState<LinksEntity>> executionResult;
		try {
			executionResult = ExecutionResultFactory.createReadOK(
					recommendationGroupsTransformer.transform(scope, recommendationsEntities));
		} catch (IllegalStateException exception) {
			executionResult = ExecutionResultFactory.createServerError(exception.getMessage());
		}

		return executionResult;
	}

	@Override
	public ExecutionResult<ResourceState<PaginatedLinksEntity>> getRecommendedItemsFromGroup(
			final String scope,
			final String recommendationGroup,
			final int pageNumber) {


		PaginationDto recommendedItems = Assign.ifSuccessful(
				storeRecommendationsLookupStrategy.getRecommendedItemsFromGroup(scope, recommendationGroup, pageNumber));

		ResourceState<PaginatedLinksEntity> paginatedItemLinks = recommendedItemsTransformer
				.transformToRepresentation(scope, recommendedItems, recommendationGroup);

		return ExecutionResultFactory.createReadOK(paginatedItemLinks);
	}

}
