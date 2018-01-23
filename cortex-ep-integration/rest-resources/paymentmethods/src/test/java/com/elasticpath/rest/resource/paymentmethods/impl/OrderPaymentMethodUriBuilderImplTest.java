/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.OrdersUriBuilder;
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests the {@link OrderPaymentMethodUriBuilderImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderPaymentMethodUriBuilderImplTest {
	public static final String RESOURCE_SERVER_NAME = "paymentmethods";
	private static final String TEST_ORDER_ID = "testOrderId";
	private static final String TEST_SCOPE = "testScope";
	private static final String TEST_ORDERS_URI = "/testOrdersUri";

	@Mock
	private OrdersUriBuilderFactory ordersUriBuilderFactory;
	@Mock
	private OrdersUriBuilder ordersUriBuilder;

	private OrderPaymentMethodUriBuilder orderPaymentMethodUriBuilder;

	@Before
	public void setupTestComponentsAndHappyCollaborators() {
		shouldBuildOrderUri();
		orderPaymentMethodUriBuilder = new OrderPaymentMethodUriBuilderImpl(ordersUriBuilderFactory,
				RESOURCE_SERVER_NAME);
	}

	@Test
	public void ensureOrderUriIsBuilt() {
		buildOrderPaymentMethodUri();

		verify(ordersUriBuilder, times(1)).build();
	}

	@Test
	public void ensureScopeIsSet() {
		buildOrderPaymentMethodUri();

		verify(ordersUriBuilder, times(1)).setScope(TEST_SCOPE);
	}

	@Test
	public void ensureOrderIdIsSet() {
		buildOrderPaymentMethodUri();

		verify(ordersUriBuilder, times(1)).setOrderId(TEST_ORDER_ID);
	}

	@Test
	public void ensureOrderPaymentMethodUriIsBuiltCorrectly() {
		assertEquals("The order payment method uri should be the same as expected",
				URIUtil.format(RESOURCE_SERVER_NAME, TEST_ORDERS_URI), buildOrderPaymentMethodUri());
	}

	private void shouldBuildOrderUri() {
		when(ordersUriBuilderFactory.get()).thenReturn(ordersUriBuilder);
		when(ordersUriBuilder.setOrderId(TEST_ORDER_ID)).thenReturn(ordersUriBuilder);
		when(ordersUriBuilder.setScope(TEST_SCOPE)).thenReturn(ordersUriBuilder);
		when(ordersUriBuilder.build()).thenReturn(TEST_ORDERS_URI);
	}

	private String buildOrderPaymentMethodUri() {
		return orderPaymentMethodUriBuilder
				.setOrderId(TEST_ORDER_ID)
				.setScope(TEST_SCOPE)
				.build();
	}
}
