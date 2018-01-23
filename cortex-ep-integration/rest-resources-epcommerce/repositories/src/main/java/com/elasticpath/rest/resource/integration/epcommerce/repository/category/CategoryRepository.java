/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.category;

import java.util.Collection;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Repository class for parentCategory search.
 */
public interface CategoryRepository {

	/**
	 * Find root categories for Store.
	 *
	 * @param storeCode the store code
	 * @return ExecutionResult with the root categories
	 */
	ExecutionResult<Collection<Category>> findRootCategories(String storeCode);

	/**
	 * Find by category GUID.
	 *
	 * @param storeCode the store code
	 * @param categoryGuid the category GUID
	 * @return ExecutionResult with the category
	 */
	ExecutionResult<Category> findByGuid(String storeCode, String categoryGuid);

	/**
	 * Find category children.
	 *
	 * @param storeCode the store code
	 * @param parentCategoryGuid the parent category GUID
	 * @return ExecutionResult with the child categories
	 */
	ExecutionResult<Collection<Category>> findChildren(String storeCode, String parentCategoryGuid);

	/**
	 * Find parent Category.
	 * @param storeCode the store code
	 * @param childCategoryCode the child's category code.
	 * @return ExecutionResult with the parent category or null if one cannot be found.
	 */
	ExecutionResult<String> findParentCategoryCode(String storeCode, String childCategoryCode);

}
