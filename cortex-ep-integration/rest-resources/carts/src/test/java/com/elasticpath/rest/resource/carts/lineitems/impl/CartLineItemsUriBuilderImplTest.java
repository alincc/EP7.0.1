/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.impl;

import org.junit.Assert;
import org.junit.Test;

import com.elasticpath.rest.resource.carts.lineitems.LineItems;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests {@link com.elasticpath.rest.resource.carts.impl.CartsUriBuilderImpl}.
 */
public final class CartLineItemsUriBuilderImplTest {

	private static final String CART_LINE_ITEM_ID = "line123";
	private static final String CART_URI = "/asd/sdh";

	@Test
	public void testCreateLineItemIdUri() {
		CartLineItemsUriBuilder cartLineItemsUriBuilder = new CartLineItemsUriBuilderImpl();

		String uri = cartLineItemsUriBuilder
				.setSourceUri(CART_URI)
				.setLineItemId(CART_LINE_ITEM_ID)
				.build();

		Assert.assertEquals(URIUtil.format(CART_URI, LineItems.URI_PART, CART_LINE_ITEM_ID), uri);
	}

	@Test
	public void testCreateLineItemsUri() {
		CartLineItemsUriBuilder cartLineItemsUriBuilder = new CartLineItemsUriBuilderImpl();

		String uri = cartLineItemsUriBuilder
				.setSourceUri(CART_URI)
				.build();

		Assert.assertEquals(URIUtil.format(CART_URI, LineItems.URI_PART), uri);
	}


	@Test(expected = AssertionError.class)
	public void testAssertionErrorWhenCartUriMissing() {
		new CartLineItemsUriBuilderImpl()
				.build();
	}
}
