/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.impl;

import org.junit.Assert;
import org.junit.Test;

import com.elasticpath.rest.resource.carts.lineitems.LineItems;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests {@link CartsUriBuilderImpl}.
 */
public final class CartsUriBuilderImplTest {

	private static final String RESOURCE_SERVER = "carts";
	private static final String SCOPE = "scope";
	private static final String CART_ID = "cartId123";
	private static final String ITEM_URI = "/item/balh/blah";
	private static final String FORM_URI = "/asd/sdh";


	@Test
	public void testCreateCartUri() {
		CartsUriBuilder cartsUriBuilder = new CartsUriBuilderImpl(RESOURCE_SERVER);

		String uri = cartsUriBuilder
				.setCartId(CART_ID)
				.setScope(SCOPE)
				.build();

		Assert.assertEquals(URIUtil.format(RESOURCE_SERVER, SCOPE, CART_ID), uri);
	}

	@Test
	public void testCreateAddItemToCartLinkUri() {
		CartsUriBuilder cartsUriBuilder = new CartsUriBuilderImpl(RESOURCE_SERVER);

		String uri = cartsUriBuilder
				.setCartId(CART_ID)
				.setScope(SCOPE)
				.setItemUri(ITEM_URI)
				.build();

		Assert.assertEquals(URIUtil.format(RESOURCE_SERVER, SCOPE, CART_ID, LineItems.URI_PART, ITEM_URI), uri);
	}

	@Test
	public void testCreateAddAddToCartFormLinkUri() {
		CartsUriBuilder cartsUriBuilder = new CartsUriBuilderImpl(RESOURCE_SERVER);

		String uri = cartsUriBuilder
				.setFormUri(FORM_URI)
				.build();

		Assert.assertEquals(URIUtil.format(RESOURCE_SERVER, FORM_URI, Form.URI_PART), uri);
	}

	@Test(expected = AssertionError.class)
	public void testAssertionErrorWhenCartIdMissing() {
		new CartsUriBuilderImpl(RESOURCE_SERVER)
				.setScope(SCOPE)
				.build();
	}

	@Test(expected = AssertionError.class)
	public void testAssertionErrorWhenScopeMissing() {
		new CartsUriBuilderImpl(RESOURCE_SERVER)
				.setCartId(CART_ID)
				.build();
	}
}
