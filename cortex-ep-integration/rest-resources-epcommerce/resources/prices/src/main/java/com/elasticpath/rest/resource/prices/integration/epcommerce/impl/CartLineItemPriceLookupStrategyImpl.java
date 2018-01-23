/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.prices.integration.CartLineItemPriceLookupStrategy;
import com.elasticpath.rest.resource.prices.integration.epcommerce.domain.wrapper.MoneyWrapper;
import com.elasticpath.rest.resource.prices.integration.epcommerce.transformer.MoneyWrapperTransformer;

/**
 * Look up cart line item prices.
 */
@Singleton
@Named("cartLineItemPriceLookupStrategy")
public class CartLineItemPriceLookupStrategyImpl implements CartLineItemPriceLookupStrategy {

	private static final String LINEITEM_NOT_FOUND = "Line item was not found with GUID = '%s'";

	private final MoneyWrapperTransformer moneyWrapperTransformer;
	private final CartOrderRepository cartOrderRepository;
	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Constructor.
	 * @param cartOrderRepository cartOrderRepository.
	 * @param moneyWrapperTransformer the line item price transformer
	 * @param pricingSnapshotRepository the pricing snapshot repository
	 */
	@Inject
	CartLineItemPriceLookupStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("moneyWrapperTransformer")
			final MoneyWrapperTransformer moneyWrapperTransformer,
			@Named("pricingSnapshotRepository")
			final PricingSnapshotRepository pricingSnapshotRepository) {
		this.cartOrderRepository = cartOrderRepository;
		this.moneyWrapperTransformer = moneyWrapperTransformer;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Override
	public ExecutionResult<CartLineItemPriceEntity> getLineItemPrice(final String scope, final String cartGuid, final String shoppingItemGuid) {

		ShoppingCart cart = Assign.ifSuccessful(
					cartOrderRepository.getEnrichedShoppingCart(scope, cartGuid, CartOrderRepository.FindCartOrder.BY_CART_GUID));
		ShoppingCartPricingSnapshot cartPricingSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getShoppingCartPricingSnapshot(cart));
		ShoppingItem shoppingItem = Assign.ifNotNull(cart.getShoppingItemByGuid(shoppingItemGuid),
				OnFailure.returnNotFound(LINEITEM_NOT_FOUND, shoppingItemGuid));
		ShoppingItemPricingSnapshot itemPricingSnapshot = cartPricingSnapshot.getShoppingItemPricingSnapshot(shoppingItem);
		Money listPrice = itemPricingSnapshot.getPriceCalc().forUnitPrice().getMoney();
		Money purchasePrice = itemPricingSnapshot.getPriceCalc().forUnitPrice().withCartDiscounts().getMoney();
		MoneyWrapper lineItemPrice = new MoneyWrapper();
		lineItemPrice.setPurchasePrice(purchasePrice);
		lineItemPrice.setListPrice(listPrice);
		CartLineItemPriceEntity cartLineItemPrice = moneyWrapperTransformer.transformToEntity(lineItemPrice);
		return ExecutionResultFactory.createReadOK(cartLineItemPrice);
	}

}
