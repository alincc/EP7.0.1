/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
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
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.recommendations.ItemRecommendationsLookup;
import com.elasticpath.rest.resource.recommendations.integration.ItemRecommendationsLookupStrategy;
import com.elasticpath.rest.resource.recommendations.transformer.RfoRecommendedItemsTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Implementation of ItemRecommendationsLookup for accessing data from core.
 */
@Singleton
@Named("itemRecommendationsLookup")
public class ItemRecommendationsLookupImpl implements ItemRecommendationsLookup {

	private final RfoRecommendedItemsTransformer rfoRecommendedItemsTransformer;
	private final TransformRfoToResourceState<LinksEntity, Collection<RecommendationsEntity>, ItemEntity> rfoRecommendationGroupsTransformer;
	private final ItemRecommendationsLookupStrategy itemRecommendationsLookupStrategy;

	/**
	 * Default constructor.
	 *
	 * @param rfoRecommendedItemsTransformer     the transformer for the list of recommended Items
	 * @param rfoRecommendationGroupsTransformer the transformer for a list of item recommendations
	 * @param itemRecommendationsLookupStrategy  the recommendations lookup strategy
	 */
	@Inject
	public ItemRecommendationsLookupImpl(
			@Named("rfoRecommendedItemsTransformer")
			final RfoRecommendedItemsTransformer rfoRecommendedItemsTransformer,
			@Named("rfoRecommendationGroupsTransformer")
			final TransformRfoToResourceState<LinksEntity, Collection<RecommendationsEntity>, ItemEntity> rfoRecommendationGroupsTransformer,
			@Named("itemRecommendationsLookupStrategy")
			final ItemRecommendationsLookupStrategy itemRecommendationsLookupStrategy) {

		this.rfoRecommendedItemsTransformer = rfoRecommendedItemsTransformer;
		this.rfoRecommendationGroupsTransformer = rfoRecommendationGroupsTransformer;
		this.itemRecommendationsLookupStrategy = itemRecommendationsLookupStrategy;
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> getRecommendations(final ResourceState<ItemEntity> item) {

		String sourceId = item.getEntity().getItemId();
		String scope = item.getScope();
		Collection<RecommendationsEntity> recommendationEntities =
				Assign.ifSuccessful(itemRecommendationsLookupStrategy.getRecommendations(scope, sourceId));

		ExecutionResult<ResourceState<LinksEntity>> executionResult;
		try {
			executionResult = ExecutionResultFactory.createReadOK(
					rfoRecommendationGroupsTransformer.transform(recommendationEntities, item));
		} catch (IllegalStateException exception) {
			executionResult = ExecutionResultFactory.createServerError(exception.getMessage());
		}

		return executionResult;
	}

	@Override
	public ExecutionResult<ResourceState<PaginatedLinksEntity>> getRecommendedItemsFromGroup(final ResourceState<ItemEntity> itemEntityResourceState,
																							final String recommendationGroup, final int pageNumber) {


		String itemId = itemEntityResourceState.getEntity().getItemId();
		String scope = itemEntityResourceState.getScope();

		PaginationDto recommendedItems = Assign.ifSuccessful(
				itemRecommendationsLookupStrategy.getRecommendedItemsFromGroup(scope, itemId, recommendationGroup, pageNumber));

		ResourceState<PaginatedLinksEntity> paginatedItemLinks = rfoRecommendedItemsTransformer
				.transformToRepresentation(recommendedItems, itemEntityResourceState, recommendationGroup);

		return ExecutionResultFactory.createReadOK(paginatedItemLinks);
	}
}
