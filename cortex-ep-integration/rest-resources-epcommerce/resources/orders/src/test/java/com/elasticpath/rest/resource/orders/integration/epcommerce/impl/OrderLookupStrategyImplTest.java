/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.orders.integration.epcommerce.transform.CartOrderTransformer;

/**
 * Test that {@link OrderLookupStrategyImpl} behaves as expected.
 */
@SuppressWarnings({"PMD.TooManyStaticImports"})
@RunWith(MockitoJUnitRunner.class)
public class OrderLookupStrategyImplTest {

	private static final String CART_GUID = "CART_GUID";
	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";
	private static final String STORE_CODE = "STORE_CODE";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private CartOrderTransformer cartOrderTransformer;
	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@InjectMocks
	private OrderLookupStrategyImpl orderLookupStrategy;


	/**
	 * Test the behaviour of get cart order.
	 */
	@Test
	public void testGetOrder() {
		CartOrder expectedCartOrder = mock(CartOrder.class);
		OrderEntity orderEntity = OrderEntity.builder().build();

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(expectedCartOrder));
		shouldTransformToEntity(expectedCartOrder, orderEntity);

		ExecutionResult<OrderEntity> result = orderLookupStrategy.getOrder(STORE_CODE, CART_ORDER_GUID);

		assertNull("There should not be an error message.", result.getErrorMessage());
		assertEquals("Result entity does not match expected entity.", orderEntity, result.getData());
	}

	/**
	 * Test the behaviour of unsuccessful get cart order.
	 */
	@Test
	public void testUnsuccessfulGetCartOrder() {
		shouldFindByGuidWithResult(ExecutionResultFactory.<CartOrder>createNotFound("Not found."));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		orderLookupStrategy.getOrder(STORE_CODE, CART_ORDER_GUID);
	}

	/**
	 * Test the behaviour of get cart order for cart.
	 */
	@Test
	public void testGetOrderForCart() {
		CartOrder expectedCartOrder = mock(CartOrder.class);
		OrderEntity orderEntity = OrderEntity.builder().build();

		shouldShoppingCartExistsForStore(true);
		shouldFindByCartGuidWithResult(ExecutionResultFactory.createReadOK(expectedCartOrder));
		shouldTransformToEntity(expectedCartOrder, orderEntity);

		ExecutionResult<OrderEntity> result = orderLookupStrategy.getOrderForCart(STORE_CODE, CART_GUID);

		assertNull("There should be no error message", result.getErrorMessage());
		assertEquals("Result entity does not match expected entity.", orderEntity, result.getData());
	}

	/**
	 * Test the behaviour of unsuccessful get cart order for cart.
	 */
	@Test
	public void testUnsuccessfulGetCartOrderForCart() {
		shouldShoppingCartExistsForStore(false);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		orderLookupStrategy.getOrderForCart(STORE_CODE, CART_GUID);
	}

	/**
	 * Test the behaviour of get cart order for cart when cart order not found.
	 */
	@Test
	public void testGetCartOrderForCartWhenCartOrderNotFound() {
		shouldShoppingCartExistsForStore(true);
		shouldFindByCartGuidWithResult(ExecutionResultFactory.<CartOrder>createNotFound("No cart order was found"));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		orderLookupStrategy.getOrderForCart(STORE_CODE, CART_GUID);
	}

	private void shouldTransformToEntity(final CartOrder expectedCartOrder, final OrderEntity result) {
		when(cartOrderTransformer.transformToEntity(expectedCartOrder)).thenReturn(result);
	}

	private void shouldFindByGuidWithResult(final ExecutionResult<CartOrder> result) {
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(result);
	}

	private void shouldFindByCartGuidWithResult(final ExecutionResult<CartOrder> result) {
		when(cartOrderRepository.findByCartGuid(CART_GUID)).thenReturn(result);
	}

	private void shouldShoppingCartExistsForStore(final boolean cartExistsForStore) {
		when(shoppingCartRepository.verifyShoppingCartExistsForStore(CART_GUID, STORE_CODE)).thenReturn(cartExistsForStore);
	}
}
