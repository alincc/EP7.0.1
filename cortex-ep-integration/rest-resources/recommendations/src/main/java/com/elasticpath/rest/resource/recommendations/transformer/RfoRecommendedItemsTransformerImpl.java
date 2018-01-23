/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.pagination.transform.PaginatedLinksTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * The transformer for converting a list of recommended items.
 */
@Singleton
@Named("rfoRecommendedItemsTransformer")
public final class RfoRecommendedItemsTransformerImpl implements RfoRecommendedItemsTransformer {

	private final RecommendationsUriBuilderFactory recommendationsUriBuilderFactory;
	private final PaginatedLinksTransformer paginatedLinksTransformer;

	/**
	 * Default Constructor.
	 *
	 * @param paginatedLinksTransformer the paginated links transformer
	 * @param recommendationsUriBuilderFactory the recommendations URI builder factory
	 */
	@Inject
	public RfoRecommendedItemsTransformerImpl(
			@Named("paginatedLinksTransformer")
			final PaginatedLinksTransformer paginatedLinksTransformer,
			@Named("recommendationsUriBuilderFactory")
			final RecommendationsUriBuilderFactory recommendationsUriBuilderFactory) {

		this.paginatedLinksTransformer = paginatedLinksTransformer;
		this.recommendationsUriBuilderFactory = recommendationsUriBuilderFactory;
	}

	@Override
	public ResourceState<PaginatedLinksEntity> transformToRepresentation(final PaginationDto recommendedItems,
			final ResourceState<?> otherResourceState,
			final String recommendationGroup) {

		String otherUri = ResourceStateUtil.getSelfUri(otherResourceState);
		String baseURI = recommendationsUriBuilderFactory.get()
				.setSourceUri(otherUri)
				.setRecommendationGroup(recommendationGroup)
				.build();

		return paginatedLinksTransformer.transformToResourceState(recommendedItems, otherResourceState.getScope(), baseURI);
	}
}
