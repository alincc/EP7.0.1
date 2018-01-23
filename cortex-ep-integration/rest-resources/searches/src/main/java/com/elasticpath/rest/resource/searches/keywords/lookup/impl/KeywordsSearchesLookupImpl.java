/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.keywords.lookup.impl;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.definition.collections.PaginationEntity;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.pagination.transform.PaginatedLinksTransformer;
import com.elasticpath.rest.resource.searches.keywords.integration.KeywordSearchLookupStrategy;
import com.elasticpath.rest.resource.searches.keywords.lookup.KeywordsSearchesLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Look up class for keyword searches.
 */
@Singleton
@Named("keywordsSearchesLookup")
public final class KeywordsSearchesLookupImpl implements KeywordsSearchesLookup {

	private final KeywordSearchLookupStrategy keywordSearchLookupStrategy;
	private final PaginatedLinksTransformer paginatedLinksTransformer;


	/**
	 * Constructor.
	 *
	 * @param keywordSearchLookupStrategy the keyword search lookup strategy
	 * @param paginatedLinksTransformer the paginated links transformer
	 */
	@Inject
	KeywordsSearchesLookupImpl(
			@Named("keywordSearchLookupStrategy")
			final KeywordSearchLookupStrategy keywordSearchLookupStrategy,
			@Named("paginatedLinksTransformer")
			final PaginatedLinksTransformer paginatedLinksTransformer) {

		this.keywordSearchLookupStrategy = keywordSearchLookupStrategy;
		this.paginatedLinksTransformer = paginatedLinksTransformer;
	}


	@Override
	public ExecutionResult<ResourceState<PaginatedLinksEntity>> findItemsByKeywords(final String scope, final String baseUri,
			final String searchKeywords, final int pageNumber) {

		Map<String, String> searchFieldValuesMap = Assign.ifNotNull(CompositeIdUtil.decodeCompositeId(searchKeywords),
				OnFailure.returnNotFound());
		String decodedSearchKeywords = searchFieldValuesMap.get(SearchKeywordsEntity.KEYWORDS_PROPERTY);
		String pageSizeString = searchFieldValuesMap.get(PaginationEntity.PAGE_SIZE_PROPERTY);
		Integer pageSize = null;
		if (StringUtils.isNotBlank(pageSizeString)) {
			Ensure.isTrue(NumberUtils.isDigits(pageSizeString),
					OnFailure.returnBadRequestBody("Invalid page size"));
			pageSize = NumberUtils.createInteger(pageSizeString);
		}

		PaginationDto keywordSearchDto = Assign.ifSuccessful(
				keywordSearchLookupStrategy.find(scope, decodedSearchKeywords, pageNumber, pageSize));
		ResourceState<PaginatedLinksEntity> paginatedLinks =
				paginatedLinksTransformer.transformToResourceState(keywordSearchDto, scope, baseUri);
		return ExecutionResultFactory.createReadOK(paginatedLinks);
	}

	@Override
	public ExecutionResult<Integer> getDefaultPageSize(final String scope) {
		return keywordSearchLookupStrategy.getDefaultPageSize(scope);
	}
}
