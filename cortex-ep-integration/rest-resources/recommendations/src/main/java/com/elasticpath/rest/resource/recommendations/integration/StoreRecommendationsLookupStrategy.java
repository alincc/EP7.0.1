/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;

/**
 * The Interface StoreRecommendationsLookupStrategy.
 */
public interface StoreRecommendationsLookupStrategy {

	/**
	 * Get recommendations for the given store.
	 * These are recommendations not associated with a source object (i.e: an item or navigation node).
	 *
	 * @param scope the scope
	 * @return the collection of root {@link RecommendationsEntity}s
	 */
	ExecutionResult<Collection<RecommendationsEntity>> getRecommendations(String scope);

	/**
	 * Get the recommended items for store from a particular recommendation group.
	 *
	 * @param scope the scope
	 * @param recommendationGroup the recommendation group
	 * @param pageNumber the page number of items to retrieve
	 * @return the collection of recommended item IDs wrapped in a {@link PaginationDto}
	 */
	ExecutionResult<PaginationDto> getRecommendedItemsFromGroup(String scope, String recommendationGroup, int pageNumber);

}
