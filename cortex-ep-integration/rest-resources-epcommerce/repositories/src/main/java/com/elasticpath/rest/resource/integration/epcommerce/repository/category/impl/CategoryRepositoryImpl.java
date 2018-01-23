/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.category.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.misc.OrderingComparator;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Repository class for general search.
 */
@Singleton
@Named("categoryRepository")
public class CategoryRepositoryImpl implements CategoryRepository {

	private final CategoryService categoryService;
	private final CategoryLookup categoryLookup;
	private final StoreRepository storeRepository;
	private final BeanFactory coreBeanFactory;

	/**
	 * Default constructor.
	 *
	 * @param categoryLookup the category lookup.
	 * @param storeRepository the store repository.
	 * @param coreBeanFactory the core bean factory.
	 * @param categoryService the category service.
	 */
	@Inject
	CategoryRepositoryImpl(
			@Named("categoryLookup")
			final CategoryLookup categoryLookup,
			@Named("storeRepository")
			final StoreRepository storeRepository,
			@Named("coreBeanFactory")
			final BeanFactory coreBeanFactory,
			@Named("categoryService")
			final CategoryService categoryService) {

		this.categoryLookup = categoryLookup;
		this.categoryService = categoryService;
		this.coreBeanFactory = coreBeanFactory;
		this.storeRepository = storeRepository;

	}

	@Override
	@CacheResult
	public ExecutionResult<Collection<Category>> findRootCategories(final String storeCode) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Store store = Assign.ifSuccessful(storeRepository.findStore(storeCode));
				Catalog storeCatalog = store.getCatalog();
				Collection<Category> categories = categoryService.listRootCategories(storeCatalog, true);
				return ExecutionResultFactory.createReadOK(categories);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Category> findByGuid(final String storeCode, final String categoryGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Store store = Assign.ifSuccessful(storeRepository.findStore(storeCode));
				Catalog storeCatalog = store.getCatalog();
				Category category = Assign.ifNotNull(categoryLookup.findByCategoryAndCatalogCode(categoryGuid, storeCatalog.getCode()),
						OnFailure.returnNotFound("Navigation node was not found."));
				return ExecutionResultFactory.createReadOK(category);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Collection<Category>> findChildren(final String storeCode, final String categoryGuid) {
		Category parentCategory = Assign.ifSuccessful(findByGuid(storeCode, categoryGuid));
		List<Category> subCategories = categoryLookup.findChildren(parentCategory);
		Collections.sort(subCategories, (OrderingComparator) coreBeanFactory.getBean(ContextIdNames.ORDERING_COMPARATOR));
		return ExecutionResultFactory.<Collection<Category>>createReadOK(subCategories);
	}

	@Override
	@CacheResult
	public ExecutionResult<String> findParentCategoryCode(final String storeCode, final String childCategoryCode) {
		Category child = Assign.ifSuccessful(findByGuid(storeCode, childCategoryCode));
		Category parent = categoryLookup.findParent(child);
		if (parent != null) {
			return ExecutionResultFactory.createReadOK(parent.getCode());
		}
		return ExecutionResultFactory.createNotFound();
	}
}
