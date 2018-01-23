/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static com.elasticpath.rest.definition.items.ItemsMediaTypes.ITEM;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests URI-related annotations on {@link CartsResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CartsResourceOperatorImpl.class})
public class CartsResourceOperatorImplUriTest extends AbstractUriTest {

	private static final String CART_ID = "4ndg5pjosxx6x4ria6xfclmq3u=";
	private static final String SCOPE = "siu";
	private static final String RESOURCE_SERVER = "carts";
	private static final String CART_URI = new CartsUriBuilderImpl(RESOURCE_SERVER)
			.setCartId(CART_ID)
			.setScope(SCOPE)
			.build();
	private static final String FORM_URI = "items/blah=";

	private final String formUri = new CartsUriBuilderImpl(RESOURCE_SERVER)
			.setFormUri(FORM_URI)
			.build();

	@Mock
	private CartsResourceOperatorImpl resourceOperator;

	@Test
	public void testProcessCartReadIsMatched() {

		ResourceOperation operation = createRead(CART_URI);
		when(resourceOperator.processCartRead(anyString(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processCartRead(anyString(), anyString(), anyResourceOperation());
	}

	@Test
	public void testProcessReadFormIsMatched() {

		ResourceOperation operation = createRead(formUri);
		mediaType(ITEM);
		readOther(operation);
		when(resourceOperator.processReadForm(anyItemEntity(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadForm(anyItemEntity(), anyResourceOperation());
	}

	private static ResourceState<ItemEntity> anyItemEntity() {

		return Mockito.any();
	}
}
