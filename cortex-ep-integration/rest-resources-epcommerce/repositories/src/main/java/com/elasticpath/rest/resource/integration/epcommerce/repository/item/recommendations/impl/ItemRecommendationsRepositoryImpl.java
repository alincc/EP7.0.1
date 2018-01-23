/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.item.recommendations.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.recommendations.ItemRecommendationsRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Repository for gathering sourceProduct associations.
 */
@Singleton
@Named("itemRecommendationsRepository")
public class ItemRecommendationsRepositoryImpl implements ItemRecommendationsRepository {

	private static final Logger LOG = LoggerFactory.getLogger(ItemRecommendationsRepositoryImpl.class);
	private final ProductAssociationService productAssociationService;
	private final ItemRepository itemRepository;
	private final RecommendedItemsPageSizeResolver pageSizeResolver;

	/**
	 * Creates instance with needed services wired in.
	 *
	 * @param productAssociationService the product association service
	 * @param itemRepository the item repository
	 * @param pageSizeResolver the resolver for page size
	 */
	@Inject
	public ItemRecommendationsRepositoryImpl(
			@Named("productAssociationService")
			final ProductAssociationService productAssociationService,
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("recommendedItemsPageSizeResolver")
			final RecommendedItemsPageSizeResolver pageSizeResolver) {
		this.productAssociationService = productAssociationService;
		this.itemRepository = itemRepository;
		this.pageSizeResolver = pageSizeResolver;
	}

	@Override
	public ExecutionResult<Collection<ProductAssociationType>> getRecommendationGroups() {
		Collection<ProductAssociationType> recommendationGroups = ProductAssociationType.values();
		return ExecutionResultFactory.createReadOK(recommendationGroups);
	}

	@Override
	public ExecutionResult<PaginatedResult> getRecommendedItemsFromGroup(final Store store, final Product sourceProduct,
			final String recommendationGroup, final int pageNumber) {
		ExecutionResult<PaginatedResult> result;
		try {
			ProductAssociationType productAssociationType = ProductAssociationType.fromName(recommendationGroup);

			ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
			criteria.setAssociationType(productAssociationType);
			criteria.setSourceProduct(sourceProduct);
			criteria.setCatalogCode(store.getCatalog().getCode());
			criteria.setWithinCatalogOnly(true);
			Date now = new Date();
			criteria.setStartDateBefore(now);
			criteria.setEndDateAfter(now);

			int pageSize = pageSizeResolver.getPageSize();
			int startIndex = (pageNumber - 1) * pageSize;

			int totalResultCount = productAssociationService.findCountForCriteria(criteria).intValue();
			Collection<String> recommendedItemIds = Collections.emptyList();
			if (totalResultCount > 0) {
				Collection<ProductAssociation> associations = productAssociationService.findByCriteria(criteria, startIndex, pageSize);
				recommendedItemIds = extractRecommendedItemIds(associations);
			}

			result = ExecutionResultFactory.createReadOK(
					new PaginatedResult(recommendedItemIds, pageNumber, pageSize, totalResultCount));
		} catch (Exception exception) {
			LOG.error("Error when searching for item associations", exception);
			result = ExecutionResultFactory.createServerError("Server error when searching for item ids");
		}
		return result;
	}

	private Collection<String> extractRecommendedItemIds(final Collection<ProductAssociation> associations) {
		Collection<String> recommendedItemIds = new ArrayList<>(associations.size());
		for (ProductAssociation association : associations) {
			Product product = association.getTargetProduct();
			String itemId = Assign.ifSuccessful(itemRepository.getDefaultItemIdForProduct(product));
			recommendedItemIds.add(itemId);
		}
		return recommendedItemIds;
	}
}
