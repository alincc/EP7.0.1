/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.elasticpath.rest.ResourceStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.orders.integration.OrderLookupStrategy;
import com.elasticpath.rest.resource.orders.transform.OrderTransformer;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Test class for OrderLookupCoreImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public final class OrderLookupImplTest {

	private static final String REPRESENTATION_ASSERT_MESSAGE = "returned order representation should be equal to expected representation";
	private static final String SCOPE = "SCOPE";
	private static final String DECODED_ORDER_ID = "DECODED_ORDER_ID";
	private static final String ORDER_ID = Base32Util.encode(DECODED_ORDER_ID);
	private static final String DECODED_CART_ID = "DECODED_CART_ID";
	private static final String CART_ID = Base32Util.encode(DECODED_CART_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private OrderTransformer orderTransformer;
	@Mock
	private OrderLookupStrategy orderLookupStrategy;

	@InjectMocks
	private OrderLookupImpl orderLookup;


	/**
	 * Tests findOrderByCartId.
	 */
	@Test
	public void testFindOrderByCartId() {
		OrderEntity orderEntity = createDecodedOrderEntity();
		ResourceState<OrderEntity> expectedRepresentation = createOrderRepresentation();

		shouldGetOrderForCartWithResult(ExecutionResultFactory.createReadOK(orderEntity));
		shouldTransformToRepresentation(orderEntity, expectedRepresentation);

		ExecutionResult<ResourceState<OrderEntity>> orderResult = orderLookup.findOrderByCartId(SCOPE, CART_ID);

		assertTrue("This should be a successful result.", orderResult.isSuccessful());
		assertNull("There should be no error message.", orderResult.getErrorMessage());
		assertEquals(REPRESENTATION_ASSERT_MESSAGE, expectedRepresentation, orderResult.getData());
	}

	/**
	 * Tests findOrderByCartId with strategy result failure.
	 */
	@Test
	public void testFindOrderByCartIdWithStrategyFailure() {
		shouldGetOrderForCartWithResult(ExecutionResultFactory.<OrderEntity>createNotFound("Not found."));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		orderLookup.findOrderByCartId(SCOPE, CART_ID);
	}

	/**
	 * Tests findOrderByOrderId.
	 */
	@Test
	public void testFindOrderByOrderId() {
		OrderEntity orderEntity = createDecodedOrderEntity();
		ExecutionResult<OrderEntity> mockResult = ExecutionResultFactory.createReadOK(orderEntity);
		ResourceState<OrderEntity> expectedRepresentation = createOrderRepresentation();

		shouldGetOrderWithResult(mockResult);
		shouldTransformToRepresentation(orderEntity, expectedRepresentation);

		ExecutionResult<ResourceState<OrderEntity>> orderResult = orderLookup.findOrderByOrderId(SCOPE, ORDER_ID);

		assertEquals(REPRESENTATION_ASSERT_MESSAGE, expectedRepresentation, orderResult.getData());
		assertNotNull("The OrderEntity should exist.", orderResult.getData().getEntity());
	}

	/**
	 * Tests findOrderByOrderId with strategy failure.
	 */
	@Test
	public void testFindOrderByOrderIdWithStrategyFailure() {

		shouldGetOrderWithResult(ExecutionResultFactory.<OrderEntity>createNotFound("Not found."));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		orderLookup.findOrderByOrderId(SCOPE, ORDER_ID);
	}

	private void shouldGetOrderWithResult(final ExecutionResult<OrderEntity> result) {
		when(orderLookupStrategy.getOrder(SCOPE, DECODED_ORDER_ID))
				.thenReturn(result);
	}

	private void shouldTransformToRepresentation(final OrderEntity orderEntity, final ResourceState<OrderEntity> expectedRepresentation) {
		when(orderTransformer.transformToRepresentation(SCOPE, orderEntity))
				.thenReturn(expectedRepresentation);
	}

	private void shouldGetOrderForCartWithResult(final ExecutionResult<OrderEntity> mockResult) {
		when(orderLookupStrategy.getOrderForCart(SCOPE, DECODED_CART_ID))
				.thenReturn(mockResult);
	}

	private OrderEntity createDecodedOrderEntity() {
		return OrderEntity.builder().withCartId(DECODED_CART_ID).withOrderId(DECODED_ORDER_ID).build();
	}
	private OrderEntity createEncodedOrderEntity() {
		return OrderEntity.builder().withCartId(CART_ID).withOrderId(ORDER_ID).build();
	}

	private ResourceState<OrderEntity> createOrderRepresentation() {
		return ResourceState.Builder.create(createEncodedOrderEntity()).build();
	}
}
