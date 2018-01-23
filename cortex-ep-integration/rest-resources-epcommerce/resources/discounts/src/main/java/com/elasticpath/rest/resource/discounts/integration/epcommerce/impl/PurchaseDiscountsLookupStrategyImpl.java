/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.domain.order.Order;
import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.resource.discounts.integration.PurchaseDiscountsLookupStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * The Lookup Strategy for Purchase Discounts resource.
 */
@Named("purchaseDiscountsLookupStrategy")
public class PurchaseDiscountsLookupStrategyImpl implements PurchaseDiscountsLookupStrategy {

	private final OrderRepository orderRepository;
	private final MoneyTransformer moneyTransformer;

	/**
	 * Constructor.
	 * @param orderRepository the order repository
	 * @param moneyTransformer the money transformer
	 */
	@Inject
	public PurchaseDiscountsLookupStrategyImpl(
			@Named("orderRepository") final OrderRepository orderRepository,
			@Named("moneyTransformer") final MoneyTransformer moneyTransformer) {
		this.orderRepository = orderRepository;
		this.moneyTransformer = moneyTransformer;
	}


	@Override
	public ExecutionResult<DiscountEntity> getPurchaseDiscounts(final String purchaseIdDecoded, final String scope) {
		return new ExecutionResultChain() {
			@Override
			public ExecutionResult<?> build() {
				Order order = Assign.ifSuccessful(orderRepository.findByGuid(scope, purchaseIdDecoded));
				Money subtotalDiscountMoney = order.getSubtotalDiscountMoney();
				CostEntity discountCostEntity = moneyTransformer.transformToEntity(subtotalDiscountMoney, order.getLocale());
				DiscountEntity discountEntity = DiscountEntity.builder()
						.addingDiscount(discountCostEntity)
						.build();
				return ExecutionResultFactory.createReadOK(discountEntity);
			}
		}.execute();
	}
}
