/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.resource.discounts.integration.CartDiscountsLookupStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * The Lookup for Discounts resource.
 */
@Named("cartDiscountsLookupStrategy")
public class CartDiscountsLookupStrategyImpl implements CartDiscountsLookupStrategy {

	private final CartOrderRepository cartOrderRepository;
	private final CustomerSessionRepository customerSessionRepository;
	private final MoneyTransformer moneyTransformer;
	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Constructor.
	 * @param cartOrderRepository the cart order repository
	 * @param customerSessionRepository the customer session repository
	 * @param moneyTransformer the money transformer
	 * @param pricingSnapshotRepository the pricing snapshot repository
	 */
	@Inject
	public CartDiscountsLookupStrategyImpl(
			@Named("cartOrderRepository") final CartOrderRepository cartOrderRepository,
			@Named("customerSessionRepository") final CustomerSessionRepository customerSessionRepository,
			@Named("moneyTransformer") final MoneyTransformer moneyTransformer,
			@Named("pricingSnapshotRepository") final PricingSnapshotRepository pricingSnapshotRepository) {
		this.cartOrderRepository = cartOrderRepository;
		this.customerSessionRepository = customerSessionRepository;
		this.moneyTransformer = moneyTransformer;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}


	@Override
	public ExecutionResult<DiscountEntity> getCartDiscounts(final String cartGuid, final String scope) {

		ShoppingCart shoppingCart = getShoppingCart(cartGuid, scope);
		CustomerSession customerSession = Assign.ifSuccessful(customerSessionRepository.findOrCreateCustomerSession());
		ShoppingCartPricingSnapshot pricingSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart));

		Money subtotalDiscountMoney = pricingSnapshot.getSubtotalDiscountMoney();
		CostEntity discountCostEntity = moneyTransformer.transformToEntity(subtotalDiscountMoney, customerSession.getLocale());
		DiscountEntity discountEntity
			= DiscountEntity.builder()
				.addingDiscount(discountCostEntity)
				.build();
		return ExecutionResultFactory.createReadOK(discountEntity);
	}

	private ShoppingCart getShoppingCart(final String cartGuid, final String scope) {
		return Assign.ifSuccessful(cartOrderRepository.getEnrichedShoppingCart(scope, cartGuid, CartOrderRepository.FindCartOrder.BY_CART_GUID));
	}
}
