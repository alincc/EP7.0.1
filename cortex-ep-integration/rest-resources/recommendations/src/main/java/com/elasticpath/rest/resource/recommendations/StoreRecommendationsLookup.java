/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.schema.ResourceState;
/**
 * Lookup class for Recommendations data.
 */
public interface StoreRecommendationsLookup {

	/**
	 * Get root recommendations.
	 *
	 * @param scope the scope
	 * @return the execution result with a {@link ResourceState} containing recommendation groups on success
	 */
	ExecutionResult<ResourceState<LinksEntity>> getRecommendations(String scope);

	/**
	 * Get the recommended items for the given recommendations id.
	 *
	 *
	 * @param scope the scope
	 * @param recommendationGroup the recommendations group
	 * @param pageNumber the page number to return
	 * @return the execution result with a paginated list of recommended item links
	 */
	ExecutionResult<ResourceState<PaginatedLinksEntity>> getRecommendedItemsFromGroup(String scope,
																			String recommendationGroup,
																			int pageNumber);

}
