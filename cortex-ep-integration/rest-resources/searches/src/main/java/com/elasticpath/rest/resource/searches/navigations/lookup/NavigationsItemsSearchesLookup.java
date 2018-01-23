/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.navigations.lookup;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The lookup interface for finding items for navigations.
 */
public interface NavigationsItemsSearchesLookup {

	/**
	 * Finds items for a navigation.
	 *
	 * @param scope the scope
	 * @param baseUri the base URI.
	 * @param encodedSearchString the encoded search string
	 * @param pageNumber the page number
	 * @return the execution result with the paginated item links representation.
	 */
	ExecutionResult<ResourceState<PaginatedLinksEntity>> find(String scope, String baseUri, String encodedSearchString, int pageNumber);
}
