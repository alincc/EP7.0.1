/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.FailedResultBuilder;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.search.SearchRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.settings.SettingsRepository;
import com.elasticpath.rest.util.math.NumberUtil;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.service.search.solr.IndexUtility;

/**
 * Repository class for general search.
 */
@Singleton
@Named("searchRepository")
public class SearchRepositoryImpl implements SearchRepository {

	private static final Logger LOG = LoggerFactory.getLogger(SearchRepositoryImpl.class);

	private static final String DEFAULT_PAGINATION_VALUE_ERROR = "Default pagination value for '%s' is invalid: '%s'";
	private static final String PAGINATION_SETTING = "COMMERCE/STORE/listPagination";

	private final SettingsRepository settingsRepository;
	private final IndexSearchService indexSearchService;
	private final StoreProductRepository storeProductRepository;
	private final ItemRepository itemRepository;
	private final IndexUtility indexUtility;
	private final BeanFactory beanFactory;


	/**
	 * Default Constructor.
	 *
	 * @param settingsRepository the settings repository
	 * @param indexSearchService the index search service
	 * @param storeProductRepository a product repository
	 * @param itemRepository the repository for managing item domain interactions
	 * @param indexUtility the index utility
	 * @param beanFactory beanFactory
	 */
	@Inject
	public SearchRepositoryImpl(
			@Named("settingsRepository")
			final SettingsRepository settingsRepository,
			@Named("indexSearchService")
			final IndexSearchService indexSearchService,
			@Named("storeProductRepository")
			final StoreProductRepository storeProductRepository,
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("indexUtility")
			final IndexUtility indexUtility,
			@Named("coreBeanFactory")
			final BeanFactory beanFactory) {

		this.settingsRepository = settingsRepository;
		this.indexSearchService = indexSearchService;
		this.storeProductRepository = storeProductRepository;
		this.itemRepository = itemRepository;
		this.indexUtility = indexUtility;
		this.beanFactory = beanFactory;
	}


	@Override
	@CacheResult
	public ExecutionResult<Integer> getDefaultPageSize(final String storeCode) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				String paginationString = Assign.ifSuccessful(settingsRepository.getStringSettingValue(PAGINATION_SETTING, storeCode));
				FailedResultBuilder<?> failureBuilder = OnFailure.returnServerError(
						DEFAULT_PAGINATION_VALUE_ERROR, storeCode, paginationString);
				Integer paginationValue = Assign.ifNotNull(parseInteger(paginationString), failureBuilder);
				Ensure.isTrue(NumberUtil.isPositive(paginationValue), failureBuilder);
				LOG.trace("default page size for {} is {}", storeCode, paginationValue);
				return ExecutionResultFactory.createReadOK(paginationValue);
			}
		}.execute();
	}

	private Integer parseInteger(final String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	@CacheResult
	public ExecutionResult<PaginatedResult> searchForItemIds(final int startPageNumber, final int numberOfResultsPerPage,
			final ProductCategorySearchCriteria productSearchCriteria) {

		ExecutionResult<PaginatedResult> result;

		try {
			result = new ExecutionResultChain() {
				public ExecutionResult<?> build() {
					IndexSearchResult productSearchResult = indexSearchService.search(productSearchCriteria);

					int startIndex = (startPageNumber - 1) * numberOfResultsPerPage;
					List<Long> productUids = productSearchResult.getResults(startIndex, numberOfResultsPerPage);

					List<Product> products = storeProductRepository.findByUids(productUids);
					List<Product> sortedProducts = indexUtility.sortDomainList(productUids, products);

					// translate product IDs to item IDs in the same order
					Collection<String> itemIds = new ArrayList<>(sortedProducts.size());
					for (Product product : sortedProducts) {
						String defaultItemId = Assign.ifSuccessful(itemRepository.getDefaultItemIdForProduct(product));
						itemIds.add(defaultItemId);
					}

					int totalResults = productSearchResult.getLastNumFound();
					return ExecutionResultFactory.createReadOK(
							new PaginatedResult(itemIds, startPageNumber, numberOfResultsPerPage, totalResults));
				}
			}.execute();
		} catch (Exception exception) {
			LOG.error("Error when searching for item ids", exception);
			result = ExecutionResultFactory.createServerError("Server error when searching for item ids");
		}

		return result;
	}

	@Override
	public <T> T createSearchCriteria(final String criteriaType) {
		return beanFactory.getBean(criteriaType);
	}
}
