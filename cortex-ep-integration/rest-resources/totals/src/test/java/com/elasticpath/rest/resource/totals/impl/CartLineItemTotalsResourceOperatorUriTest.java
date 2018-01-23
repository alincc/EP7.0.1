/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static com.elasticpath.rest.definition.carts.CartsMediaTypes.LINE_ITEM;
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
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CartLineItemTotalsResourceOperatorImpl.class})
public final class CartLineItemTotalsResourceOperatorUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER_NAME = "totals";
	private static final String OTHER_URI = "items/other/uri=";

	@Mock
	private CartLineItemTotalsResourceOperatorImpl resourceOperator;

	@Before
	public void setUp() {
		mediaType(LINE_ITEM);
	}

	@Test
	public void testProcessRead() {

		ResourceOperation operation = createRead(format(RESOURCE_SERVER_NAME, OTHER_URI));
		readOther(operation);
		when(resourceOperator.processRead(anyLineItemEntity(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processRead(anyLineItemEntity(), anyResourceOperation());
	}

	private static ResourceState<LineItemEntity> anyLineItemEntity() {

		return Mockito.any();
	}

}
