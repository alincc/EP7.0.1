/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.item.recommendations;

import java.util.Collection;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;

/**
 * Recommendation repository for items.
 */
public interface ItemRecommendationsRepository {

	/**
	 * Get a collection of recommendation groups for an item.
	 *
	 * @return Collection of recommendation groups for an item.
	 */
	ExecutionResult<Collection<ProductAssociationType>> getRecommendationGroups();

	/**
	 * Get a collection of recommendation groups for an item.
	 *
	 * @param store the store
	 * @param sourceProduct the source product for the recommendations
	 * @param recommendationGroup the name of the recommendation group
	 * @param pageNumber current page number
	 * @return Collection of paginated recommendation groups for an item.
	 */
	ExecutionResult<PaginatedResult> getRecommendedItemsFromGroup(Store store, Product sourceProduct,
			String recommendationGroup, int pageNumber);

}
