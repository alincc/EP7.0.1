/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.impl;

import javax.inject.Named;

import com.elasticpath.rest.resource.orders.deliveries.Deliveries;
import com.elasticpath.rest.schema.uri.DeliveryListUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Build the URI for an order's delivery list.
 */
@Named("deliveryListUriBuilder")
public final class DeliveryListUriBuilderImpl implements DeliveryListUriBuilder {

	private String orderUri;

	@Override
	public String build() {
		assert orderUri != null : "sourceUri required.";

		return URIUtil.format(orderUri, Deliveries.URI_PART);
	}

	@Override
	public DeliveryListUriBuilder setSourceUri(final String sourceUri) {
		this.orderUri = sourceUri;
		return this;
	}
}
