/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests correct dispatch of order uris to orders resource.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({OrdersResourceOperatorImpl.class})
public final class OrdersResourceOperatorImplUriTest extends AbstractUriTest {

	private static final String ORDER_ID = "4ndg5pjosxx6x4ria6xfclmq3u=";
	private static final String SCOPE = "scope";
	private static final String RESOURCE_NAME = "orders";

	@Mock
	private OrdersResourceOperatorImpl ordersResourceOperator;

	@Test
	public void testUriDispatchToProcessReadMethod() {

		String uri = URIUtil.format(RESOURCE_NAME, SCOPE, ORDER_ID);
		ResourceOperation readOrderOperation = TestResourceOperationFactory.createRead(uri);
		when(ordersResourceOperator.processReadOrder(anyString(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(readOrderOperation, ordersResourceOperator);

		verify(ordersResourceOperator).processReadOrder(anyString(), anyString(), anyResourceOperation());
	}
}
