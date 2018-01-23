/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.recommendations.items.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Store;
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
import com.elasticpath.rest.resource.recommendations.integration.ItemRecommendationsLookupStrategy;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Lookup strategy for item recommendations through CE.
 */
@Singleton
@Named("itemRecommendationsLookupStrategy")
public class ItemRecommendationsLookupStrategyImpl implements ItemRecommendationsLookupStrategy {

	private final ItemRecommendationsRepository itemRecommendationsRepository;
	private final ItemRepository itemRepository;
	private final StoreRepository storeRepository;
	private final AbstractDomainTransformer<ProductAssociationType, RecommendationsEntity> recommendationsTransformer;
	private final PaginatedResultTransformer paginatedResultTransformer;
	private final RecommendationsGroupMapper<ProductAssociationType> recommendationsGroupMapper;

	/**
	 * Constructor.
	 *
	 * @param itemRecommendationsRepository item recommendations repository.
	 * @param itemRepository                item repository.
	 * @param storeRepository               store repository.
	 * @param paginatedResultTransformer    paginated result transformer.
	 * @param recommendationsGroupMapper    recommendations group mapper.
	 * @param recommendationsTransformer    recommendations transformer.
	 */
	@Inject
	public ItemRecommendationsLookupStrategyImpl(
			@Named("itemRecommendationsRepository")
			final ItemRecommendationsRepository itemRecommendationsRepository,
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("storeRepository")
			final StoreRepository storeRepository,
			@Named("paginatedResultTransformer")
			final PaginatedResultTransformer paginatedResultTransformer,
			@Named("recommendationsGroupMapper")
			final RecommendationsGroupMapper<ProductAssociationType> recommendationsGroupMapper,
			@Named("recommendationsTransformer")
			final AbstractDomainTransformer<ProductAssociationType, RecommendationsEntity> recommendationsTransformer) {

		this.itemRepository = itemRepository;
		this.storeRepository = storeRepository;
		this.itemRecommendationsRepository = itemRecommendationsRepository;
		this.paginatedResultTransformer = paginatedResultTransformer;
		this.recommendationsTransformer = recommendationsTransformer;
		this.recommendationsGroupMapper = recommendationsGroupMapper;
	}

	@Override
	public ExecutionResult<Collection<RecommendationsEntity>> getRecommendations(final String scope, final String itemId) {

		Collection<ProductAssociationType> productAssociationTypes =
				Assign.ifSuccessful(itemRecommendationsRepository.getRecommendationGroups());
		Collection<RecommendationsEntity> recommendationGroups = new ArrayList<>();
		try {
			for (ProductAssociationType type : productAssociationTypes) {
				RecommendationsEntity recommendationGroup = recommendationsTransformer.transformToEntity(type);
				recommendationGroups.add(recommendationGroup);
			}
		} catch (IllegalArgumentException exception) {
			return ExecutionResultFactory.createServerError("Invalid recommendation group mapping");
		}

		return ExecutionResultFactory.createReadOK(recommendationGroups);
	}

	@Override
	public ExecutionResult<PaginationDto> getRecommendedItemsFromGroup(final String scope, final String itemId,
																		final String recommendationGroup, final int pageNumber) {

		String integrationRecommendationGroup;
		try {
			integrationRecommendationGroup = recommendationsGroupMapper.fromCortexToCommerce(recommendationGroup).getName();
		} catch (IllegalArgumentException exception) {
			return ExecutionResultFactory.createNotFound("Recommendation group not found");
		}
		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
		Product sourceProduct = productSku.getProduct();
		Store store = Assign.ifSuccessful(storeRepository.findStore(scope));
		PaginatedResult paginatedResult =
				Assign.ifSuccessful(itemRecommendationsRepository.getRecommendedItemsFromGroup(
						store, sourceProduct, integrationRecommendationGroup, pageNumber));
		PaginationDto paginationDto = paginatedResultTransformer.transformToEntity(paginatedResult);

		return ExecutionResultFactory.createReadOK(paginationDto);
	}
}
