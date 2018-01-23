/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.resource.orders.deliveries.Deliveries;
import com.elasticpath.rest.resource.orders.deliveries.impl.DeliveriesResourceOperatorImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DeliveriesResourceOperatorImpl.class})
public final class DeliveriesResourceOperatorImplUriTest extends AbstractUriTest {

	private static final String DELIVERY_ID = "mrswy2lwmvzhssle=";
	private static final String ORDER_ID = "4ndg5pjosxx6x4ria6xfclmq3u=";
	private static final String SCOPE = "scope";
	private static final String RESOURCE_NAME = "orders";
	private static final String ORDER_URI = URIUtil.format(RESOURCE_NAME, SCOPE, ORDER_ID);
	private final String deliveriesUri = URIUtil.format(ORDER_URI, Deliveries.URI_PART, DELIVERY_ID);

	@Mock
	private DeliveriesResourceOperatorImpl deliveriesResourceOperator;

	@Test
	public void testUriDispatchToDeliveryProcessReadDeliveryMethod() {

		when(deliveriesResourceOperator.processReadDelivery(anyOrderEntity(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);
		ResourceOperation readDeliveryOperation = createRead(ORDER_URI);
		mediaType(OrdersMediaTypes.ORDER);
		readOther(readDeliveryOperation);

		dispatchMethod(createRead(deliveriesUri), deliveriesResourceOperator);

		verify(deliveriesResourceOperator).processReadDelivery(anyOrderEntity(), anyString(), anyResourceOperation());
	}

	private static ResourceState<OrderEntity> anyOrderEntity() {

		return any();
	}
}
