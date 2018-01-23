/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.item.recommendations.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.test.AssertExecutionResult;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Unit tests for {@link ItemRecommendationsRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemRecommendationsRepositoryImplTest {

	private static final String SCOPE = "scope";
	private static final String GROUP = "upsell";
	private static final int PAGE_SIZE = 10;
	private static final String ITEM_ID = "itemId";

	@Mock
	ProductAssociationService productAssociationService;
	@InjectMocks
	private ItemRecommendationsRepositoryImpl repository;
	@Mock
	Store store;
	@Mock
	Catalog catalog;
	@Mock
	Product product;
	@Mock
	ProductAssociation productAssociation;
	@Mock
	RecommendedItemsPageSizeResolver paginationResolver;
	@Mock
	ItemRepository itemRepository;

	@Before
	public void setUp() {
		when(store.getCatalog()).thenReturn(catalog);
		when(catalog.getCode()).thenReturn(SCOPE);
		when(paginationResolver.getPageSize()).thenReturn(PAGE_SIZE);
		when(productAssociation.getTargetProduct()).thenReturn(product);
		when(itemRepository.getDefaultItemIdForProduct(any(Product.class))).thenReturn(ExecutionResultFactory.createReadOK(ITEM_ID));
	}

	@Test
	public void testGettingRecommendationGroups() {

		ExecutionResult<Collection<ProductAssociationType>> result = repository.getRecommendationGroups();

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(result.getData());
	}

	@Test
	public void testGettingRecommendedItemsWhenZeroReturned() {
		List<ProductAssociation> expectedAssociations = Collections.emptyList();
		when(productAssociationService.findByCriteria(any(ProductAssociationSearchCriteria.class),
				any(Integer.class), any(Integer.class))).thenReturn(expectedAssociations);

		ExecutionResult<PaginatedResult> result = repository.getRecommendedItemsFromGroup(store, product, GROUP, 1);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(result.getData());
	}

	@Test
	public void testGettingRecommendedItemsWhenOneReturned() {
		List<ProductAssociation> expectedAssociations = Collections.singletonList(productAssociation);
		when(productAssociationService.findByCriteria(any(ProductAssociationSearchCriteria.class),
				any(Integer.class), any(Integer.class))).thenReturn(expectedAssociations);
		when(productAssociationService.findCountForCriteria(any(ProductAssociationSearchCriteria.class))).thenReturn(Long.valueOf(1));

		ExecutionResult<PaginatedResult> result = repository.getRecommendedItemsFromGroup(store, product, GROUP, 1);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(result.getData());
	}

	@Test
	public void testGettingRecommendedItemsWhenServiceError() {
		when(productAssociationService.findCountForCriteria(any(ProductAssociationSearchCriteria.class))).thenReturn(Long.valueOf(1));
		when(productAssociationService.findByCriteria(any(ProductAssociationSearchCriteria.class),
				any(Integer.class), any(Integer.class))).thenThrow(new EpServiceException(""));

		ExecutionResult<PaginatedResult> result = repository.getRecommendedItemsFromGroup(store, product, GROUP, 1);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.SERVER_ERROR);
	}
}
