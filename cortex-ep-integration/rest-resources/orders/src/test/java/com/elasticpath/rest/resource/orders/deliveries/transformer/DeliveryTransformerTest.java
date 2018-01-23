/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.transformer;

import static com.elasticpath.rest.schema.SelfFactory.createSelf;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.orders.deliveries.Deliveries;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * The test for {@link DeliveryTransformer}.
 */
public final class DeliveryTransformerTest {

	private static final String SCOPE = "SCOPE";
	private static final String ORDERS_RESOURCE = "orders";
	private static final String DELIVERY_TYPE = "DELIVERY_TYPE";
	private static final String ORDER_ID = "ORDER_ID";
	private static final String DECODED_DELIVERY_ID = "DECODED_DELIVERY_ID";
	private static final String DELIVERY_ID = Base32Util.encode(DECODED_DELIVERY_ID);

	private final DeliveryTransformer deliveryTransformer = new DeliveryTransformer(ORDERS_RESOURCE);

	/**
	 * Tests that {@link DeliveryTransformer#transformToRepresentation(String, DeliveryEntity, String)} transforms the DTO to a
	 * {@link ResourceState}.
	 */
	@Test
	public void testTransformToRepresentation() {
		DeliveryEntity entity = DeliveryEntity.builder()
				.withDeliveryId(DECODED_DELIVERY_ID)
				.withDeliveryType(DELIVERY_TYPE)
				.build();
		String deliveryId = Base32Util.encode(DECODED_DELIVERY_ID);
		String orderUri = URIUtil.format(ORDERS_RESOURCE, SCOPE, ORDER_ID);
		String deliveriesUri = URIUtil.format(orderUri, Deliveries.URI_PART);
		String selfUri = URIUtil.format(deliveriesUri, deliveryId);

		ResourceState<DeliveryEntity> representation = deliveryTransformer.transformToRepresentation(SCOPE, entity, ORDER_ID);

		DeliveryEntity deliveryEntity = DeliveryEntity.builder()
				.withDeliveryId(DELIVERY_ID)
				.withDeliveryType(DELIVERY_TYPE)
				.withOrderId(ORDER_ID)
				.build();
		ResourceState<DeliveryEntity> expectedRepresentation = ResourceState.Builder
				.create(deliveryEntity)
				.withSelf(createSelf(selfUri))
				.withScope(SCOPE)
				.build();
		assertEquals(expectedRepresentation, representation);
	}
}
