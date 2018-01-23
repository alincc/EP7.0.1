/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.navigations.integration.dto.NavigationDto;
import com.elasticpath.rest.resource.navigations.integration.epcommerce.transform.CategoryTransformer;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link NavigationLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class NavigationLookupStrategyImplTest {
	private static final String THE_RESULT_SHOULD_BE_SUCCESSFUL = "The result should be successful";
	private static final String CATEGORY2_CODE = "CATEGORY2_CODE";
	private static final String CATEGORY1_CODE = "CATEGORY1_CODE";
	private static final String CATEGORY_CODE = "CATEGORY_CODE";
	private static final String STORE_CODE = "STORE_CODE";
	private static final String USER_ID = "userid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private CategoryTransformer categoryTransformer;
	@InjectMocks
	private NavigationLookupStrategyImpl navigationLookupStrategy;


	@Test
	public void testFindRootNodesWhenFound() {
		Collection<Category> categories = createCategoriesList();
		when(categoryRepository.findRootCategories(STORE_CODE)).thenReturn(ExecutionResultFactory.createReadOK(categories));

		ExecutionResult<Collection<String>> rootNodesResult = navigationLookupStrategy.findRootNodeIds(STORE_CODE);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, rootNodesResult.isSuccessful());
		assertTrue("THe collection should contain the categories, as expected",
			CollectionUtil.containsOnly(rootNodesResult.getData(), Arrays.asList(CATEGORY1_CODE, CATEGORY2_CODE)));
	}

	@Test
	public void testFindRootNodesWhenNotFound() {
		when(categoryRepository.findRootCategories(STORE_CODE)).thenReturn(ExecutionResultFactory.<Collection<Category>>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		navigationLookupStrategy.findRootNodeIds(STORE_CODE);
	}

	@Test
	public void testGettingChildNodes() {
		NavigationDto dto = mock(NavigationDto.class);
		Category category = createMockCategory();
		Collection<Category> categories = createCategoriesList();
		shouldFindSubject();
		when(categoryRepository.findByGuid(STORE_CODE, CATEGORY_CODE)).thenReturn(ExecutionResultFactory.createReadOK(category));
		when(categoryRepository.findChildren(STORE_CODE, CATEGORY_CODE)).thenReturn(ExecutionResultFactory.createReadOK(categories));
		shouldTransformCategoryToEntity(category, categories, dto);

		ExecutionResult<NavigationDto> result = navigationLookupStrategy.find(STORE_CODE, CATEGORY_CODE);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("The result should be a navigation dto as expected", dto, result.getData());
	}

	@Test
	public void testFindWhereCategoryNotFound() {
		when(categoryRepository.findByGuid(STORE_CODE, CATEGORY_CODE)).thenReturn(ExecutionResultFactory.<Category>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		navigationLookupStrategy.find(STORE_CODE, CATEGORY_CODE);
	}


	private void shouldFindSubject() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USER_ID, Locale.ENGLISH);
		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}


	private void shouldTransformCategoryToEntity(final Category category, final Collection<Category> categories, final NavigationDto dto) {
		when(categoryTransformer.transformToEntity(category, categories, Locale.ENGLISH, STORE_CODE)).thenReturn(dto);
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
