/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static com.elasticpath.rest.definition.carts.CartsMediaTypes.CART;
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
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CartTotalsResourceOperatorImpl.class})
public final class CartTotalsResourceOperatorUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER_NAME = "totals";
	private static final String OTHER_URI = "items/other/uri=";

	@Mock
	private CartTotalsResourceOperatorImpl resourceOperator;

	@Before
	public void setUp() {
		mediaType(CART);
	}

	@Test
	public void testProcessRead() {

		ResourceOperation operation = createRead(format(RESOURCE_SERVER_NAME, OTHER_URI));
		readOther(operation);
		when(resourceOperator.processRead(anyCartEntity(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processRead(anyCartEntity(), anyResourceOperation());
	}

	private static ResourceState<CartEntity> anyCartEntity() {

		return Mockito.any();
	}

}
