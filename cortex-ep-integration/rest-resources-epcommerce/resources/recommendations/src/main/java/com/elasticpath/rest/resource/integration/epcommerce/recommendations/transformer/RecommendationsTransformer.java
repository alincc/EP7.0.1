/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.recommendations.transformer;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.integration.epcommerce.recommendations.mapper.RecommendationsGroupMapper;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transform from Product Association to Recommendation (Group).
 */
@Singleton
@Named("recommendationsTransformer")
public class RecommendationsTransformer extends AbstractDomainTransformer<ProductAssociationType, RecommendationsEntity> {

	private final RecommendationsGroupMapper<ProductAssociationType> recommendationsGroupMapper;

	/**
	 * Configure injected dependencies.
	 *
	 * @param recommendationsGroupMapper used to map to a cortex-specific group name
	 */
	@Inject
	public RecommendationsTransformer(
			@Named("recommendationsGroupMapper")
			final RecommendationsGroupMapper<ProductAssociationType> recommendationsGroupMapper) {
		this.recommendationsGroupMapper = recommendationsGroupMapper;
	}

	@Override
	public ProductAssociationType transformToDomain(final RecommendationsEntity resourceEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public RecommendationsEntity transformToEntity(final ProductAssociationType type, final Locale locale) {
		return RecommendationsEntity.builder()
				.withName(recommendationsGroupMapper.fromCommerceToCortex(type))
				.build();
	}

}
