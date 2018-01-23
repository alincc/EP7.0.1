/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository.FindCartOrder.BY_ORDER_GUID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository.FindCartOrder.BY_CART_GUID;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TotalsCalculator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;

/**
 * <p>
 * The TotalsCalculator is responsible calculating ShoppingCart related totals.
 * </p>
 * <p>
 * Since totals include discounts, please use the enriched ShoppingCart for all
 * totals calculations.  See {@link CartOrderRepository#getEnrichedShoppingCart(String, String,
 *                                    com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository.FindCartOrder)}.
 * NOTE: Please do not call ShoppingCartImpl.fireRules() directly.  Enriched cart will handle this for you.
 * </p>
 * <p>
 * Also see {@link TaxesCalculatorImpl} which performs a similar role for tax calculations.
 * </p>
 */
@Singleton
@Named("totalsCalculator")
public class TotalsCalculatorImpl implements TotalsCalculator {

	private final CartOrderRepository cartOrderRepository;

	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Constructor.
	 *  @param cartOrderRepository the CartOrderRepository
	 * @param pricingSnapshotRepository the pricing snapshot repository
	 */
	@Inject
	TotalsCalculatorImpl(
		@Named("cartOrderRepository")
		final CartOrderRepository cartOrderRepository,
		@Named("pricingSnapshotRepository")
		final PricingSnapshotRepository pricingSnapshotRepository) {

		this.cartOrderRepository = cartOrderRepository;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Override
	public ExecutionResult<Money> calculateTotalForShoppingCart(final String storeCode, final String shoppingCartGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ShoppingCart shoppingCart = Assign.ifSuccessful(
					cartOrderRepository.getEnrichedShoppingCart(storeCode, shoppingCartGuid, BY_CART_GUID));
				ShoppingCartPricingSnapshot pricingSnapshot = Assign.ifSuccessful(
					pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart));

				return ExecutionResultFactory.createReadOK(pricingSnapshot.getSubtotalMoney());
			}
		}.execute();
	}

	@Override
	public ExecutionResult<Money> calculateTotalForCartOrder(final String storeCode, final String cartOrderGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				final ShoppingCart shoppingCart = Assign.ifSuccessful(
							cartOrderRepository.getEnrichedShoppingCart(storeCode, cartOrderGuid, BY_ORDER_GUID));
				final ShoppingCartTaxSnapshot taxSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart));

				return ExecutionResultFactory.createReadOK(taxSnapshot.getTotalMoney());
			}
		}.execute();
	}

	@Override
	public ExecutionResult<Money> calculateSubTotalForCartOrder(final String storeCode, final String cartOrderGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				final ShoppingCart shoppingCart = Assign.ifSuccessful(
							cartOrderRepository.getEnrichedShoppingCart(storeCode, cartOrderGuid, BY_ORDER_GUID));
				final ShoppingCartPricingSnapshot pricingSnapshot = Assign.ifSuccessful(
							pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart));

				return ExecutionResultFactory.createReadOK(pricingSnapshot.getSubtotalMoney());
			}
		}.execute();
	}

	@Override
	public ExecutionResult<Money> calculateTotalForLineItem(final String storeCode, final String shoppingCartGuid, final String cartItemGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {

				final ShoppingCart shoppingCart = Assign.ifSuccessful(
							cartOrderRepository.getEnrichedShoppingCart(storeCode, shoppingCartGuid, BY_CART_GUID));
				final ShoppingCartPricingSnapshot cartPricingSnapshot = Assign.ifSuccessful(
							pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart));

				ShoppingItem shoppingItem = Assign.ifNotNull(shoppingCart.getCartItemByGuid(cartItemGuid),
						OnFailure.returnNotFound("Cannot find line item."));

				final ShoppingItemPricingSnapshot pricingSnapshot = cartPricingSnapshot.getShoppingItemPricingSnapshot(shoppingItem);
				Money shoppingItemTotal = pricingSnapshot.getPriceCalc().withCartDiscounts().getMoney();

				return ExecutionResultFactory.createReadOK(shoppingItemTotal);
			}
		}.execute();
	}
}