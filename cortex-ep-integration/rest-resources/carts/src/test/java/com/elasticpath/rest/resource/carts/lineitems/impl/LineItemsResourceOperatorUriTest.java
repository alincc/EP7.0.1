/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createCreate;
import static com.elasticpath.rest.TestResourceOperationFactory.createDelete;
import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static com.elasticpath.rest.TestResourceOperationFactory.createUpdate;
import static com.elasticpath.rest.definition.carts.CartsMediaTypes.CART;
import static org.mockito.Matchers.eq;
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
import com.elasticpath.rest.resource.carts.impl.CartsUriBuilderImpl;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests the LineItemOperators.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LineItemResourceOperatorImpl.class})
public class LineItemsResourceOperatorUriTest extends AbstractUriTest {

	private static final String LINE_ITEM_ID = "4ndg5pjosxx6x4ria6xfclmq3u=";
	private static final String CART_ID = "5ngg5pjfsxx6x4ria6xfclmq4u=";
	private static final String TEST_SCOPE = "siu";
	private static final String RESOURCE_NAME = "carts";
	public static final String ITEM_ID = "5ngg5pjfsxx6x4ria7xfclmq4y=";

	private final CartsUriBuilderImpl cartsUriBuilder = new CartsUriBuilderImpl(RESOURCE_NAME);
	private static final String BASE_CART_URI = new CartsUriBuilderImpl(RESOURCE_NAME).setScope(TEST_SCOPE)
																					.setCartId(CART_ID)
																					.build();
	private final CartLineItemsUriBuilderImpl cartLineItemsUriBuilder = new CartLineItemsUriBuilderImpl();

	@Mock
	private LineItemResourceOperatorImpl lineItemResourceOperator;

	@Before
	public void setUp() {

		mediaType(CART);
	}

	@Test
	public void testProcessCreateLineItemIsMatched() {

		String uri = cartsUriBuilder
				.setScope(TEST_SCOPE)
				.setCartId(CART_ID)
				.setItemUri(URIUtil.format("items/siu", ITEM_ID))
				.build();
		ResourceOperation operation = createCreate(uri, null);
		readOther(operation);
		when(lineItemResourceOperator.processCreateLineItem(anyCartEntity(), eq(ITEM_ID), eq(operation)))
				.thenReturn(operationResult);

		dispatchMethod(operation, lineItemResourceOperator);

		verify(lineItemResourceOperator).processCreateLineItem(anyCartEntity(), eq(ITEM_ID), eq(operation));
	}

	@Test
	public void testProcessDeleteLineItemIsMatched() {

		String uri = cartLineItemsUriBuilder.setSourceUri(BASE_CART_URI)
											.setLineItemId(LINE_ITEM_ID)
											.build();
		ResourceOperation operation = createDelete(uri);
		readOther(operation);
		when(lineItemResourceOperator.processDeleteLineItem(anyCartEntity(), eq(LINE_ITEM_ID), eq(operation)))
				.thenReturn(operationResult);

		dispatchMethod(operation, lineItemResourceOperator);

		verify(lineItemResourceOperator).processDeleteLineItem(anyCartEntity(), eq(LINE_ITEM_ID), eq(operation));
	}

	@Test
	public void testProcessReadLineItemsIsMatchedCorrectly() {

		String uri = cartLineItemsUriBuilder.setSourceUri(BASE_CART_URI)
											.build();
		ResourceOperation operation = createRead(uri);
		readOther(operation);
		when(lineItemResourceOperator.processReadLineItems(anyCartEntity(), eq(operation)))
				.thenReturn(operationResult);

		dispatchMethod(operation, lineItemResourceOperator);

		verify(lineItemResourceOperator).processReadLineItems(anyCartEntity(), eq(operation));
	}

	@Test
	public void testProcessDeleteLineItemsIsMatchedCorrectly() {

		String uri = cartLineItemsUriBuilder.setSourceUri(BASE_CART_URI)
											.build();
		ResourceOperation operation = createDelete(uri);
		readOther(operation);
		when(lineItemResourceOperator.processDeleteLineItems(anyCartEntity(), eq(operation)))
				.thenReturn(operationResult);

		dispatchMethod(operation, lineItemResourceOperator);

		verify(lineItemResourceOperator).processDeleteLineItems(anyCartEntity(), eq(operation));
	}

	@Test
	public void testProcessReadLineItemIsMatchedCorrectly() {

		String uri = cartLineItemsUriBuilder.setSourceUri(BASE_CART_URI)
											.setLineItemId(LINE_ITEM_ID)
											.build();
		ResourceOperation operation = createRead(uri);
		readOther(operation);
		when(lineItemResourceOperator.processReadLineItem(anyCartEntity(), eq(LINE_ITEM_ID), eq(operation)))
				.thenReturn(operationResult);

		dispatchMethod(operation, lineItemResourceOperator);

		verify(lineItemResourceOperator).processReadLineItem(anyCartEntity(), eq(LINE_ITEM_ID), eq(operation));
	}

	@Test
	public void testProcessUpdateLineItemIsMatchedCorrectly() {

		String uri = cartLineItemsUriBuilder.setSourceUri(BASE_CART_URI)
											.setLineItemId(LINE_ITEM_ID)
											.build();
		ResourceOperation operation = createUpdate(uri, null);
		readOther(operation);
		when(lineItemResourceOperator.processUpdateLineItem(anyCartEntity(), eq(LINE_ITEM_ID), eq(operation)))
				.thenReturn(operationResult);

		dispatchMethod(operation, lineItemResourceOperator);

		verify(lineItemResourceOperator).processUpdateLineItem(anyCartEntity(), eq(LINE_ITEM_ID), eq(operation));
	}

	private static ResourceState<CartEntity> anyCartEntity() {

		return Mockito.any();
	}
}
