/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for navigation Recommendations.
 */
public interface NavigationRecommendationsLookup {


	/**
	 * Get the recommendations for a navigation.
	 *
	 * @param navigationEntityResourceState the navigation representation
	 * @return the execution result with a {@link ResourceState} containing links
	 * to {@link com.elasticpath.rest.definition.recommendations.RecommendationsEntity}s
	 */
	ExecutionResult<ResourceState<LinksEntity>> getRecommendations(ResourceState<NavigationEntity> navigationEntityResourceState);

	/**
	 * See if given source has recommendations.
	 *
	 * @param navigationEntity the navigation representation
	 * @param scope the scope
	 * @return true if source contains recommendations
	 */
	ExecutionResult<Boolean> hasRecommendations(NavigationEntity navigationEntity, String scope);


	/**
	 * Get the recommended items from a group for a navigation.
	 *
	 * @param navigationEntityResourceState the navigation representation
	 * @param recommendationGroup the recommendation group, eg crosssells, upsells etc
	 * @param pageNumber the page number to return
	 * @return the execution result with a paginated list of recommended item links
	 */
	ExecutionResult<ResourceState<PaginatedLinksEntity>> getRecommendedItemsFromGroup(ResourceState<NavigationEntity> navigationEntityResourceState,
																			String recommendationGroup, int pageNumber);
}
