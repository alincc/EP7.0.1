/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.navigations.integration.NavigationLookupStrategy;
import com.elasticpath.rest.resource.navigations.integration.dto.NavigationDto;
import com.elasticpath.rest.resource.navigations.integration.epcommerce.transform.CategoryTransformer;

/**
 * Service that provides lookup of navigation data from external systems.
 */
@Singleton
@Named("navigationLookupStrategy")
public class NavigationLookupStrategyImpl implements NavigationLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final CategoryTransformer categoryTransformer;
	private final CategoryRepository categoryRepository;


	/**
	 * Default constructor.
	 *
	 * @param resourceOperationContext the resource operation context.
	 * @param categoryRepository the category repository.
	 * @param categoryTransformer the category transformer.
	 */
	@Inject
	NavigationLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("categoryRepository")
			final CategoryRepository categoryRepository,
			@Named("categoryTransformer")
			final CategoryTransformer categoryTransformer) {

		this.resourceOperationContext = resourceOperationContext;
		this.categoryRepository = categoryRepository;
		this.categoryTransformer = categoryTransformer;
	}

	@Override
	public ExecutionResult<Collection<String>> findRootNodeIds(final String storeCode) {
		Collection<Category> rootCategories = Assign.ifSuccessful(categoryRepository.findRootCategories(storeCode));
		Collection<String> categoryGuids = new ArrayList<>(rootCategories.size());

		for (Category category : rootCategories) {
			categoryGuids.add(category.getCode());
		}

		return ExecutionResultFactory.createReadOK(categoryGuids);
	}

	@Override
	public ExecutionResult<NavigationDto> find(final String storeCode, final String categoryGuid) {
		Category category = Assign.ifSuccessful(categoryRepository.findByGuid(storeCode, categoryGuid));
		Collection<Category> subCategories = Assign.ifSuccessful(categoryRepository.findChildren(storeCode, categoryGuid));

		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		NavigationDto navigationDto = categoryTransformer.transformToEntity(category, subCategories, locale, storeCode);

		return ExecutionResultFactory.createReadOK(navigationDto);
	}
}
