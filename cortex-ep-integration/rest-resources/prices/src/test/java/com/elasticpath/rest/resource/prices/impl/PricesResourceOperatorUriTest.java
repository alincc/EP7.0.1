/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests URI-related annotation on {@link PricesResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PricesResourceOperatorImpl.class })
public final class PricesResourceOperatorUriTest extends AbstractUriTest {
	private static final String LINE_ITEM_URI = "/carts/scope/mqytamjrmy2/lineitems/gen3egrstmmdeg=";
	private static final String ITEM_URI = "/items/scope/gen3egrstmmdeg=";
	private static final String ITEM_DEFINITION_URI = "/itemdefinitions/scope/gen3egrstmmdeg=";
	private static final String PRICES = "prices";

	@Mock
	private PricesResourceOperatorImpl resourceOperator;

	@Test
	public void testProcessReadForCartLineItem() {
		mediaType(CartsMediaTypes.LINE_ITEM);
		readOther(TestResourceOperationFactory.createRead(LINE_ITEM_URI));
		when(resourceOperator.processReadForCartLineItem(Matchers.<ResourceState<LineItemEntity>>any(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(TestResourceOperationFactory.createRead(URIUtil.format(PRICES, LINE_ITEM_URI)), resourceOperator);

		verify(resourceOperator).processReadForCartLineItem(Matchers.<ResourceState<LineItemEntity>>any(), anyResourceOperation());
	}

	@Test
	public void testProcessReadForItem() {
		mediaType(ItemsMediaTypes.ITEM);
		readOther(TestResourceOperationFactory.createRead(ITEM_URI));
		when(resourceOperator.processReadForItem(Matchers.<ResourceState<ItemEntity>>any(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(TestResourceOperationFactory.createRead(URIUtil.format(PRICES, ITEM_URI)), resourceOperator);

		verify(resourceOperator).processReadForItem(Matchers.<ResourceState<ItemEntity>>any(), anyResourceOperation());
	}

	@Test
	public void testProcessReadForItemDefinition() {
		mediaType(ItemdefinitionsMediaTypes.ITEM_DEFINITION);
		readOther(TestResourceOperationFactory.createRead(ITEM_DEFINITION_URI));
		when(resourceOperator.processReadForItemDefinition(Matchers.<ResourceState<ItemDefinitionEntity>>any(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(TestResourceOperationFactory.createRead(URIUtil.format(PRICES, ITEM_DEFINITION_URI)), resourceOperator);

		verify(resourceOperator).processReadForItemDefinition(Matchers.<ResourceState<ItemDefinitionEntity>>any(), anyResourceOperation());
	}
}
