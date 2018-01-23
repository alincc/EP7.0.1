/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.command.impl;

import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;
import static com.elasticpath.rest.resource.orders.deliveries.DeliveryConstants.DELIVERY_LIST_NAME;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.orders.deliveries.command.ReadOrderDeliveriesCommand;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link ReadOrderDeliveriesCommandImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadOrderDeliveriesCommandImplTest {

	private static final String ORDER_RESOURCE = "orders";
	private static final String SCOPE = "scope";
	private static final String ORDER_ID = "order_id";
	private static final String DELIVERIES = "deliveries";
	private static final String ORDER_URI = URIUtil.format(ORDER_RESOURCE, SCOPE, ORDER_ID);
	private static final String DELIVERIES_URI = URIUtil.format(ORDER_URI, DELIVERIES);

	@Test
	public void ensureDeliveriesCanBeReadForOrder() {
		ExecutionResult<ResourceState<LinksEntity>> result = createReadOrderDeliveriesCommand().execute();

		assertExecutionResult(result).data(createExpectedDeliveriesRepresentation());
	}

	private ResourceState<LinksEntity> createExpectedDeliveriesRepresentation() {

		LinksEntity linksEntity = LinksEntity.builder()
				.withName(DELIVERY_LIST_NAME)
				.withElementListId(ORDER_ID)
				.build();
		return ResourceState.Builder
				.create(linksEntity)
				.withScope(SCOPE)
				.withSelf(createSelf(DELIVERIES_URI))
				.build();
	}

	private ReadOrderDeliveriesCommand createReadOrderDeliveriesCommand() {
		ResourceState<OrderEntity> orderState = ResourceState.Builder
				.create(OrderEntity.builder()
						.withOrderId(ORDER_ID)
						.build())
				.withScope(SCOPE)
				.withSelf(createSelf(ORDER_URI, ORDER.id()))
				.build();

		return new ReadOrderDeliveriesCommandImpl.BuilderImpl(new ReadOrderDeliveriesCommandImpl())
				.setOrder(orderState)
				.build();
	}
}
