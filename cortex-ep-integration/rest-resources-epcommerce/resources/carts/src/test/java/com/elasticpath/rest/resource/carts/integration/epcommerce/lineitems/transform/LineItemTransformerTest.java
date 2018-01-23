/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.domain.wrapper.LineItem;


/**
 * The test for {@link LineItemTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemTransformerTest {
	private static final String CART_GUID = "cartGuid";
	private static final String ITEM_GUID = "itemGuid";
	private static final String LINE_ITEM_GUID = "lineItemGuid";
	private static final int QUANTITY = 8;

	@Mock
	private ShoppingItem shoppingItem;
	private final LineItemTransformerImpl transformer = new LineItemTransformerImpl();

	@Before
	public void setupHappyCollaborators() {
		when(shoppingItem.getGuid()).thenReturn(LINE_ITEM_GUID);
		when(shoppingItem.getQuantity()).thenReturn(QUANTITY);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void ensureTransformToDomainIsUnsupported() {
		transformer.transformToDomain(null);
	}

	@Test
	public void ensureLineItemCanBeTransformedToEntityCorrectly() {
		LineItemEntity expectedEntity = LineItemEntity.builder().withCartId(CART_GUID)
				.withItemId(ITEM_GUID)
				.withLineItemId(LINE_ITEM_GUID)
				.withQuantity(QUANTITY)
				.build();


		LineItem lineItemToTransform = ResourceTypeFactory.createResourceEntity(LineItem.class)
				.setCartId(CART_GUID)
				.setItemId(ITEM_GUID)
				.setShoppingItem(shoppingItem);

		LineItemEntity result = transformer.transformToEntity(lineItemToTransform);

		assertEquals("The line item entity from transformation was not the same as expected",
				expectedEntity, result);
	}
}
