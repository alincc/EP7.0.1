/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.integration.epcommerce.keywords.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResultTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.search.SearchRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.service.search.query.KeywordSearchCriteria;

/**
 * Test class for {@link KeywordSearchLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class KeywordSearchLookupStrategyImplTest {

	private static final String ITEM_ID = "item id";
	private static final int TOTAL_RESULTS = 12842;
	private static final String CATALOG_CODE = "catalog_code";
	private static final String STORE_CODE = "store_code";
	private static final String USERID = "userid";
	private static final String SEARCH_KEYWORDS = "search_keywords";
	private static final int PAGE = 1;
	private static final int RESULTS_PER_PAGE = 5;
	private static final int THREE = 3;
	private static final String OPERATION_SUCCESS_EXPECTED = "The operation should have been successful";

	private final KeywordSearchCriteria searchCriteria = new KeywordSearchCriteria();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private SearchRepository searchRepository;
	@Mock
	private PaginatedResultTransformer paginatedResultTransformer;

	@InjectMocks
	private KeywordSearchLookupStrategyImpl keywordSearchLookupStrategy;

	/**
	 * Test a valid keyword search.
	 */
	@Test
	public void testKeywordSearch() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);
		Collection<String> itemIds = Collections.singleton(ITEM_ID);
		PaginatedResult searchResult = new PaginatedResult(itemIds, PAGE, RESULTS_PER_PAGE, TOTAL_RESULTS);
		PaginationDto dto = ResourceTypeFactory.createResourceEntity(PaginationDto.class);

		shouldGetBean();
		shouldFindSubject();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.createReadOK(RESULTS_PER_PAGE));
		shouldSearchItemIdsWithResult(PAGE, RESULTS_PER_PAGE, ExecutionResultFactory.createReadOK(searchResult));
		shouldTransformIndexSearchResultToEntity(dto);

		ExecutionResult<PaginationDto> result = keywordSearchLookupStrategy.find(STORE_CODE, SEARCH_KEYWORDS, PAGE, null);

		assertTrue(OPERATION_SUCCESS_EXPECTED, result.isSuccessful());
		assertEquals("The results should only contain the expected item", dto, result.getData());
	}

	/**
	 * Test a valid keyword search with a custom page size.
	 */
	@Test
	public void testKeywordSearchWithCustomPageSize() {
		int pageSize = THREE;
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);
		Collection<String> itemIds = Collections.singleton(ITEM_ID);
		PaginatedResult searchResult = new PaginatedResult(itemIds, PAGE, RESULTS_PER_PAGE, pageSize);
		PaginationDto dto = ResourceTypeFactory.createResourceEntity(PaginationDto.class);

		shouldGetBean();
		shouldFindSubject();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldSearchItemIdsWithResult(PAGE, pageSize, ExecutionResultFactory.createReadOK(searchResult));
		shouldTransformIndexSearchResultToEntity(dto);

		ExecutionResult<PaginationDto> result = keywordSearchLookupStrategy.find(STORE_CODE, SEARCH_KEYWORDS, PAGE, pageSize);

		assertTrue(OPERATION_SUCCESS_EXPECTED, result.isSuccessful());
		assertEquals("The results should only contain the expected item", dto, result.getData());
	}

	/**
	 * Test keyword search when no store is found.
	 */
	@Test
	public void testKeywordSearchWithStoreNotFound() {
		shouldFindStoreWithResult(ExecutionResultFactory.<Store>createNotFound("not found"));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		keywordSearchLookupStrategy.find(STORE_CODE, SEARCH_KEYWORDS, PAGE, null);
	}


	/**
	 * Test keyword search when trying to access a page greater than the number of resulting pages.
	 */
	@Test
	public void testKeywordSearchWithPageGreaterThanResultPages() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);
		Collection<String> emptyItemIds = Collections.emptyList();
		PaginatedResult searchResult = new PaginatedResult(emptyItemIds, PAGE, RESULTS_PER_PAGE,
				RESULTS_PER_PAGE);

		shouldGetBean();
		shouldFindSubject();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.createReadOK(RESULTS_PER_PAGE));
		shouldSearchItemIdsWithResult(PAGE + 1, RESULTS_PER_PAGE, ExecutionResultFactory.createReadOK(searchResult));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		keywordSearchLookupStrategy.find(STORE_CODE, SEARCH_KEYWORDS, PAGE + 1, null);
	}

	/**
	 * Test keyword search with invalid pagination setting result returned.
	 */
	@Test
	public void testKeywordSearchWithInvalidPageSizeReturnedFromSettingsRepository() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);

		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.<Integer>createServerError("Invalid pagination setting."));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		keywordSearchLookupStrategy.find(STORE_CODE, SEARCH_KEYWORDS, PAGE, null);
	}

	/**
	 * Test keyword search when pagination setting is invalid.
	 */
	@Test
	public void testKeywordSearchWithPaginationSettingOfZero() {
		shouldFindStoreWithResult(ExecutionResultFactory.<Store>createReadOK(null));
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.<Integer>createServerError("Zero size pagination setting"));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		keywordSearchLookupStrategy.find(STORE_CODE, SEARCH_KEYWORDS, PAGE, null);
	}

	/**
	 * Test the behaviour of keyword search with search result failure.
	 */
	@Test
	public void testKeywordSearchWithSearchResultFailure() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);

		shouldGetBean();
		shouldFindSubject();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldSearchItemIdsWithResult(PAGE, RESULTS_PER_PAGE,
				ExecutionResultFactory.<PaginatedResult>createServerError("Server error during search"));
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.createReadOK(RESULTS_PER_PAGE));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		keywordSearchLookupStrategy.find(STORE_CODE, SEARCH_KEYWORDS, PAGE, null);
	}

	/**
	 * Test the behaviour of keyword search with null from transformer.
	 */
	@Test
	public void testKeywordSearchWithNullFromTransformer() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);
		Collection<String> itemIds = Collections.singleton(ITEM_ID);
		PaginatedResult searchResult = new PaginatedResult(itemIds, PAGE, RESULTS_PER_PAGE, TOTAL_RESULTS);

		shouldGetBean();
		shouldFindSubject();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.createReadOK(RESULTS_PER_PAGE));
		shouldSearchItemIdsWithResult(PAGE, RESULTS_PER_PAGE, ExecutionResultFactory.createReadOK(searchResult));
		shouldTransformIndexSearchResultToEntity(null);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		keywordSearchLookupStrategy.find(STORE_CODE, SEARCH_KEYWORDS, PAGE, null);
	}


	private void shouldFindSubject() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USERID, Locale.ENGLISH);
		when(resourceOperationContext.getSubject())
				.thenReturn(subject);
	}

	private void shouldGetBean() {
		when(searchRepository.createSearchCriteria(ContextIdNames.KEYWORD_SEARCH_CRITERIA)).thenReturn(searchCriteria);
	}

	private void shouldFindStoreWithResult(final ExecutionResult<Store> result) {
		when(storeRepository.findStore(STORE_CODE)).thenReturn(result);
	}

	private void shouldGetDefaultPageSizeWithResult(final ExecutionResult<Integer> result) {
		when(searchRepository.getDefaultPageSize(STORE_CODE)).thenReturn(result);
	}

	private void shouldTransformIndexSearchResultToEntity(final PaginationDto dto) {
		when(paginatedResultTransformer.transformToEntity(any(PaginatedResult.class))).thenReturn(dto);
	}

	private void shouldSearchItemIdsWithResult(final Integer page, final Integer pageSize,
			final ExecutionResult<PaginatedResult> result) {
		when(searchRepository.searchForItemIds(page, pageSize, searchCriteria)).thenReturn(result);
	}

	private Catalog createMockCatalog() {
		Catalog catalog = mock(Catalog.class);
		stub(catalog.getCode()).toReturn(CATALOG_CODE);

		return catalog;
	}

	private Store createMockStore(final Catalog catalog) {
		Store store = mock(Store.class);
		stub(store.getCatalog()).toReturn(catalog);

		return store;
	}
}
