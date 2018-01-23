/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;
import static com.elasticpath.rest.uri.URIUtil.format;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OrderTotalsResourceOperatorImpl.class})
public final class OrderTotalsResourceOperatorUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER_NAME = "totals";
	private static final String OTHER_URI = "items/other/uri=";

	@Mock
	private OrderTotalsResourceOperatorImpl resourceOperator;

	@Before
	public void setUp() {
		mediaType(ORDER);
	}

	@Test
	public void testProcessRead() {

		ResourceOperation operation = createRead(format(RESOURCE_SERVER_NAME, OTHER_URI));
		readOther(operation);
		when(resourceOperator.processRead(anyOrderEntity(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processRead(anyOrderEntity(), anyResourceOperation());
	}

	private static ResourceState<OrderEntity> anyOrderEntity() {

		return Mockito.any();
	}

}
