/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;

/**
 * The Interface ItemRecommendationsLookupStrategy.
 */
public interface ItemRecommendationsLookupStrategy {
	/**
	 * Get the recommendations related to item.
	 *
	 * @param scope the scope
	 * @param itemId the item ID
	 * @return the collection of {@link RecommendationsEntity}s
	 */
	ExecutionResult<Collection<RecommendationsEntity>> getRecommendations(String scope, String itemId);


	/**
	 * Get the recommended items for item from a particular recommendation group.
	 *
	 * @param scope the scope
	 * @param itemId the item ID
	 * @param recommendationGroup the recommendation group
	 * @param pageNumber the page number of items to retrieve
	 * @return the collection of recommended item IDs wrapped in a {@link PaginationDto}
	 */
	ExecutionResult<PaginationDto> getRecommendedItemsFromGroup(String scope, String itemId,
																String recommendationGroup, int pageNumber);
}
