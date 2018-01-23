/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.keywords.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;

/**
 * Service that provides lookup of item data through indexed keyword search.
 */
public interface KeywordSearchLookupStrategy {

	/**
	 * Find the search results with the given search keywords and page information.
	 *
	 * @param scope the scope
	 * @param decodedSearchKeywords the decoded search keywords
	 * @param currentPageNumber the current page number
	 * @param numberOfResultsPerPage the number of results per page
	 * @return the execution result
	 */
	ExecutionResult<PaginationDto> find(String scope, String decodedSearchKeywords, int currentPageNumber, Integer numberOfResultsPerPage);

	/**
	 * Gets the default page size.
	 *
	 * @param scope the scope
	 * @return the default page size
	 */
	ExecutionResult<Integer> getDefaultPageSize(String scope);
}
