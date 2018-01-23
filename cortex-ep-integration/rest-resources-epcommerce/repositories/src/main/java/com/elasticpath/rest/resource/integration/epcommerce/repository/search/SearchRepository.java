/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.service.search.ProductCategorySearchCriteria;

/**
 * Repository class for general search.
 */
public interface SearchRepository {

	/**
	 * Gets the default page size.
	 *
	 * @param storeCode the store code
	 * @return the default page size
	 */
	ExecutionResult<Integer> getDefaultPageSize(String storeCode);

	/**
	 * Returns a paginated search result containing the default item ids for products
	 * returned by a search with {@code productSearchCriteria}.
	 *
	 * @param startPageNumber the starting page to return results from
	 * @param numberOfResultsPerPage the page size to use
	 * @param productSearchCriteria the search criteria for finding the desired products
	 * @return the paginated item search results
	 */
	ExecutionResult<PaginatedResult> searchForItemIds(int startPageNumber, int numberOfResultsPerPage,
			ProductCategorySearchCriteria productSearchCriteria);

	/**
	 * Returns an empty criteria of the specified type.
	 *
	 * @param criteriaType type of criteria. Expects strings found in {@link com.elasticpath.commons.constants.ContextIdNames}
	 * @param <T> criteria return type
	 * @return criteria
	 */
	<T> T createSearchCriteria(String criteriaType);
}
