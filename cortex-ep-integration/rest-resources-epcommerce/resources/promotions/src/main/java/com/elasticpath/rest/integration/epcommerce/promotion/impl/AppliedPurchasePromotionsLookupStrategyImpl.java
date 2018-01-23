/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.Order;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.promotions.integration.AppliedPurchasePromotionsLookupStrategy;

/**
 * Service that provides lookup of applied line item promotions data from external systems.
 */
@Singleton
@Named("appliedPurchasePromotionsLookupStrategy")
public class AppliedPurchasePromotionsLookupStrategyImpl implements AppliedPurchasePromotionsLookupStrategy {
	private final OrderRepository orderRepository;
	private final PromotionRepository promotionRepository;

	/**
	 * Constructs.
	 * @param orderRepository orderRepository.
	 * @param promotionRepository promotionRepository.
	 */
	@Inject
	public AppliedPurchasePromotionsLookupStrategyImpl(
			@Named("orderRepository")
			final OrderRepository orderRepository,
			@Named("promotionRepository")
			final PromotionRepository promotionRepository) {
		this.orderRepository = orderRepository;
		this.promotionRepository = promotionRepository;
	}

	@Override
	public ExecutionResult<Collection<String>> getAppliedPromotionsForPurchase(final String scope, final String decodedPurchaseId) {
		Order order = Assign.ifSuccessful(orderRepository.findByGuid(scope, decodedPurchaseId));
		Collection<String> appliedPromotions
				= promotionRepository.getAppliedPromotionsForPurchase(order);
		return ExecutionResultFactory.createReadOK(appliedPromotions);
	}
}