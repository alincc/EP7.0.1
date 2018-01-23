/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for item Recommendations.
 */
public interface ItemRecommendationsLookup {


	/**
	 * Get the recommendations for an item.
	 *
	 * @param itemEntityResourceState the item representation
	 * @return the execution result with collection of recommendations Dtos
	 */
	ExecutionResult<ResourceState<LinksEntity>> getRecommendations(ResourceState<ItemEntity> itemEntityResourceState);

	/**
	 * Get the recommended items from a group for a item.
	 *
	 * @param itemEntityResourceState the item representation
	 * @param recommendationGroup the recommendation group, eg crosssells, upsells etc
	 * @param pageNumber the page number to return
	 * @return the execution result with a paginated list of recommended item links
	 */
	ExecutionResult<ResourceState<PaginatedLinksEntity>> getRecommendedItemsFromGroup(ResourceState<ItemEntity> itemEntityResourceState,
																			String recommendationGroup, int pageNumber);
}
