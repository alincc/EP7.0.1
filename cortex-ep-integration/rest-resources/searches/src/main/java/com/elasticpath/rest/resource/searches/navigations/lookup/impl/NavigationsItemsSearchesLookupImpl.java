/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.navigations.lookup.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.pagination.transform.PaginatedLinksTransformer;
import com.elasticpath.rest.resource.searches.navigations.integration.NavigationsItemsSearchesLookupStrategy;
import com.elasticpath.rest.resource.searches.navigations.lookup.NavigationsItemsSearchesLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The lookup class for finding items for navigations.
 */
@Singleton
@Named("navigationsItemsSearchesLookup")
public final class NavigationsItemsSearchesLookupImpl implements NavigationsItemsSearchesLookup {

	private static final Logger LOG = LoggerFactory.getLogger(NavigationsItemsSearchesLookupImpl.class);

	private final NavigationsItemsSearchesLookupStrategy navigationsItemsSearchesLookupStrategy;
	private final PaginatedLinksTransformer paginatedLinksTransformer;


	/**
	 * Default constructor.
	 *
	 * @param navigationsItemSearchesLookupStrategy the navigations items searches lookup strategy.
	 * @param paginatedLinksTransformer the paginated links transformer
	 */
	@Inject
	NavigationsItemsSearchesLookupImpl(
			@Named("navigationsItemsSearchesLookupStrategy")
			final NavigationsItemsSearchesLookupStrategy navigationsItemSearchesLookupStrategy,
			@Named("paginatedLinksTransformer")
			final PaginatedLinksTransformer paginatedLinksTransformer) {

		this.navigationsItemsSearchesLookupStrategy = navigationsItemSearchesLookupStrategy;
		this.paginatedLinksTransformer = paginatedLinksTransformer;
	}


	@Override
	public ExecutionResult<ResourceState<PaginatedLinksEntity>> find(final String scope, final String baseUri, final String encodedSearchString,
			final int pageNumber) {

		String decodedNavigationNodeId = Base32Util.decode(encodedSearchString);
		LOG.debug("Decoding Navigation Search String {} to Navigation Node Id {}", encodedSearchString, decodedNavigationNodeId);
		PaginationDto itemSearchDto = Assign.ifSuccessful(
				navigationsItemsSearchesLookupStrategy.find(scope, decodedNavigationNodeId, pageNumber));
		ResourceState<PaginatedLinksEntity> paginatedLinksRepresentation =
				paginatedLinksTransformer.transformToResourceState(itemSearchDto, scope, baseUri);
		return ExecutionResultFactory.createReadOK(paginatedLinksRepresentation);
	}
}
