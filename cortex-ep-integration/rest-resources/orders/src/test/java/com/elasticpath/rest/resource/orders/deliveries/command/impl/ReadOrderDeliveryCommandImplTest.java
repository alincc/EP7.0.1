/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.command.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.orders.deliveries.DeliveryLookup;
import com.elasticpath.rest.resource.orders.deliveries.command.ReadOrderDeliveryCommand;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Tests for {@link ReadOrderDeliveryCommandImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadOrderDeliveryCommandImplTest {

	private static final String DECODED_DELIVERY_ID = "DELIVERY_ID";
	private static final String DELIVERY_ID = Base32Util.encode(DECODED_DELIVERY_ID);
	private static final String DECODED_ORDER_ID = "ORDER_ID";
	private static final String ORDER_ID = Base32Util.encode(DECODED_ORDER_ID);
	private static final String SCOPE = "SCOPE";
	private static final String PARENT_URI = "/parentUri";

	@Mock
	private DeliveryLookup deliveryLookup;

	/**
	 * Test execute with successful return from find order by order ID.
	 */
	@Test
	public void testExecuteWithSuccessFromFindOrderByOrderId() {

		DeliveryEntity deliveryEntity = DeliveryEntity.builder().build();
		ResourceState<DeliveryEntity> deliveryRepresentation = ResourceState.Builder.create(deliveryEntity).build();

		shouldFindByIdAndOrderIdWithResult(ORDER_ID, DELIVERY_ID, ExecutionResultFactory.createCreateOKWithData(deliveryRepresentation, false));

		ReadOrderDeliveryCommand readOrderDeliveryCommand = createReadOrderDeliveryCommand(PARENT_URI);
		ExecutionResult<ResourceState<DeliveryEntity>> result = readOrderDeliveryCommand.execute();

		assertTrue("This should be a successful operation.", result.isSuccessful());
		assertEquals("The result data should be as expected.", deliveryRepresentation, result.getData());
	}

	private void shouldFindByIdAndOrderIdWithResult(final String orderId, final String deliveryId,
			final ExecutionResult<ResourceState<DeliveryEntity>> result) {

		when(deliveryLookup.findByIdAndOrderId(SCOPE, orderId, deliveryId))
				.thenReturn(result);
	}

	private ReadOrderDeliveryCommand createReadOrderDeliveryCommand(final String parentUri) {

		ReadOrderDeliveryCommandImpl readOrderDeliveryCommand = new ReadOrderDeliveryCommandImpl(deliveryLookup);
		ReadOrderDeliveryCommand.Builder builder = new ReadOrderDeliveryCommandImpl.BuilderImpl(readOrderDeliveryCommand);
		builder.setDeliveryId(DELIVERY_ID).setOrder(createOrder(parentUri, ORDER_ID));

		return readOrderDeliveryCommand;
	}

	private ResourceState<OrderEntity> createOrder(final String parentUri, final String orderId) {
		OrderEntity orderEntity = OrderEntity.builder().withOrderId(orderId).build();
		return ResourceState.Builder.create(orderEntity).withSelf(SelfFactory.createSelf(parentUri, OrdersMediaTypes.ORDER.id())).withScope(SCOPE)
				.build();
	}
}
