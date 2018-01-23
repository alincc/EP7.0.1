/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.category.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.misc.impl.OrderingComparatorImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Tests {@link CategoryRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryRepositoryImplTest {

	private static final long CATEGORY_UID = 0;
	private static final String CATALOG_CODE = "CATALOG_CODE";
	private static final String CATEGORY2_CODE = "CATEGORY2_CODE";
	private static final String CATEGORY1_CODE = "CATEGORY1_CODE";
	private static final String CATEGORY_CODE = "CATEGORY_CODE";
	private static final String STORE_CODE = "STORE_CODE";

	@Mock
	private CategoryService categoryService;
	@Mock
	private CategoryLookup categoryLookup;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private BeanFactory coreBeanFactory;
	@InjectMocks
	private CategoryRepositoryImpl categoryRepository;

	@Test
	public void testFindRootNodesWhenSuccessful() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		Category category =  createMockCategory();
		List<Category> categories = Collections.singletonList(category);
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldListRootCategories(catalog, categories);

		ExecutionResult<Collection<Category>> rootNodesResult = categoryRepository.findRootCategories(STORE_CODE);

		assertExecutionResult(rootNodesResult)
				.isSuccessful()
				.data(categories);
	}

	@Test
	public void testFindRootNodesWhenNotFound() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		List<Category> categories = Collections.emptyList();
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		shouldListRootCategories(catalog, categories);

		ExecutionResult<Collection<Category>> rootNodesResult = categoryRepository.findRootCategories(STORE_CODE);

		assertExecutionResult(rootNodesResult)
				.isSuccessful()
				.data(categories);
	}

	@Test
	public void testFindRootNodesWhenStoreNotFound() {
		shouldFindStoreWithResult(ExecutionResultFactory.<Store>createNotFound("not found"));

		ExecutionResult<Collection<Category>> rootNodesResult = categoryRepository.findRootCategories(STORE_CODE);

		assertExecutionResult(rootNodesResult)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testGetCategoryByIdWhenFound() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		Category category = createMockCategory();
		when(categoryLookup.findByCategoryAndCatalogCode(CATEGORY_CODE, CATALOG_CODE)).thenReturn(category);

		ExecutionResult<Category> result = categoryRepository.findByGuid(STORE_CODE, CATEGORY_CODE);

		assertExecutionResult(result)
				.isSuccessful()
				.data(category);
	}
	@Test
	public void testGetCategoryByIdWhenStoreNotFound() {
		shouldFindStoreWithResult(ExecutionResultFactory.<Store>createNotFound("not found"));

		ExecutionResult<Category> result = categoryRepository.findByGuid(STORE_CODE, CATEGORY_CODE);

		assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testGetCategoryByIdWhenCategoryNotFound() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		when(categoryLookup.findByCategoryAndCatalogCode(CATEGORY_CODE, CATALOG_CODE)).thenReturn(null);

		ExecutionResult<Category> result = categoryRepository.findByGuid(STORE_CODE, CATEGORY_CODE);

		assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testGettingChildNodesWhenFound() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		Category category = createMockCategory();
		when(categoryLookup.findByCategoryAndCatalogCode(CATEGORY_CODE, CATALOG_CODE)).thenReturn(category);
		List<Category> categories = createCategoriesList();
		shouldGetSubCategories(category, categories);
		shouldGetBean();

		ExecutionResult<Collection<Category>> result = categoryRepository.findChildren(STORE_CODE, CATEGORY_CODE);

		assertExecutionResult(result)
				.isSuccessful()
				.data(categories);
	}

	@Test
	public void testGettingChildNodesWhenNoneFound() {
		Catalog catalog = createMockCatalog(CATALOG_CODE);
		Store store = createMockStore(catalog);
		shouldFindStoreWithResult(ExecutionResultFactory.createReadOK(store));
		Category category = createMockCategory();
		when(categoryLookup.findByCategoryAndCatalogCode(CATEGORY_CODE, CATALOG_CODE)).thenReturn(category);
		List<Category> categories = Collections.emptyList();
		shouldGetSubCategories(category, categories);

		ExecutionResult<Collection<Category>> result = categoryRepository.findChildren(STORE_CODE, CATEGORY_CODE);

		assertExecutionResult(result)
				.isSuccessful()
				.data(categories);
	}



	private void shouldFindStoreWithResult(final ExecutionResult<Store> result) {
		when(storeRepository.findStore(STORE_CODE)).thenReturn(result);
	}

	private void shouldListRootCategories(final Catalog catalog, final List<Category> categories) {
		when(categoryService.listRootCategories(catalog, true)).thenReturn(categories);
	}

	private void shouldGetSubCategories(final Category category, final List<Category> categories) {
		when(category.getUidPk()).thenReturn(CATEGORY_UID);
		when(categoryLookup.findChildren(category)).thenReturn(categories);
	}

	private void shouldGetBean() {
		when(coreBeanFactory.getBean(ContextIdNames.ORDERING_COMPARATOR)).thenReturn(new OrderingComparatorImpl());
	}

	private Catalog createMockCatalog(final String catalogCode) {
		Catalog catalog = mock(Catalog.class);
		stub(catalog.getCode()).toReturn(catalogCode);

		return catalog;
	}

	private Store createMockStore(final Catalog catalog) {
		Store store = mock(Store.class);
		stub(store.getCatalog()).toReturn(catalog);

		return store;
	}

	private List<Category> createCategoriesList() {
		List<Category> categories = new ArrayList<>();
		Category category1 = new CategoryImpl();
		Category category2 = new CategoryImpl();

		category1.setCode(CATEGORY1_CODE);
		category2.setCode(CATEGORY2_CODE);

		categories.add(category1);
		categories.add(category2);

		return categories;
	}

	private Category createMockCategory() {
		Category category = mock(Category.class);
		category.setCode(CATEGORY_CODE);

		return category;
	}
}
