/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.keywords.lookup;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Look up class for keyword searches.
 */
public interface KeywordsSearchesLookup {

	/**
	 * Find items for the given keywords.
	 *
	 * @param scope the scope
	 * @param baseUri the URI without the pages path part for this search
	 * @param searchKeywords the search keywords string
	 * @param pageNumber the page number
	 * @return the execution result with links to items
	 */
	ExecutionResult<ResourceState<PaginatedLinksEntity>> findItemsByKeywords(String scope, String baseUri, String searchKeywords, int pageNumber);

	/**
	 * Gets the Default page size for a given scope.
	 *
	 * @param scope the scope.
	 * @return the execution result with the default page size.
	 */
	ExecutionResult<Integer> getDefaultPageSize(String scope);
}
