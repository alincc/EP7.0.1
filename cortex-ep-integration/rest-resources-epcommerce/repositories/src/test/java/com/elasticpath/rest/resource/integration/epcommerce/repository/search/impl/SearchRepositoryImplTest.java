/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.settings.SettingsRepository;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.index.IndexSearchService;
import com.elasticpath.service.search.query.KeywordSearchCriteria;
import com.elasticpath.service.search.solr.IndexUtility;

/**
 * Test class for search lookup strategy.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchRepositoryImplTest {
	private static final String STORE_CODE = "store";
	private static final String PAGINATION_SETTING = "COMMERCE/STORE/listPagination";
	private static final Integer DEFAULT_PAGE_SIZE = 10;
	private static final String OPERATION_FAILURE = "The operation should have failed";
	private static final String SERVER_ERROR_EXPECTED = "The status should be SERVER ERROR";

	@Mock
	private SettingsRepository mockSettingsRepository;
	@Mock
	private IndexSearchService mockIndexSearchService;
	@Mock
	private StoreProductRepository storeProductRepository;
	@Mock
	private ItemRepository mockItemRepository;
	@Mock
	private IndexUtility mockIndexUtility;
	@Mock
	private IndexSearchResult mockIndexSearchResult;

	@InjectMocks
	private SearchRepositoryImpl searchLookupStrategy;

	@Test
	public void ensureSearchForMultipleItemIdsLessThanPageSizeReturnsThemAll() {
		final int page = 1;
		final int pageSize = 10;

		final ProductCategorySearchCriteria searchCriteria = new KeywordSearchCriteria();
		final Collection<String> expectedItemIds = Arrays.asList("id1", "id2", "id3");

		final List<Long> resultUids = mockIndexSearch(page, pageSize, searchCriteria, expectedItemIds.size());
		mockProductAndItemCalls(resultUids, expectedItemIds);

		ExecutionResult<PaginatedResult> itemIdsSearchExecutionResult = searchLookupStrategy.searchForItemIds(page, pageSize,
				searchCriteria);

		assertEquals(ResourceStatus.READ_OK, itemIdsSearchExecutionResult.getResourceStatus());

		PaginatedResult itemIdsSearchResult = itemIdsSearchExecutionResult.getData();
		assertEquals("The result should be the ids returned by the search", expectedItemIds, itemIdsSearchResult.getResultIds());
		assertEquals("The current page should match", page, itemIdsSearchResult.getCurrentPage());
		assertEquals("There should be 1 page", 1, itemIdsSearchResult.getNumberOfPages());
		assertEquals("The page size should match", pageSize, itemIdsSearchResult.getResultsPerPage());
		assertEquals("The total number of results should match", expectedItemIds.size(), itemIdsSearchResult.getTotalNumberOfResults());
	}

	private List<Long> mockIndexSearch(final int page, final int pageSize, final ProductCategorySearchCriteria searchCriteria,
			final int numberOfSearchResultsToReturn) {
		final List<Long> resultUids = Collections.emptyList();

		when(mockIndexSearchService.search(searchCriteria)).thenReturn(mockIndexSearchResult);
		when(mockIndexSearchResult.getResults(page - 1, pageSize)).thenReturn(resultUids);
		when(mockIndexSearchResult.getLastNumFound()).thenReturn(numberOfSearchResultsToReturn);

		return resultUids;
	}

	private void mockProductAndItemCalls(final List<Long> productUids, final Collection<String> expectedItemIds) {
		final Product product1 = new ProductImpl();
		product1.setCode("one");
		final Product product2 = new ProductImpl();
		product2.setCode("two");
		final Product product3 = new ProductImpl();
		product3.setCode("three");
		final List<Product> products = Arrays.asList(product1, product2, product3);

		when(storeProductRepository.findByUids(productUids)).thenReturn(products);
		when(mockIndexUtility.sortDomainList(productUids, products)).thenReturn(products);

		Iterator<Product> productsIterator = products.iterator();
		Iterator<String> expectedItemIdsIterator = expectedItemIds.iterator();
		while (productsIterator.hasNext() && expectedItemIdsIterator.hasNext()) {
			Product product = productsIterator.next();
			String itemId = expectedItemIdsIterator.next();

			when(mockItemRepository.getDefaultItemIdForProduct(product)).thenReturn(ExecutionResultFactory.createReadOK(itemId));
		}
	}

	@Test(expected = AssertionError.class)
	public void ensureSearchForItemIdsWhereGetDefaultItemIdFailsReturnsServerError() {
		final int page = 1;
		final int pageSize = 10;
		final ProductCategorySearchCriteria searchCriteria = new KeywordSearchCriteria();
		final Collection<String> expectedItemIds = Arrays.asList("id1", "id2", "id3");
		final List<Long> resultUids = mockIndexSearch(page, pageSize, searchCriteria, 2);

		mockProductAndItemCallWithAssertionError(resultUids, expectedItemIds);

		searchLookupStrategy.searchForItemIds(page, pageSize, searchCriteria);
	}

	private void mockProductAndItemCallWithAssertionError(final List<Long> productUids, final Collection<String> expectedItemIds) {
		final Product product1 = new ProductImpl();
		final List<Product> products = Arrays.asList(product1);

		when(storeProductRepository.findByUids(productUids)).thenReturn(products);
		when(mockIndexUtility.sortDomainList(productUids, products)).thenReturn(products);

		Iterator<Product> productsIterator = products.iterator();
		Iterator<String> expectedItemIdsIterator = expectedItemIds.iterator();
		while (productsIterator.hasNext() && expectedItemIdsIterator.hasNext()) {
			Product product = productsIterator.next();

			when(mockItemRepository.getDefaultItemIdForProduct(product)).thenThrow(new AssertionError());
		}
	}

	@Test
	public void ensureSearchItemIdsWithException() {
		final int page = 1;
		final int pageSize = 10;
		final ProductCategorySearchCriteria searchCriteria = new KeywordSearchCriteria();

		when(mockIndexSearchService.search(searchCriteria)).thenReturn(mockIndexSearchResult);
		when(mockIndexSearchResult.getResults(page - 1, pageSize))
				.thenThrow(new EpPersistenceException("persistence exception during search"));

		ExecutionResult<PaginatedResult> indexSearchResult = searchLookupStrategy.searchForItemIds(page, pageSize,
				searchCriteria);
		assertTrue(OPERATION_FAILURE, indexSearchResult.isFailure());
		assertEquals(SERVER_ERROR_EXPECTED, ResourceStatus.SERVER_ERROR, indexSearchResult.getResourceStatus());
	}

	@Test
	public void ensureGetDefaultPageSize() {
		when(mockSettingsRepository.getStringSettingValue(PAGINATION_SETTING, STORE_CODE))
				.thenReturn(ExecutionResultFactory.createReadOK(String.valueOf(DEFAULT_PAGE_SIZE)));

		ExecutionResult<Integer> result = searchLookupStrategy.getDefaultPageSize(STORE_CODE);
		assertTrue("The operation should have been successful", result.isSuccessful());
		assertEquals("The page size should have come from the setting", DEFAULT_PAGE_SIZE, result.getData());
	}

	@Test
	public void ensureGetDefaultPageSizeWithInvalidStoreCode() {
		when(mockSettingsRepository.getStringSettingValue(PAGINATION_SETTING, STORE_CODE))
				.thenReturn(ExecutionResultFactory.<String>createNotFound());

		ExecutionResult <Integer> result = searchLookupStrategy.getDefaultPageSize(STORE_CODE);
		assertTrue(OPERATION_FAILURE, result.isFailure());
		assertEquals("The status should be NOT FOUND", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	@Test
	public void ensureGetDefaultPageSizeWithMissingSetting() {
		when(mockSettingsRepository.getStringSettingValue(PAGINATION_SETTING, STORE_CODE))
				.thenReturn(ExecutionResultFactory.<String>createServerError("Error reading setting"));

		ExecutionResult<Integer> result = searchLookupStrategy.getDefaultPageSize(STORE_CODE);
		assertTrue(OPERATION_FAILURE, result.isFailure());
		assertEquals(SERVER_ERROR_EXPECTED, ResourceStatus.SERVER_ERROR, result.getResourceStatus());
	}

	@Test
	public void ensureGetDefaultPageSizeWithNegativePageSize() {
		when(mockSettingsRepository.getStringSettingValue(PAGINATION_SETTING, STORE_CODE))
				.thenReturn(ExecutionResultFactory.createReadOK("-1"));

		ExecutionResult<Integer> result = searchLookupStrategy.getDefaultPageSize(STORE_CODE);
		assertTrue(OPERATION_FAILURE, result.isFailure());
		assertEquals(SERVER_ERROR_EXPECTED, ResourceStatus.SERVER_ERROR, result.getResourceStatus());
	}

	@Test
	public void ensureGetDefaultPageSizeWithNonNumericPageSize() {
		when(mockSettingsRepository.getStringSettingValue(PAGINATION_SETTING, STORE_CODE))
				.thenReturn(ExecutionResultFactory.createReadOK("blarg"));

		ExecutionResult<Integer> result = searchLookupStrategy.getDefaultPageSize(STORE_CODE);
		assertTrue(OPERATION_FAILURE, result.isFailure());
		assertEquals(SERVER_ERROR_EXPECTED, ResourceStatus.SERVER_ERROR, result.getResourceStatus());
	}
}
