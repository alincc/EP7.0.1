/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfo;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests URI-related annotations on {@link EmailInfoResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailInfoResourceOperatorImplUriTest extends AbstractUriTest {

	private static final String ORDER_ID = "orderid=";
	private static final String SCOPE = "testscope";
	private static final String ORDERS = "orders";

	@Mock
	private EmailInfoResourceOperatorImpl resourceOperator;

	@Test
	public void testPathAnnotationForProcessReadEmailInfo() {

		String uri = URIUtil.format(ORDERS, SCOPE, ORDER_ID, EmailInfo.URI_PART);
		ResourceOperation operation = createRead(uri);
		mediaType(ORDER);
		readOther(operation);
		when(resourceOperator.processReadEmailInfo(anyOrderEntity(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadEmailInfo(anyOrderEntity(), anyResourceOperation());
	}

	private static ResourceState<OrderEntity> anyOrderEntity() {

		return any();
	}
}
