/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.impl;

import javax.inject.Named;

import com.elasticpath.rest.resource.shipments.shippingoption.ShippingOption;
import com.elasticpath.rest.schema.uri.ShippingOptionUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Default implementation of {@link com.elasticpath.rest.schema.uri.ShippingOptionUriBuilder}.
 */
@Named("shippingOptionUriBuilder")
public final class ShippingOptionUriBuilderImpl implements ShippingOptionUriBuilder {

	private String shipmentUri;

	@Override
	public String build() {
		assert shipmentUri != null;
		return URIUtil.format(shipmentUri, ShippingOption.URI_PART);
	}

	@Override
	public ShippingOptionUriBuilder setSourceUri(final String shipmentUri) {
		this.shipmentUri = shipmentUri;
		return this;
	}

}
