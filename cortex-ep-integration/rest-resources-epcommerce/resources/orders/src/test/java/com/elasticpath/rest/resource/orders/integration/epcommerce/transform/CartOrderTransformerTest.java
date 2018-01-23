/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.rest.definition.orders.OrderEntity;

/**
 * Tests {@link CartOrderTransformer}.
 */
public class CartOrderTransformerTest {

	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";

	private static final String CART_GUID = "CART_GUID";

	private final CartOrderTransformer cartOrderTransformer = new CartOrderTransformer();

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		cartOrderTransformer.transformToDomain(null);
	}

	/**
	 * Test internal transform to entity.
	 */
	@Test
	public void testInternalTransformToEntity() {
		CartOrder cartOrder = new CartOrderImpl();
		cartOrder.setGuid(CART_ORDER_GUID);
		cartOrder.setShoppingCartGuid(CART_GUID);

		OrderEntity expectedOrderEntity = OrderEntity.builder()
				.withCartId(CART_GUID)
				.withOrderId(CART_ORDER_GUID)
				.build();

		OrderEntity orderEntity = cartOrderTransformer.transformToEntity(cartOrder, Locale.ENGLISH);

		assertEquals("Order entity returned should be same as expected order entity", expectedOrderEntity, orderEntity);
	}

}
