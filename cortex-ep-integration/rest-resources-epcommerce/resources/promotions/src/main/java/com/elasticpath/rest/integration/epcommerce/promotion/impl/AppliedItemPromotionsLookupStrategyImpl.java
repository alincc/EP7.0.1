/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.promotions.integration.AppliedItemPromotionsLookupStrategy;

/**
 * Service that provides lookup of applied item promotions data from external systems.
 */
@Singleton
@Named("appliedItemPromotionsLookupStrategy")
public class AppliedItemPromotionsLookupStrategyImpl implements AppliedItemPromotionsLookupStrategy {

	private final PromotionRepository promotionRepository;

	/**
	 * Constructs.
	 *
	 * @param promotionRepository promotionRepository.
	 */
	@Inject
	public AppliedItemPromotionsLookupStrategyImpl(
			@Named("promotionRepository")
			final PromotionRepository promotionRepository) {

		this.promotionRepository = promotionRepository;
	}


	@Override
	public ExecutionResult<Collection<String>> getAppliedPromotionsForItem(final String scope, final String itemId) {
		Collection<String> appliedPromotions
				= promotionRepository.getAppliedPromotionsForItem(scope, itemId);
		return ExecutionResultFactory.createReadOK(appliedPromotions);
	}
}