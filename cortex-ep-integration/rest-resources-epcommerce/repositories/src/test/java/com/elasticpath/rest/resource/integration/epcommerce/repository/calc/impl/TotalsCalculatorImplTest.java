/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;


import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;

@RunWith(MockitoJUnitRunner.class)
public class TotalsCalculatorImplTest {

	private static final String STORE_CODE = "TEST_STORE";
	private static final String EXISTS_GUID = "exists guid";
	private static final String NOT_EXISTS_GUID = "not exists guid";
	private static final String LINE_ITEM_GUID = "line item guid";
	private static final Money ZERO_CAD = Money.valueOf(BigDecimal.ZERO, Currency.getInstance("CAD"));
	private static final Money TEN_CAD = Money.valueOf(BigDecimal.TEN, Currency.getInstance("CAD"));
	public static final String OPERATION_SUCCESS = "Operation should have been successful.";
	public static final String OPERATION_FAILED = "Operation should have failed.";
	public static final String EXPECTED_MONEY = "Expected money value does not match.";

	@Mock
	private ShoppingCart shoppingCart;
	@Mock
	private ShoppingCartPricingSnapshot cartPricingSnapshot;
	@Mock
	private ShoppingCartTaxSnapshot cartTaxSnapshot;
	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@InjectMocks
	private TotalsCalculatorImpl calculator;

	@Test
	public void ensureTotalIsCalculatedBeforeTotalIsReadForShoppingCartWithNoSubtotalDiscount() {
		CartOrder cartOrder = mock(CartOrder.class);
		when(cartOrder.getGuid()).thenReturn(EXISTS_GUID);
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, EXISTS_GUID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart))
				.thenReturn(ExecutionResultFactory.createReadOK(cartPricingSnapshot));

		when(cartPricingSnapshot.getSubtotalMoney()).thenReturn(TEN_CAD);
		when(cartPricingSnapshot.getSubtotalDiscountMoney()).thenReturn(ZERO_CAD);

		ExecutionResult<Money> result = calculator.calculateTotalForShoppingCart(STORE_CODE, EXISTS_GUID);

		assertEquals(OPERATION_SUCCESS, ResourceStatus.READ_OK, result.getResourceStatus());
		assertEquals(EXPECTED_MONEY, TEN_CAD, result.getData());
	}
	
	@Test
	public void ensureErrorPropagationOfFailedGetShoppingCartWhenCalculatingShoppingCartTotal() {
		CartOrder cartOrder = mock(CartOrder.class);
		when(cartOrder.getGuid()).thenReturn(NOT_EXISTS_GUID);
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, NOT_EXISTS_GUID,  CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(ExecutionResultFactory.<ShoppingCart>createNotFound());
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart))
			.thenReturn(ExecutionResultFactory.createReadOK(cartPricingSnapshot));

		ExecutionResult<Money> result = calculator.calculateTotalForShoppingCart(STORE_CODE, NOT_EXISTS_GUID);

		assertTrue(OPERATION_FAILED, result.getResourceStatus().isFailure());
	}

	@Test
	public void ensureTotalIsCalculatedBeforeTotalIsReadForCartOrder() {
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, EXISTS_GUID,  CartOrderRepository.FindCartOrder.BY_ORDER_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)).thenReturn(ExecutionResultFactory.createReadOK(cartTaxSnapshot));
		when(cartTaxSnapshot.getTotalMoney()).thenReturn(ZERO_CAD);

		ExecutionResult<Money> result = calculator.calculateTotalForCartOrder(STORE_CODE, EXISTS_GUID);

		assertEquals(OPERATION_SUCCESS, ResourceStatus.READ_OK, result.getResourceStatus());
		assertEquals(EXPECTED_MONEY, ZERO_CAD, result.getData());
	}

	@Test
	public void ensureErrorPropagationOfFailedGetCartWhenCalculatingCartOrderTotal() {
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, NOT_EXISTS_GUID,  CartOrderRepository.FindCartOrder.BY_ORDER_GUID))
				.thenReturn(ExecutionResultFactory.<ShoppingCart>createNotFound());
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart))
			.thenReturn(ExecutionResultFactory.createReadOK(cartPricingSnapshot));

		ExecutionResult<Money> result = calculator.calculateTotalForCartOrder(STORE_CODE, NOT_EXISTS_GUID);

		assertTrue(OPERATION_FAILED, result.getResourceStatus().isFailure());
	}

	@Test
	public void testCalculateTotalForShoppingCart() {
		CartOrder cartOrder = mock(CartOrder.class);
		when(cartOrder.getGuid()).thenReturn(EXISTS_GUID);
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, EXISTS_GUID,  CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart))
			.thenReturn(ExecutionResultFactory.createReadOK(cartPricingSnapshot));

		when(cartPricingSnapshot.getSubtotalMoney()).thenReturn(TEN_CAD);

		ExecutionResult<Money> result = calculator.calculateTotalForShoppingCart(STORE_CODE, EXISTS_GUID);

		assertEquals(OPERATION_SUCCESS, ResourceStatus.READ_OK, result.getResourceStatus());
		assertEquals(EXPECTED_MONEY, TEN_CAD, result.getData());
	}

	@Test
	public void testCalculateSubTotalWithoutTaxForCartOrder() {
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, EXISTS_GUID,  CartOrderRepository.FindCartOrder.BY_ORDER_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart))
				.thenReturn(ExecutionResultFactory.createReadOK(cartPricingSnapshot));
		when(cartPricingSnapshot.getSubtotalMoney()).thenReturn(TEN_CAD);

		ExecutionResult<Money> result = calculator.calculateSubTotalForCartOrder(STORE_CODE, EXISTS_GUID);

		assertEquals(OPERATION_SUCCESS, ResourceStatus.READ_OK, result.getResourceStatus());
		assertEquals(EXPECTED_MONEY, TEN_CAD, result.getData());
	}

	@Test
	public void testCalculateTotalForLineItem() {
		CartOrder cartOrder = mock(CartOrder.class);
		when(cartOrder.getGuid()).thenReturn(EXISTS_GUID);
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, EXISTS_GUID,  CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart))
			.thenReturn(ExecutionResultFactory.createReadOK(cartPricingSnapshot));

		when(cartPricingSnapshot.getSubtotalMoney()).thenReturn(ZERO_CAD);

		ShoppingItem mockShoppingItem = mockGetShoppingItem(shoppingCart);
		mockShoppingItemExpectations(mockShoppingItem, ZERO_CAD);

		ExecutionResult<Money> result = calculator.calculateTotalForLineItem(STORE_CODE, EXISTS_GUID, LINE_ITEM_GUID);

		assertEquals(OPERATION_SUCCESS, ResourceStatus.READ_OK, result.getResourceStatus());
		assertEquals(EXPECTED_MONEY, ZERO_CAD, result.getData());
	}

	private ShoppingItem mockGetShoppingItem(final ShoppingCart mockShoppingCart) {
		ShoppingItem mockShoppingItem = mock(ShoppingItem.class);
		when(mockShoppingCart.getCartItemByGuid(LINE_ITEM_GUID)).thenReturn(mockShoppingItem);
		return mockShoppingItem;
	}

	private Price mockShoppingItemExpectations(final ShoppingItem mockShoppingItem, final Money mockPurchasePrice) {
		Price mockLineItemPrice = mock(Price.class);
		final PriceCalculator stubbedPriceCalculator = mock(PriceCalculator.class, Mockito.RETURNS_DEEP_STUBS);
		final ShoppingItemPricingSnapshot shoppingItemPricingSnapshot = mock(ShoppingItemPricingSnapshot.class);
		when(cartPricingSnapshot.getShoppingItemPricingSnapshot(mockShoppingItem)).thenReturn(shoppingItemPricingSnapshot);
		when(shoppingItemPricingSnapshot.getPriceCalc()).thenReturn(stubbedPriceCalculator);
		when(stubbedPriceCalculator.withCartDiscounts().getMoney()).thenReturn(mockPurchasePrice);
		return mockLineItemPrice;
	}

}