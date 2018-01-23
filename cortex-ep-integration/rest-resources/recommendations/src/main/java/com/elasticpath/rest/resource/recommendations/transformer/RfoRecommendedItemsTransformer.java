/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.transformer;

import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The transformer for converting a list of recommended items during an RFO scenario.
 * Unique because recommended items is not treated as a read from other.
 */
public interface RfoRecommendedItemsTransformer {

	/**
	 * Convert a {@link PaginationDto} containing recommended items to a {@link ResourceState}.
	 *
	 * @param recommendedItems the pagination dto containing a paginated list of recommended items
   	 * @param otherResourceState the other representation
	 * @param recommendationGroup the recommendation group
	 * @return the representation
	 */
	ResourceState<PaginatedLinksEntity> transformToRepresentation(PaginationDto recommendedItems, ResourceState<?> otherResourceState,
			String recommendationGroup);
}
