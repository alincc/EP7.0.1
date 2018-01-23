/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.resource.carts.lineitems.LineItems;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Creates carts URIs.
 */
@Named("cartsUriBuilder")
public final class CartsUriBuilderImpl implements CartsUriBuilder {

	private final String resourceServerName;

	private String scope;
	private String cartId;
	private String lineItemsPathPart;
	private String formUriPathPart;
	private String formUri;
	private String itemUri;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	public CartsUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public CartsUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public CartsUriBuilder setCartId(final String cartId) {
		this.cartId = cartId;
		return this;
	}

	@Override
	public CartsUriBuilder setFormUri(final String formUri) {
		this.formUri = formUri;
		this.formUriPathPart = Form.URI_PART;
		this.lineItemsPathPart = null;
		return this;
	}

	@Override
	public CartsUriBuilder setItemUri(final String itemUri) {
		this.itemUri = itemUri;
		this.formUriPathPart = null;
		this.lineItemsPathPart = LineItems.URI_PART;
		return this;
	}

	@Override
	public String build() {
		assert !(formUri == null && (scope == null || cartId == null)) : "cartId and scope must set if formUri is null.";

		return URIUtil.format(resourceServerName, scope, cartId, lineItemsPathPart, formUri, itemUri, formUriPathPart);
	}
}
