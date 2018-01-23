/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.impl;

import javax.inject.Named;

import com.elasticpath.rest.resource.shipments.addresses.ShippingAddress;
import com.elasticpath.rest.schema.uri.ShippingAddressUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link com.elasticpath.rest.schema.uri.ShippingAddressUriBuilder}.
 */
@Named("shippingAddressUriBuilder")
public final class ShippingAddressUriBuilderImpl implements ShippingAddressUriBuilder {

	private String parentUri;

	@Override
	public String build() {
		assert parentUri != null : "The parent URI is required.";
		return URIUtil.format(parentUri, ShippingAddress.URI_PART);
	}

	@Override
	public ShippingAddressUriBuilder setSourceUri(final String parentUri) {
		this.parentUri = parentUri;
		return this;
	}
}
