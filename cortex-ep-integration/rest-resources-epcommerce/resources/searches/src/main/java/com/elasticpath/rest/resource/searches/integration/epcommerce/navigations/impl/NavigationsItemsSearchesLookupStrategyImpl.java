/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.integration.epcommerce.navigations.impl;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResultTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.search.SearchRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.searches.navigations.integration.NavigationsItemsSearchesLookupStrategy;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * Service that provides lookup of item data through SOLR search.
 */
@Singleton
@Named("navigationsItemsSearchesLookupStrategy")
public class NavigationsItemsSearchesLookupStrategyImpl implements NavigationsItemsSearchesLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final CategoryRepository categoryRepository;
	private final StoreRepository storeRepository;
	private final SearchRepository searchRepository;
	private final PaginatedResultTransformer paginatedResultTransformer;


	/**
	 * Default constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param categoryRepository a category lookup
	 * @param storeRepository the store repository
	 * @param searchRepository the search repository
	 * @param paginatedResultTransformer the paginated result transformer
	 */
	@Inject
	public NavigationsItemsSearchesLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("categoryRepository")
			final CategoryRepository categoryRepository,
			@Named("storeRepository")
			final StoreRepository storeRepository,
			@Named("searchRepository")
			final SearchRepository searchRepository,
			@Named("paginatedResultTransformer")
			final PaginatedResultTransformer paginatedResultTransformer) {

		this.resourceOperationContext = resourceOperationContext;
		this.categoryRepository = categoryRepository;
		this.storeRepository = storeRepository;
		this.searchRepository = searchRepository;
		this.paginatedResultTransformer = paginatedResultTransformer;
	}

	@Override
	public ExecutionResult<PaginationDto> find(final String storeCode, final String navigationNodeGuid, final int page) {

		Store store = Assign.ifSuccessful(storeRepository.findStore(storeCode));
		Integer resultsPerPage = Assign.ifSuccessful(searchRepository.getDefaultPageSize(store.getCode()));
		Category category = Assign.ifSuccessful(
				categoryRepository.findByGuid(storeCode, navigationNodeGuid));
		Ensure.notNull(category, OnFailure.returnNotFound("Navigation node was not found."));
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		ProductSearchCriteria productSearchCriteria = createSearchCriteria(category, store.getCode(), locale);

		PaginatedResult paginatedResult =
				Assign.ifSuccessful(searchRepository.searchForItemIds(page, resultsPerPage, productSearchCriteria));

		Ensure.isTrue(page <= paginatedResult.getNumberOfPages(),
				OnFailure.returnNotFound("Page %s does not exist.", page));
		PaginationDto paginationDto = paginatedResultTransformer.transformToEntity(paginatedResult);
		return ExecutionResultFactory.createReadOK(paginationDto);
	}


	/**
	 * Creates new search criteria and populates it with category id and default search values.
	 */
	private ProductSearchCriteria createSearchCriteria(final Category category, final String storeCode, final Locale locale) {
		ProductSearchCriteria criteria = searchRepository.createSearchCriteria(ContextIdNames.PRODUCT_SEARCH_CRITERIA);

		criteria.setFuzzySearchDisabled(true);
		criteria.setOnlyWithinDirectCategory(true);
		criteria.setDisplayableOnly(true);
		criteria.setActiveOnly(true);
		criteria.setDirectCategoryUid(category.getUidPk());
		criteria.setCatalogCode(category.getCatalog().getCode());
		criteria.setStoreCode(storeCode);
		criteria.setLocale(locale);
		criteria.setSortingType(StandardSortBy.FEATURED_CATEGORY);
		criteria.setSortingOrder(SortOrder.DESCENDING);
		return criteria;
	}
}
