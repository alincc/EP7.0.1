/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.recommendations.items.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.integration.epcommerce.recommendations.mapper.RecommendationsGroupMapper;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.recommendations.ItemRecommendationsRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResultTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Lookup strategy for item recommendations through CSV data.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemRecommendationsLookupStrategyImplTest {

	private static final String SCOPE = "scope";
	private static final String DECODED_ITEM_ID = "decoded-item-id";
	private static final String RECOMMENDATIONS_GROUP = "updownleftrightsells";
	private static final String OH_SNAP = "Bad repository test.";
	private final Collection<ProductAssociationType> productAssociationTypes = new ArrayList<>();
	private final Collection<ProductAssociation> productAssociations = new ArrayList<>();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ItemRepository itemRepository;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private AbstractDomainTransformer<ProductAssociationType, RecommendationsEntity> recommendationsTransformer;
	@Mock
	private RecommendationsGroupMapper<ProductAssociationType> recommendationsGroupMapper;
	@Mock
	private ItemRecommendationsRepository itemRecommendationsRepository;
	@Mock
	private PaginatedResultTransformer paginatedResultTransformer;
	private ItemRecommendationsLookupStrategyImpl strategy;
	@Mock
	private ProductAssociationType productAssociationType1;
	@Mock
	private ProductAssociationType productAssociationType2;
	@Mock
	private RecommendationsEntity recommendationGroup1;
	@Mock
	private RecommendationsEntity recommendationGroup2;
	@Mock
	private Store store;
	@Mock
	private ProductSku productSku;
	@Mock
	private Product product;
	@Mock
	private ProductAssociation productAssociation1;
	@Mock
	private ProductAssociation productAssociation2;

	@Before
	public void setUp() {
		when(storeRepository.findStore(SCOPE)).thenReturn(ExecutionResultFactory.createReadOK(store));
		when(itemRepository.getSkuForItemId(DECODED_ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(productSku));
		when(productSku.getProduct()).thenReturn(product);
		when(productAssociation1.getTargetProduct()).thenReturn(product);
		when(productAssociation2.getTargetProduct()).thenReturn(product);
		when(itemRepository.getDefaultItemIdForProduct(product)).thenReturn(ExecutionResultFactory.createReadOK(DECODED_ITEM_ID));
		when(recommendationsGroupMapper.fromCortexToCommerce(RECOMMENDATIONS_GROUP)).thenReturn(productAssociationType1);
		when(productAssociationType1.getName()).thenReturn(RECOMMENDATIONS_GROUP);
		when(recommendationsGroupMapper.fromCommerceToCortex(productAssociationType1)).thenReturn(RECOMMENDATIONS_GROUP);
		// setup here as InjectMocks has occasional issues with multiple transformers
		strategy = new ItemRecommendationsLookupStrategyImpl(itemRecommendationsRepository, itemRepository,
				storeRepository, paginatedResultTransformer, recommendationsGroupMapper, recommendationsTransformer);
	}

	@Test
	public void testGettingZeroRecommendationGroups() {
		arrangeRecommendationsToBeReturnedFromRepository();

		ExecutionResult<Collection<RecommendationsEntity>> result = strategy.getRecommendations(SCOPE, DECODED_ITEM_ID);

		assertExecutionResult(result).isSuccessful().data(Collections.EMPTY_LIST);
	}

	@Test
	public void testGettingOneRecommendationGroups() {
		arrangeRecommendationsToBeReturnedFromRepository();
		productAssociationTypes.add(productAssociationType1);
		arrangeTransformerToReturnTransformedRecommendationGroups();

		ExecutionResult<Collection<RecommendationsEntity>> result = strategy.getRecommendations(SCOPE, DECODED_ITEM_ID);

		assertExecutionResult(result).isSuccessful();

		assertTrue(CollectionUtil.areSame(Arrays.asList(recommendationGroup1), result.getData()));
	}

	@Test
	public void testGettingManyRecommendationGroups() {
		arrangeRecommendationsToBeReturnedFromRepository();
		productAssociationTypes.add(productAssociationType1);
		productAssociationTypes.add(productAssociationType2);
		arrangeTransformerToReturnTransformedRecommendationGroups();

		ExecutionResult<Collection<RecommendationsEntity>> result = strategy.getRecommendations(SCOPE, DECODED_ITEM_ID);

		assertExecutionResult(result).isSuccessful();

		assertTrue(CollectionUtil.areSame(Arrays.asList(recommendationGroup1, recommendationGroup2), result.getData()));
	}

	@Test
	public void testGettingZeroRecommendedItems() {
		PaginatedResult searchResult = new PaginatedResult(new ArrayList<String>(), 1, 0, 0);
		when(itemRecommendationsRepository.getRecommendedItemsFromGroup(store, product, RECOMMENDATIONS_GROUP, 1))
				.thenReturn(ExecutionResultFactory.createReadOK(searchResult));
		PaginationDto expectedPaginationDto = createPaginationDto(productAssociations);
		when(paginatedResultTransformer.transformToEntity(searchResult)).thenReturn(expectedPaginationDto);

		ExecutionResult<PaginationDto> result =
				strategy.getRecommendedItemsFromGroup(SCOPE, DECODED_ITEM_ID, RECOMMENDATIONS_GROUP, 1);

		assertExecutionResult(result)
				.isSuccessful()
				.data(expectedPaginationDto);
	}

	@Test
	public void testGettingOneRecommendedItems() {
		PaginatedResult searchResult = new PaginatedResult(Collections.singleton(DECODED_ITEM_ID), 1, 1, 1);
		productAssociations.add(productAssociation1);
		when(itemRecommendationsRepository.getRecommendedItemsFromGroup(store, product, RECOMMENDATIONS_GROUP, 1))
				.thenReturn(ExecutionResultFactory.createReadOK(searchResult));
		PaginationDto expectedPaginationDto = createPaginationDto(productAssociations);
		when(paginatedResultTransformer.transformToEntity(searchResult)).thenReturn(expectedPaginationDto);

		ExecutionResult<PaginationDto> result =
				strategy.getRecommendedItemsFromGroup(SCOPE, DECODED_ITEM_ID, RECOMMENDATIONS_GROUP, 1);

		assertExecutionResult(result)
				.isSuccessful()
				.data(expectedPaginationDto);
	}

	@Test
	public void testGettingManyRecommendedItems() {
		List<String> items = Arrays.asList(DECODED_ITEM_ID, DECODED_ITEM_ID);
		PaginatedResult searchResult = new PaginatedResult(items, 1, items.size(), items.size());
		productAssociations.add(productAssociation1);
		productAssociations.add(productAssociation2);
		when(itemRecommendationsRepository.getRecommendedItemsFromGroup(store, product, RECOMMENDATIONS_GROUP, 1))
				.thenReturn(ExecutionResultFactory.createReadOK(searchResult));
		PaginationDto expectedPaginationDto = createPaginationDto(productAssociations);
		when(paginatedResultTransformer.transformToEntity(searchResult)).thenReturn(expectedPaginationDto);

		ExecutionResult<PaginationDto> result =
				strategy.getRecommendedItemsFromGroup(SCOPE, DECODED_ITEM_ID, RECOMMENDATIONS_GROUP, 1);

		assertExecutionResult(result)
				.isSuccessful()
				.data(expectedPaginationDto);
	}

	@Test
	public void testGettingRecommendedItemsWhenServerError() {
		when(itemRecommendationsRepository.getRecommendedItemsFromGroup(store, product, RECOMMENDATIONS_GROUP, 1))
				.thenReturn(ExecutionResultFactory.<PaginatedResult>createServerError(OH_SNAP));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		strategy.getRecommendedItemsFromGroup(SCOPE, DECODED_ITEM_ID, RECOMMENDATIONS_GROUP, 1);
	}

	@Test
	public void testGettingRecommendedItemsGroupMappingFailure() {
		when(recommendationsGroupMapper.fromCortexToCommerce(RECOMMENDATIONS_GROUP)).thenThrow(IllegalArgumentException.class);

		ExecutionResult<PaginationDto> result =
				strategy.getRecommendedItemsFromGroup(SCOPE, DECODED_ITEM_ID, RECOMMENDATIONS_GROUP, 1);

		assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testGettingRecommendationGroupsMappingFailure() {
		arrangeRecommendationsToBeReturnedFromRepository();
		productAssociationTypes.add(productAssociationType1);
		when(recommendationsTransformer.transformToEntity(productAssociationType1)).thenThrow(IllegalArgumentException.class);

		ExecutionResult<Collection<RecommendationsEntity>> result = strategy.getRecommendations(SCOPE, DECODED_ITEM_ID);

		assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	/*
	 * Create a pagination Dto with one page regardless of result size
	 */
	private PaginationDto createPaginationDto(final Collection<ProductAssociation> associations) {
		ArrayList<String> recommendedItemIds = new ArrayList<>();
		for (ProductAssociation association : associations) {
			Product product = association.getTargetProduct();
			String itemId = Assign.ifSuccessful(itemRepository.getDefaultItemIdForProduct(product));
			recommendedItemIds.add(itemId);
		}
		return ResourceTypeFactory.createResourceEntity(PaginationDto.class)
				.setCurrentPage(1)
				.setNumberOfPages(1)
				.setTotalResultsFound(associations.size())
				.setNumberOfResultsOnPage(associations.size())
				.setPageSize(associations.size())
				.setPageResults(recommendedItemIds);
	}

	private void arrangeRecommendationsToBeReturnedFromRepository() {
		when(itemRecommendationsRepository.getRecommendationGroups()).thenReturn(ExecutionResultFactory.createReadOK(productAssociationTypes));
	}

	private void arrangeTransformerToReturnTransformedRecommendationGroups() {
		when(recommendationsTransformer.transformToEntity(productAssociationType1)).thenReturn(recommendationGroup1);
		when(recommendationsTransformer.transformToEntity(productAssociationType2)).thenReturn(recommendationGroup2);

	}
}
