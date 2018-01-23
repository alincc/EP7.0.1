/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.integration.epcommerce.keywords.impl;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResultTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.search.SearchRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.searches.keywords.integration.KeywordSearchLookupStrategy;
import com.elasticpath.service.search.query.KeywordSearchCriteria;

/**
 * Service that provides lookup of item data through indexed keyword search.
 */
@Singleton
@Named("keywordSearchLookupStrategy")
public class KeywordSearchLookupStrategyImpl implements KeywordSearchLookupStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(KeywordSearchLookupStrategyImpl.class);

	private final ResourceOperationContext resourceOperationContext;
	private final StoreRepository storeRepository;
	private final SearchRepository searchRepository;
	private final PaginatedResultTransformer paginatedResultTransformer;


	/**
	 * Default constructor.
	 *
	 * @param resourceOperationContext   the resource operation context
	 * @param storeRepository            the store repository
	 * @param searchRepository           the search repository
	 * @param paginatedResultTransformer the paginated result transformer
	 */
	@Inject
	public KeywordSearchLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("storeRepository")
			final StoreRepository storeRepository,
			@Named("searchRepository")
			final SearchRepository searchRepository,
			@Named("paginatedResultTransformer")
			final PaginatedResultTransformer paginatedResultTransformer) {

		this.resourceOperationContext = resourceOperationContext;
		this.storeRepository = storeRepository;
		this.searchRepository = searchRepository;
		this.paginatedResultTransformer = paginatedResultTransformer;
	}


	@Override
	public ExecutionResult<PaginationDto> find(
			final String storeCode, final String searchKeywords, final int currentPageNumber, final Integer numberOfResultsPerPage) {

		Store store = Assign.ifSuccessful(storeRepository.findStore(storeCode));
		Integer pageSizeUsed = Assign.ifSuccessful(getPageSizeUsed(storeCode, numberOfResultsPerPage));
		KeywordSearchCriteria keywordSearchCriteria = Assign.ifSuccessful(createSearchCriteria(searchKeywords, store));

		PaginatedResult paginatedResult =
				Assign.ifSuccessful(searchRepository.searchForItemIds(currentPageNumber, pageSizeUsed, keywordSearchCriteria));
		int pageCount = paginatedResult.getNumberOfPages();
		if (currentPageNumber > pageCount) {
			LOG.debug("Tried to access page {} which exceeds number of pages: {}", currentPageNumber, pageCount);
		}
		Ensure.isTrue(currentPageNumber <= pageCount,
				OnFailure.returnNotFound("Page %s does not exist.", currentPageNumber));
		PaginationDto paginationDto = Assign.ifNotNull(paginatedResultTransformer.transformToEntity(paginatedResult),
				OnFailure.returnNotFound("Domain transformer failure."));
		return ExecutionResultFactory.createReadOK(paginationDto);
	}

	@Override
	public ExecutionResult<Integer> getDefaultPageSize(final String storeCode) {
		return searchRepository.getDefaultPageSize(storeCode);
	}

	private ExecutionResult<Integer> getPageSizeUsed(final String storeCode, final Integer numberOfResultsPerPage) {
		final ExecutionResult<Integer> result;
		if (numberOfResultsPerPage == null) {
			result = getDefaultPageSize(storeCode);
		} else {
			result = ExecutionResultFactory.createReadOK(numberOfResultsPerPage);
		}
		return result;
	}

	private ExecutionResult<KeywordSearchCriteria> createSearchCriteria(final String searchKeywords, final Store store) {

		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		KeywordSearchCriteria searchCriteria = createKeywordSearchCriteria(locale, store, searchKeywords);
		return ExecutionResultFactory.createReadOK(searchCriteria);
	}

	private KeywordSearchCriteria createKeywordSearchCriteria(final Locale locale, final Store store, final String searchKeywords) {
		KeywordSearchCriteria searchCriteria = searchRepository.createSearchCriteria(ContextIdNames.KEYWORD_SEARCH_CRITERIA);
		searchCriteria.setStoreCode(store.getCode());
		searchCriteria.setCatalogCode(store.getCatalog().getCode());
		searchCriteria.setFuzzySearchDisabled(false);
		searchCriteria.setKeyword(searchKeywords);
		searchCriteria.setLocale(locale);
		searchCriteria.setDisplayableOnly(true);
		searchCriteria.setActiveOnly(true);
		return searchCriteria;
	}
}
