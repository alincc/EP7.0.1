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
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.recommendations.NavigationRecommendationsLookup;
import com.elasticpath.rest.resource.recommendations.integration.NavigationRecommendationsLookupStrategy;
import com.elasticpath.rest.resource.recommendations.transformer.RfoRecommendedItemsTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Implementation of NavigationRecommendationsLookup for accessing data from core.
 */
@Singleton
@Named("navigationRecommendationsLookup")
public class NavigationRecommendationsLookupImpl implements NavigationRecommendationsLookup {

	private final RfoRecommendedItemsTransformer rfoRecommendedItemsTransformer;
	private final TransformRfoToResourceState<LinksEntity, Collection<RecommendationsEntity>, NavigationEntity>
			rfoRecommendationGroupsTransformer;
	private final NavigationRecommendationsLookupStrategy navigationRecommendationsLookupStrategy;

	/**
	 * Default constructor.
	 *
	 * @param rfoRecommendedItemsTransformer          the transformer for the list of recommended Items
	 * @param rfoRecommendationGroupsTransformer      the transformer for a list of navigation recommendations
	 * @param navigationRecommendationsLookupStrategy the recommendations lookup strategy
	 */
	@Inject
	public NavigationRecommendationsLookupImpl(
			@Named("rfoRecommendedItemsTransformer")
			final RfoRecommendedItemsTransformer rfoRecommendedItemsTransformer,
			@Named("rfoRecommendationGroupsTransformer")
			final TransformRfoToResourceState<LinksEntity, Collection<RecommendationsEntity>, NavigationEntity>
					rfoRecommendationGroupsTransformer,
			@Named("navigationRecommendationsLookupStrategy")
			final NavigationRecommendationsLookupStrategy navigationRecommendationsLookupStrategy) {

		this.rfoRecommendedItemsTransformer = rfoRecommendedItemsTransformer;
		this.rfoRecommendationGroupsTransformer = rfoRecommendationGroupsTransformer;
		this.navigationRecommendationsLookupStrategy = navigationRecommendationsLookupStrategy;
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> getRecommendations(final ResourceState<NavigationEntity> navigation) {

		String decodedSourceId = Base32Util.decode(navigation.getEntity().getNodeId());
		String scope = navigation.getScope();
		Collection<RecommendationsEntity> recommendationsEntities =
				Assign.ifSuccessful(navigationRecommendationsLookupStrategy.getRecommendations(scope, decodedSourceId));

		ExecutionResult<ResourceState<LinksEntity>> executionResult;
		try {
			executionResult = ExecutionResultFactory.createReadOK(
					rfoRecommendationGroupsTransformer.transform(recommendationsEntities, navigation));
		} catch (IllegalStateException exception) {
			executionResult = ExecutionResultFactory.createServerError(exception.getMessage());
		}

		return executionResult;
	}

	@Override
	public ExecutionResult<Boolean> hasRecommendations(final NavigationEntity navigation, final String scope) {

		String decodedSourceId = Base32Util.decode(navigation.getNodeId());
		Boolean hasRecommendation =
				Assign.ifSuccessful(navigationRecommendationsLookupStrategy.hasRecommendations(scope, decodedSourceId));
		return ExecutionResultFactory.createReadOK(hasRecommendation);
	}

	@Override
	public ExecutionResult<ResourceState<PaginatedLinksEntity>> getRecommendedItemsFromGroup(
			final ResourceState<NavigationEntity> navigationEntityResourceState, final String recommendationGroup, final int pageNumber) {


		String decodedNavigationId = Base32Util.decode(navigationEntityResourceState.getEntity().getNodeId());
		String scope = navigationEntityResourceState.getScope();

		PaginationDto recommendedItems = Assign.ifSuccessful(
				navigationRecommendationsLookupStrategy
						.getRecommendedItemsFromGroup(scope, decodedNavigationId, recommendationGroup, pageNumber));

		ResourceState<PaginatedLinksEntity> paginatedItemLinks = rfoRecommendedItemsTransformer
				.transformToRepresentation(recommendedItems, navigationEntityResourceState, recommendationGroup);

		return ExecutionResultFactory.createReadOK(paginatedItemLinks);
	}
}
