/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;

/**
 * The Interface NavigationRecommendationsLookupStrategy.
 */
public interface NavigationRecommendationsLookupStrategy {


	/**
	 * Get the recommendations related to navigation.
	 *
	 * @param scope the scope
	 * @param decodedNavigationId the decoded navigation ID
	 * @return the collection of {@link RecommendationsEntity}s
	 */
	ExecutionResult<Collection<RecommendationsEntity>> getRecommendations(String scope, String decodedNavigationId);

	/**
	 * Checks if the given navigation has recommendations.
	 *
	 * @param scope the scope
	 * @param decodedNavigationId the decoded navigation ID
	 * @return true if source object has recommendations.  false otherwise.
	 */
	ExecutionResult<Boolean> hasRecommendations(String scope, String decodedNavigationId);

	/**
	 * Get the recommended items for navigation from a particular recommendation group.
	 *
	 * @param scope the scope
	 * @param decodedNavigationId the decoded navigation ID
	 * @param recommendationGroup the recommendation group
	 * @param pageNumber the page number of items to retrieve
	 * @return the collection of recommended item IDs wrapped in a {@link PaginationDto}
	 */
	ExecutionResult<PaginationDto> getRecommendedItemsFromGroup(String scope, String decodedNavigationId,
																String recommendationGroup, int pageNumber);
}
