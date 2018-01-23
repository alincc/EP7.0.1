/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.prices.integration.epcommerce.domain.wrapper.MoneyWrapper;
import com.elasticpath.rest.resource.prices.integration.epcommerce.transformer.MoneyWrapperTransformer;

/**
 * Unit test for ${@link CartLineItemPriceLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartLineItemPriceLookupStrategyImplTest {

	private static final String STORE_CODE = "store code";
	private static final String CART_GUID = "cartGuid";
	private static final String LINE_ITEM_GUID = "lineItemGuid";
	public static final Currency CAD = Currency.getInstance("CAD");
	private static final String CART_ORDER_GUID = "cartOrderGuid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private CartOrderRepository mockCartOrderRepository;
	@Mock
	private MoneyWrapperTransformer mockMoneyWrapperTransformer;
	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@InjectMocks
	private CartLineItemPriceLookupStrategyImpl itemPriceLookupStrategy;

	private final CartLineItemPriceEntity expectedPriceEntity
			= CartLineItemPriceEntity.builder().build();

	@Mock
	private CartOrder mockCartOrder;

	private PriceCalculator stubbedPriceCalculator;

	@Before
	public void setUp() {
		stubbedPriceCalculator = mock(PriceCalculator.class, Mockito.RETURNS_DEEP_STUBS);
	}

	/**
	 * Test getLineItemPrice(Subject, String, String) success with different list and purchase prices.
	 */
	@Test
	public void testGetLineItemPriceSuccessWithDifferentListAndPurchasePrices() {
		ShoppingCart mockShoppingCart = mock(ShoppingCart.class);
		final ShoppingCartPricingSnapshot cartPricingSnapshot = mock(ShoppingCartPricingSnapshot.class);

		when(mockCartOrder.getGuid())
				.thenReturn(CART_ORDER_GUID);
		when(mockCartOrderRepository.getEnrichedShoppingCart(STORE_CODE, CART_GUID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockShoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(mockShoppingCart))
				.thenReturn(ExecutionResultFactory.createReadOK(cartPricingSnapshot));

		ShoppingItem mockShoppingItem = mockGetShoppingItem(mockShoppingCart);
		Money mockPurchasePrice = Money.valueOf(BigDecimal.ONE, CAD);

		mockShoppingItemExpectations(mockShoppingItem, mockPurchasePrice, cartPricingSnapshot);
		Money mockListPrice = Money.valueOf(BigDecimal.TEN, CAD);
		mockListPriceExpectations(mockListPrice);
		mockTransformer(mockListPrice, mockPurchasePrice, expectedPriceEntity);

		ExecutionResult<CartLineItemPriceEntity> result = itemPriceLookupStrategy.getLineItemPrice(STORE_CODE, CART_GUID, LINE_ITEM_GUID);

		assertTrue(result.isSuccessful());
		assertEquals(expectedPriceEntity, result.getData());
	}

	/**
	 * Test getLineItemPrice(Subject, String, String) success with the same list and purchase prices.
	 */
	@Test
	public void testGetLineItemPriceSuccessWithSameListAndPurchasePrices() {
		ShoppingCart mockShoppingCart = mock(ShoppingCart.class);
		final ShoppingCartPricingSnapshot cartPricingSnapshot = mock(ShoppingCartPricingSnapshot.class);

		when(mockCartOrder.getGuid())
				.thenReturn(CART_ORDER_GUID);
		when(mockCartOrderRepository.getEnrichedShoppingCart(STORE_CODE, CART_GUID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockShoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(mockShoppingCart))
				.thenReturn(ExecutionResultFactory.createReadOK(cartPricingSnapshot));

		ShoppingItem mockShoppingItem = mockGetShoppingItem(mockShoppingCart);
		Money mockPurchasePrice = Money.valueOf(BigDecimal.TEN, CAD);
		mockShoppingItemExpectations(mockShoppingItem, mockPurchasePrice, cartPricingSnapshot);
		Money mockListPrice = Money.valueOf(BigDecimal.ONE, CAD);
		mockListPriceExpectations(mockListPrice);
		mockTransformer(mockListPrice, mockPurchasePrice, expectedPriceEntity);

		ExecutionResult<CartLineItemPriceEntity> result = itemPriceLookupStrategy.getLineItemPrice(STORE_CODE, CART_GUID, LINE_ITEM_GUID);

		assertTrue(result.isSuccessful());
		assertEquals(expectedPriceEntity, result.getData());
	}

	/**
	 * Test getLineItemPrice(Subject, String, String) with failure from the repository layer.
	 */
	@Test
	public void testGetLineItemPriceWithShoppingCartRepositoryFailure() {
		when(mockCartOrderRepository.getEnrichedShoppingCart(STORE_CODE, CART_GUID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(ExecutionResultFactory.createNotFound("Test induced failure"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));
		itemPriceLookupStrategy.getLineItemPrice(STORE_CODE, CART_GUID, LINE_ITEM_GUID);
	}

	private ShoppingItem mockGetShoppingItem(final ShoppingCart mockShoppingCart) {
		ShoppingItem mockShoppingItem = mock(ShoppingItem.class);
		when(mockShoppingCart.getShoppingItemByGuid(LINE_ITEM_GUID)).thenReturn(mockShoppingItem);
		return mockShoppingItem;
	}

	private void mockShoppingItemExpectations(final ShoppingItem mockShoppingItem, final Money mockPurchasePrice,
												final ShoppingCartPricingSnapshot cartPricingSnapshot) {
		Price mockLineItemPrice = mock(Price.class);
		final ShoppingItemPricingSnapshot pricingSnapshot = mock(ShoppingItemPricingSnapshot.class);
		when(cartPricingSnapshot.getShoppingItemPricingSnapshot(mockShoppingItem)).thenReturn(pricingSnapshot);
		when(pricingSnapshot.getPrice()).thenReturn(mockLineItemPrice);
		when(pricingSnapshot.getPriceCalc()).thenReturn(stubbedPriceCalculator);
		when(stubbedPriceCalculator.forUnitPrice()).thenReturn(stubbedPriceCalculator);
		when(stubbedPriceCalculator.withCartDiscounts().getMoney()).thenReturn(mockPurchasePrice);
	}

	private void mockListPriceExpectations(final Money mockListPriceAmount) {
		when(stubbedPriceCalculator.getMoney()).thenReturn(mockListPriceAmount);
	}

	private void mockTransformer(final Money mockListPrice, final Money mockPurchasePrice, final CartLineItemPriceEntity expectedPriceEntity) {
		MoneyWrapper expectedMoneyWrapper = new MoneyWrapper();
		expectedMoneyWrapper.setListPrice(mockListPrice);
		expectedMoneyWrapper.setPurchasePrice(mockPurchasePrice);
		when(mockMoneyWrapperTransformer.transformToEntity(expectedMoneyWrapper)).thenReturn(expectedPriceEntity);
	}
}