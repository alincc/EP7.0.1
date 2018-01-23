/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilder;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Tests the {@link com.elasticpath.rest.resource.prices.link.PriceToCartLineItemLinkHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PriceToCartLineItemLinkHandlerTest {
	public static final String CART_URI = "/cartUri";
	public static final String LINE_ITEM_ID = "lineItemId";
	public static final String CART_LINE_ITEM_URI = "/cartLineItemUri";
	public static final String CART_ID = "cartId";
	public static final String SCOPE = "scope";
	@Mock
	private CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;
	@Mock
	private CartsUriBuilderFactory cartsUriBuilderFactory;
	@InjectMocks
	private PriceToCartLineItemLinkHandler linkHandler;

	private ResourceState<CartLineItemPriceEntity> cartLineItemPriceEntity;

	@Before
	public void setupCommonTestComponents() {
		CartsUriBuilder cartsUriBuilder = mock(CartsUriBuilder.class);
		given(cartsUriBuilderFactory.get()).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setCartId(CART_ID)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setScope(SCOPE)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.build()).willReturn(CART_URI);

		CartLineItemsUriBuilder cartLineItemsUriBuilder = mock(CartLineItemsUriBuilder.class);
		given(cartLineItemsUriBuilderFactory.get()).willReturn(cartLineItemsUriBuilder);
		given(cartLineItemsUriBuilder.setSourceUri(CART_URI)).willReturn(cartLineItemsUriBuilder);
		given(cartLineItemsUriBuilder.setLineItemId(LINE_ITEM_ID)).willReturn(cartLineItemsUriBuilder);
		given(cartLineItemsUriBuilder.build()).willReturn(CART_LINE_ITEM_URI);

		cartLineItemPriceEntity = ResourceState.Builder.create(CartLineItemPriceEntity.builder()
																				.withLineItemId(LINE_ITEM_ID)
																				.withCartId(CART_ID)
																				.build())
													.withScope(SCOPE)
													.build();

	}

	@Test
	public void ensureCartLineItemLinkIsReturned() {
		assertThat(linkHandler.getLinks(cartLineItemPriceEntity), hasItems(ResourceLinkFactory.create(CART_LINE_ITEM_URI,
																									CartsMediaTypes.LINE_ITEM.id(),
																									PriceRepresentationRels.LINE_ITEM_REL,
																									PriceRepresentationRels.PRICE_REV)));
	}
}
