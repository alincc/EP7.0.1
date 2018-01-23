/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.impl;

import javax.inject.Named;

import com.elasticpath.rest.resource.carts.lineitems.LineItems;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Creates cart line item URIs.
 */
@Named("cartLineItemsUriBuilder")
public final class CartLineItemsUriBuilderImpl implements CartLineItemsUriBuilder {

	private String cartUri;
	private String lineItemId;

	@Override
	public CartLineItemsUriBuilder setSourceUri(final String cartUri) {
		this.cartUri = cartUri;
		return this;
	}

	@Override
	public CartLineItemsUriBuilder setLineItemId(final String lineItemId) {
		this.lineItemId = lineItemId;
		return this;
	}

	@Override
	public String build() {
		assert cartUri != null : "cartUri has to be set";
		return URIUtil.format(cartUri, LineItems.URI_PART, lineItemId);
	}
}
