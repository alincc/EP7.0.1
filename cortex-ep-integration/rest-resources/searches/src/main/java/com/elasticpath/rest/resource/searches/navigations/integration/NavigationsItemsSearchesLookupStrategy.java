/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.navigations.integration;


import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;

/**
 * Service that provides lookup of item data through indexed search.
 */
public interface NavigationsItemsSearchesLookupStrategy {

	/**
	 * Find items from search criteria.
	 *
	 * @param scope the scope
	 * @param decodedNavigationNodeId the decoded id of the navigation node to find associated items with
	 * @param page the page number
	 * @return the DTO with paginated item configuration IDs.
	 */
	ExecutionResult<PaginationDto> find(String scope, String decodedNavigationNodeId, int page);
}
