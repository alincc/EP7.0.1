/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.linker.impl;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilder;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Tests {@link LineItemToLineItemsLinkHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemToLineItemsLinkHandlerTest {
	public static final String SCOPE = "scope";
	public static final String CART_ID = "cartId";
	public static final String CART_URI = "/cartUri";
	public static final String LINE_ITEMS_URI = "/lineItemsUri";

	@Mock
	private CartsUriBuilderFactory cartsUriBuilderFactory;
	@Mock
	private CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;
	@InjectMocks
	private LineItemToLineItemsLinkHandler lineItemToLineItemsLinkHandler;
	@Mock
	private CartsUriBuilder cartsUriBuilder;
	@Mock
	private CartLineItemsUriBuilder cartLineItemsUriBuilder;
	private ResourceState<LineItemEntity> lineItem;

	@Before
	public void setUpCommonTestComponents() {
		lineItem = ResourceState.Builder.create(LineItemEntity.builder()
																.withCartId(CART_ID)
																.build())
										.withScope(SCOPE)
										.build();

		given(cartsUriBuilderFactory.get()).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setScope(SCOPE)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setCartId(CART_ID)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.build()).willReturn(CART_URI);

		given(cartLineItemsUriBuilderFactory.get()).willReturn(cartLineItemsUriBuilder);
		given(cartLineItemsUriBuilder.setSourceUri(CART_URI)).willReturn(cartLineItemsUriBuilder);
		given(cartLineItemsUriBuilder.build()).willReturn(LINE_ITEMS_URI);
	}

	@Test
	public void ensureCartLinkIsCreatedCorrectly() {
		Iterable<ResourceLink> links = lineItemToLineItemsLinkHandler.getLinks(lineItem);

		assertThat(links, hasItems(ElementListFactory.createListWithoutElement(LINE_ITEMS_URI, CollectionsMediaTypes.LINKS.id())));
	}
}
