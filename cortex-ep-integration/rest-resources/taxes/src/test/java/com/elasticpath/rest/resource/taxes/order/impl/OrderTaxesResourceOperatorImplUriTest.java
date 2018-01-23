/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.order.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.resource.taxes.impl.TaxesUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.TaxesUriBuilder;

/**
 * URI test for {@link OrderTaxesResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderTaxesResourceOperatorImplUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER_NAME = "taxes";
	private static final String ARBITRARY_SOURCE_URI = "i/am/a/resource=";

	@Mock private OperationResult operationResult;

	@Spy private final OrderTaxesResourceOperatorImpl resourceOperator = new OrderTaxesResourceOperatorImpl(null);

	private final TaxesUriBuilder taxesUriBuilder = new TaxesUriBuilderImpl(RESOURCE_SERVER_NAME);

	@Test
	public void testPathAnnotationForProcessRead() {
		mediaType(OrdersMediaTypes.ORDER);
		String taxesUri = taxesUriBuilder.setSourceUri(ARBITRARY_SOURCE_URI).build();
		ResourceOperation operation = createRead(taxesUri);
		readOther(operation);
		doReturn(operationResult)
				.when(resourceOperator)
				.processRead(anyOrderEntity(), anyResourceOperation());

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processRead(anyOrderEntity(), anyResourceOperation());
	}

	private static ResourceState<OrderEntity> anyOrderEntity() {
		return Mockito.any();
	}

}
