/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test class for {@link com.elasticpath.rest.integration.epcommerce.promotion.impl.AppliedCartPromotionsLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppliedCartPromotionsLookupStrategyImplTest {

	private static final String SCOPE = "testScope";
	private static final String CART_ID = "testCartId";
	private static final String CART_ORDER_ID = "testOrderCartId";
	private static final String LINEITEM_ID = "testLineItemId";
	private static final int ITEM_QUANTITY = 2;
	private static final String PROMOTION_ID = "testPromotionId";

	@Mock
	private PromotionRepository promotionRepository;
	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;
	@InjectMocks
	private AppliedCartPromotionsLookupStrategyImpl appliedCartPromotionsLookupStrategy;
	@Mock
	private ShoppingCart shoppingCart;
	@Mock
	private CartOrder cartOrder;
	@Mock
	private ShoppingCartPricingSnapshot pricingSnapshot;

	@Before
	public void mockShoppingCart() {
		when(cartOrder.getGuid()).thenReturn(CART_ORDER_ID);
		when(cartOrderRepository.getEnrichedShoppingCart(SCOPE, CART_ID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart))
				.thenReturn(ExecutionResultFactory.createReadOK(pricingSnapshot));
	}

	@Test
	public void testGetAppliedPromotionsForItemInCartOneItemSuccessful() {
		Collection<String> expectedResult = Arrays.asList(PROMOTION_ID);
		mockCartLineitemPromotions(expectedResult);

		ExecutionResult<Collection<String>> result = appliedCartPromotionsLookupStrategy
				.getAppliedPromotionsForItemInCart(SCOPE, CART_ID, LINEITEM_ID, ITEM_QUANTITY);

		assertExecutionResult(result)
				.isSuccessful()
				.data(expectedResult);
	}

	@Test
	public void testGetAppliedPromotionsForItemInCartZeroItemsSuccessful() {
		Collection<String> expectedResult = Collections.emptyList();
		mockCartLineitemPromotions(expectedResult);

		ExecutionResult<Collection<String>> result = appliedCartPromotionsLookupStrategy
				.getAppliedPromotionsForItemInCart(SCOPE, CART_ID, LINEITEM_ID, ITEM_QUANTITY);

		assertExecutionResult(result)
				.isSuccessful()
				.data(expectedResult);
	}

	@Test
	public void testGetAppliedPromotionsForCartOneItemSuccessful() {
		Collection<String> expectedResult = Arrays.asList(PROMOTION_ID);
		mockCartPromotions(expectedResult);

		ExecutionResult<Collection<String>> result = appliedCartPromotionsLookupStrategy.getAppliedPromotionsForCart(SCOPE, CART_ID);

		assertExecutionResult(result)
				.isSuccessful()
				.data(expectedResult);
	}

	@Test
	public void testGetAppliedPromotionsForCartZeroItemsSuccessful() {
		Collection<String> expectedResult = Collections.emptyList();
		mockCartPromotions(expectedResult);

		ExecutionResult<Collection<String>> result = appliedCartPromotionsLookupStrategy.getAppliedPromotionsForCart(SCOPE, CART_ID);

		assertExecutionResult(result)
				.isSuccessful()
				.data(expectedResult);
	}

	private void mockCartLineitemPromotions(final Collection<String> promotionIds) {
		when(promotionRepository.getAppliedCartLineitemPromotions(shoppingCart, pricingSnapshot, LINEITEM_ID)).thenReturn(promotionIds);
	}

	private void mockCartPromotions(final Collection<String> promotionIds) {
		when(promotionRepository.getAppliedCartPromotions(pricingSnapshot)).thenReturn(promotionIds);
	}
}
