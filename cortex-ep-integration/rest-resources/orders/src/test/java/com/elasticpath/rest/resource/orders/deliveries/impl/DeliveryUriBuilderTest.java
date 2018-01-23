/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.orders.deliveries.Deliveries;
import com.elasticpath.rest.schema.uri.DeliveryUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for delivery URI builder.
 */
public final class DeliveryUriBuilderTest {

	private static final String SCOPE = "SCOPE";

	private static final String DELIVERY_ID = "DELIVERY_ID";

	private static final String ORDER_ID = "ORDER_ID";

	private static final String ORDERS_RESOURCE = "orders";

	/**
	 * Test delivery uri builder.
	 */
	@Test
	public void testDeliveryUriBuilder() {
		DeliveryUriBuilder builder = new DeliveryUriBuilderImpl(ORDERS_RESOURCE);
		String deliveryUri = builder.setOrderId(ORDER_ID)
				.setDeliveryId(DELIVERY_ID)
				.setScope(SCOPE)
				.build();

		String expectedDeliveryUri = URIUtil.format(ORDERS_RESOURCE, SCOPE, ORDER_ID, Deliveries.URI_PART, DELIVERY_ID);
		assertEquals(expectedDeliveryUri, deliveryUri);
	}
}
