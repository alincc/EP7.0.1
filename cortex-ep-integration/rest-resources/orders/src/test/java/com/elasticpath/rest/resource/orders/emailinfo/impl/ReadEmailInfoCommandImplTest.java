/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.impl;

import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;
import static com.elasticpath.rest.resource.orders.emailinfo.EmailInfoConstants.EMAIL_INFO_NAME;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.orders.emailinfo.ReadEmailInfoCommand;
import com.elasticpath.rest.resource.orders.emailinfo.impl.ReadEmailInfoCommandImpl.BuilderImpl;
import com.elasticpath.rest.schema.ResourceState;

public class ReadEmailInfoCommandImplTest {

	private static final String SCOPE = "testScope";
	private static final String ORDER_ID = "orderId";
	private static final String ORDER_URI = "/orders/orderId";

	@Test
	public void testGetEmailInfo() {

		ExecutionResult<ResourceState<InfoEntity>> result = createCommand().execute();

		assertTrue(result.isSuccessful());
		assertEquals(EMAIL_INFO_NAME, result.getData()
				.getEntity()
				.getName());
	}

	private ReadEmailInfoCommand createCommand() {

		ReadEmailInfoCommandImpl command = new ReadEmailInfoCommandImpl();

		OrderEntity orderEntity = OrderEntity.builder()
				.withOrderId(ORDER_ID)
				.build();
		ResourceState<OrderEntity> orderResourceState = ResourceState.Builder
				.create(orderEntity)
				.withSelf(createSelf(ORDER_URI, ORDER.id()))
				.withScope(SCOPE)
				.build();

		return new BuilderImpl(command)
				.setOrderResourceState(orderResourceState)
				.build();
	}
}
