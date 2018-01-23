/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.transform;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilder;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;


/**
 * The test of {@link LineItemDetailsTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class LineItemDetailsTransformerTest {

	private static final String SCOPE = "scope";
	private static final String DECODED_CART_ID = "cart";
	private static final String CART_ID = Base32Util.encode(DECODED_CART_ID);
	private static final String DECODED_LINE_ITEM_ID = "lineItem";
	private static final String LINE_ITEM_ID = Base32Util.encode(DECODED_LINE_ITEM_ID);
	private static final String ITEM_ID = "itemId";
	private static final String CART_URI = "/cart/uri";
	private static final String LINE_ITEM_URI = "/line/item/uri";

	@Mock
	private CartsUriBuilderFactory cartsUriBuilderFactory;
	@Mock
	private CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;
	@InjectMocks
	private LineItemDetailsTransformer lineItemTransformer;

	@Before
	public void setUpHappyCollaborators() {
		CartsUriBuilder mockCartsUriBuilder = mock(CartsUriBuilder.class);

		when(cartsUriBuilderFactory.get()).thenReturn(mockCartsUriBuilder);
		when(mockCartsUriBuilder.setCartId(CART_ID)).thenReturn(mockCartsUriBuilder);
		when(mockCartsUriBuilder.setScope(SCOPE)).thenReturn(mockCartsUriBuilder);
		when(mockCartsUriBuilder.build()).thenReturn(CART_URI);

		CartLineItemsUriBuilder mockCartLineItemUriBuilder = mock(CartLineItemsUriBuilder.class);
		when(cartLineItemsUriBuilderFactory.get()).thenReturn(mockCartLineItemUriBuilder);
		when(mockCartLineItemUriBuilder.setSourceUri(CART_URI)).thenReturn(mockCartLineItemUriBuilder);
		when(mockCartLineItemUriBuilder.setLineItemId(LINE_ITEM_ID)).thenReturn(mockCartLineItemUriBuilder);
		when(mockCartLineItemUriBuilder.build()).thenReturn(LINE_ITEM_URI);
	}

	@Test
	public void ensureEntityCanBeTransformedToRepresentationCorrectly() {
		LineItemEntity lineItemEntity = createLineItemEntity(DECODED_CART_ID, ITEM_ID, DECODED_LINE_ITEM_ID, 1);

		Self expectedSelf = SelfFactory.createSelf(LINE_ITEM_URI);

		ResourceState<LineItemEntity> resultRepresentation = lineItemTransformer.transform(SCOPE, lineItemEntity);

		assertResourceState(resultRepresentation)
				.self(expectedSelf);
	}


	@Test
	public void ensureRepresentationCanBeTransformedToEntityCorrectly() {
		LineItemEntity expectedLineItemEntity = createLineItemEntity(DECODED_CART_ID, ITEM_ID, DECODED_LINE_ITEM_ID, 1);

		LineItemEntity result = lineItemTransformer.transformToDto(CART_ID, LINE_ITEM_ID, ITEM_ID, 1);
		assertEquals("The resulting line item entity should be the same as expected.", expectedLineItemEntity, result);
	}

	private LineItemEntity createLineItemEntity(final String decodedCartId,
												final String itemId,
												final String decodedLineItemId,
												final Integer quantity) {

		return LineItemEntity.builder()
				.withCartId(decodedCartId)
				.withItemId(itemId)
				.withLineItemId(decodedLineItemId)
				.withQuantity(quantity)
				.build();
	}
}
