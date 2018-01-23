/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.integration.epcommerce.navigations.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

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
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResultTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.search.SearchRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.service.search.query.ProductSearchCriteria;

/**
 * Test class for {@link NavigationsItemsSearchesLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class NavigationsItemsSearchesLookupStrategyImplTest {

	private static final String ITEM_ID1 = "itemId1";
	private static final int PAGE_SIZE = 10;
	private static final String USERID = "userid";
	private static final String CATALOG_CODE = "catalog_code";
	private static final String STORE_CODE = "store_code";
	private static final String CATEGORY_CODE = "category_code";

	private final ProductSearchCriteria productSearchCriteria = new ProductSearchCriteria();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private CategoryRepository categoryLookup;
	@Mock
	private SearchRepository searchRepository;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private PaginatedResultTransformer resultDomainTransformer;

	@InjectMocks
	private NavigationsItemsSearchesLookupStrategyImpl lookupStrategy;

	/**
	 * Test find item by category.
	 */
	@Test
	public void testFindItemByCategory() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);
		Category category = createMockCategory(catalog);
		PaginationDto paginationDto = ResourceTypeFactory.createResourceEntity(PaginationDto.class);
		paginationDto.setPageSize(PAGE_SIZE);
		PaginatedResult searchResult = new PaginatedResult(Collections.singleton(ITEM_ID1), 1, PAGE_SIZE, PAGE_SIZE);

		shouldFindSubject();
		createProductSearchCriteria();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.createReadOK(PAGE_SIZE));
		shouldFindByGuid(category);
		shouldSearchItemIdsWithResult(1, ExecutionResultFactory.createReadOK(searchResult));
		shouldTransformIndexSearchResultToEntity(paginationDto);

		ExecutionResult<PaginationDto> result = lookupStrategy.find(STORE_CODE, CATEGORY_CODE, 1);

		assertTrue("The result should be successful", result.isSuccessful());
		assertEquals("The result should be a pagination dto as expected", paginationDto, result.getData());
	}

	/**
	 * Test find item by category with category not found.
	 */
	@Test
	public void testFindItemByCategoryWithCategoryNotFound() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);

		shouldFindSubject();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.createReadOK(PAGE_SIZE));
		shouldFindByGuid(null);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.find(STORE_CODE, CATEGORY_CODE, 0);
	}

	/**
	 * Test find item by category with store not found.
	 */
	@Test
	public void testFindItemByCategoryWithStoreNotFound() {
		shouldFindSubject();
		shouldFindStoreWithResult(ExecutionResultFactory.<Store>createNotFound("Store not found."));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.find(STORE_CODE, CATEGORY_CODE, 0);
	}

	/**
	 * Test find item by category with an invalid pagination setting result returned.
	 */
	@Test
	public void testFindItemByCategoryWithInvalidPageSizeFromSettingsRepository() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);

		shouldFindSubject();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.<Integer>createServerError("Invalid pagination setting"));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		lookupStrategy.find(STORE_CODE, CATEGORY_CODE, 0);
	}

	/**
	 * Test the behaviour of find item by category with search failure.
	 */
	@Test
	public void testFindItemByCategoryWithSearchFailure() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);

		shouldFindSubject();
		createProductSearchCriteria();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldFindByGuid(null);
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.createReadOK(PAGE_SIZE));
		shouldSearchItemIdsWithResult(1, ExecutionResultFactory.<PaginatedResult>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.find(STORE_CODE, CATEGORY_CODE, 1);
	}

	/**
	 * Test the behaviour of find item by category with invalid page.
	 */
	@Test
	public void testFindItemByCategoryWithInvalidPage() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);
		PaginatedResult searchResult = new PaginatedResult(Collections.singleton(ITEM_ID1), 1, PAGE_SIZE, PAGE_SIZE);

		shouldFindSubject();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldFindByGuid(null);
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.createReadOK(PAGE_SIZE));
		shouldSearchItemIdsWithResult(2, ExecutionResultFactory.createReadOK(searchResult));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.find(STORE_CODE, CATEGORY_CODE, 2);
	}

	/**
	 * Test the behaviour of find item by category with transformer failure.
	 */
	@Test
	public void testFindItemByCategoryWithTransformerFailure() {
		Catalog catalog = createMockCatalog();
		Store store = createMockStore(catalog);
		PaginatedResult searchResult = new PaginatedResult(Collections.singleton(ITEM_ID1), 1, PAGE_SIZE, PAGE_SIZE);

		shouldFindSubject();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldFindByGuid(null);
		shouldGetDefaultPageSizeWithResult(ExecutionResultFactory.createReadOK(PAGE_SIZE));
		shouldSearchItemIdsWithResult(1, ExecutionResultFactory.createReadOK(searchResult));
		shouldTransformIndexSearchResultToEntity(null);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.find(STORE_CODE, CATEGORY_CODE, 1);
	}

	private void shouldFindSubject() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USERID, Locale.ENGLISH);
		when(resourceOperationContext.getSubject())
				.thenReturn(subject);
	}

	private void shouldGetDefaultPageSizeWithResult(final ExecutionResult<Integer> result) {
		when(searchRepository.getDefaultPageSize(STORE_CODE)).thenReturn(result);
	}

	private void shouldFindStoreWithResult(final ExecutionResult<Store> result) {
		when(storeRepository.findStore(STORE_CODE)).thenReturn(result);
	}

	private void shouldSearchItemIdsWithResult(final Integer startPage,
			final ExecutionResult<PaginatedResult> executionResult) {
		when(searchRepository.searchForItemIds(startPage, PAGE_SIZE, productSearchCriteria)).thenReturn(executionResult);
	}

	private void shouldFindByGuid(final Category category) {
		when(categoryLookup.findByGuid(STORE_CODE, CATEGORY_CODE)).thenReturn(
				ExecutionResultFactory.createReadOK(category));
	}

	private void createProductSearchCriteria() {
		when(searchRepository.createSearchCriteria(ContextIdNames.PRODUCT_SEARCH_CRITERIA)).thenReturn(productSearchCriteria);
	}

	private void shouldTransformIndexSearchResultToEntity(final PaginationDto dto) {
		when(resultDomainTransformer.transformToEntity(any(PaginatedResult.class))).thenReturn(dto);
	}

	private Catalog createMockCatalog() {
		final Catalog catalog = mock(Catalog.class);
		catalog.setCode(CATALOG_CODE);

		return catalog;
	}

	private Store createMockStore(final Catalog catalog) {
		final Store store = mock(Store.class);
		stub(store.getCatalog()).toReturn(catalog);
		stub(store.getCode()).toReturn(STORE_CODE);

		return store;
	}

	private Category createMockCategory(final Catalog catalog) {
		final Category category = mock(Category.class);
		stub(category.getCatalog()).toReturn(catalog);

		return category;
	}
}
