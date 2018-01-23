/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.schema.uri.OrdersUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Basic smoke test of {@link OrdersUriBuilderImpl}.
 */
public final class OrdersUriBuilderImplTest {

	private static final String ROOT_NAME = "orderResource";

	private static final String SCOPE = "SCOPE";

	private static final String ORDER_ID = "ORDER_ID";

	/**
	 * Tests building a URI.
	 */
	@Test
	public void testCreateUri() {
		OrdersUriBuilder ordersUriBuilder = new OrdersUriBuilderImpl(ROOT_NAME);
		String uri = ordersUriBuilder.setOrderId(ORDER_ID)
				.setScope(SCOPE)
				.build();
		assertEquals("Incorrect URI generated: ", URIUtil.format(ROOT_NAME, SCOPE, ORDER_ID), uri);
	}
}
