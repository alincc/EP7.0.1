/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.transform;

import static com.elasticpath.rest.schema.ResourceState.Builder.create;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

@RunWith(MockitoJUnitRunner.class)
public final class OrderTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "RESOURCE_SERVER_NAME";
	private static final String SCOPE = "SCOPE";
	private static final String DECODED_CART_ID = "DECODED_CART_ID";
	private static final String CART_ID = Base32Util.encode(DECODED_CART_ID);
	private static final String DECODED_ORDER_ID = "DECODED_ORDER_ID";
	private static final String ORDER_ID = Base32Util.encode(DECODED_ORDER_ID);
	private static final String REPRESENTATION_ASSERT_MESSAGE = "returned order representation should be equal to expected representation";

	private final OrderTransformer orderTransformer = new OrderTransformer(RESOURCE_SERVER_NAME);

	@Test
	public void testTransformToRepresentation() {

		OrderEntity decodedOrderEntity = OrderEntity.builder()
				.withCartId(DECODED_CART_ID)
				.withOrderId(DECODED_ORDER_ID)
				.build();
		OrderEntity encodedOrderEntity = OrderEntity.builder()
				.withCartId(CART_ID)
				.withOrderId(ORDER_ID)
				.build();

		ResourceState<OrderEntity> orderRepresentation = orderTransformer.transformToRepresentation(SCOPE, decodedOrderEntity);

		String expectedOrderUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, ORDER_ID);
		Self expectedSelf = SelfFactory.createSelf(expectedOrderUri);
		ResourceState<OrderEntity> expectedOrderRepresentation = create(encodedOrderEntity)
				.withSelf(expectedSelf)
				.withScope(SCOPE)
				.build();

		assertEquals(REPRESENTATION_ASSERT_MESSAGE, expectedOrderRepresentation, orderRepresentation);
	}

}
