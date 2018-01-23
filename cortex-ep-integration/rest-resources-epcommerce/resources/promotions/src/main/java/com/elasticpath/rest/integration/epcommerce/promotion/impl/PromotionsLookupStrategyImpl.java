/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.promotions.integration.PromotionsLookupStrategy;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Lookup strategy for promotions through CSV data.
 */
@Singleton
@Named("promotionsLookupStrategy")
public class PromotionsLookupStrategyImpl implements PromotionsLookupStrategy {

	private final PromotionRepository promotionRepository;
	private final AbstractDomainTransformer<Rule, PromotionEntity> promotionTransformer;
	private final AbstractDomainTransformer<AppliedRule, PromotionEntity> appliedPromotionTransformer;
	private final OrderRepository orderRepository;

	private final ResourceOperationContext resourceOperationContext;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param promotionRepository the promotionRepository
	 * @param promotionTransformer promotion transformer
	 * @param appliedPromotionTransformer applied promotion transformer
	 * @param orderRepository order repository
	 */
	@Inject
	public PromotionsLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("promotionRepository")
			final PromotionRepository promotionRepository,
			@Named("promotionTransformer")
			final AbstractDomainTransformer<Rule, PromotionEntity> promotionTransformer,
			@Named("appliedPromotionTransformer")
			final AbstractDomainTransformer<AppliedRule, PromotionEntity> appliedPromotionTransformer,
			@Named("orderRepository")
			final OrderRepository orderRepository) {

		this.resourceOperationContext = resourceOperationContext;
		this.promotionRepository = promotionRepository;
		this.promotionTransformer = promotionTransformer;
		this.appliedPromotionTransformer = appliedPromotionTransformer;
		this.orderRepository = orderRepository;
	}

	@Override
	public ExecutionResult<PromotionEntity> getPromotionById(final String scope, final String decodedPromotionId) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		Rule rule = Assign.ifSuccessful(promotionRepository.findByPromotionId(decodedPromotionId));
		PromotionEntity promotionEntity = promotionTransformer.transformToEntity(rule, locale);
		return ExecutionResultFactory.createReadOK(promotionEntity);
	}


	@Override
	public ExecutionResult<PromotionEntity> getPromotionForPurchase(final String scope, final String decodedPromotionId,
																	final String decodedPurchaseId) {
		Order order = Assign.ifSuccessful(orderRepository.findByGuid(scope, decodedPurchaseId));

		for (AppliedRule appliedRule: order.getAppliedRules()) {
			if (appliedRule.getGuid().equals(decodedPromotionId)) {
				PromotionEntity appliedPromotion = appliedPromotionTransformer.transformToEntity(appliedRule);
				return ExecutionResultFactory.createReadOK(appliedPromotion);
			}
		}
		return ExecutionResultFactory.createNotFound("Applied promotion not found for purchase.");
	}
}