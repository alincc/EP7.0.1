/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.impl;

import javax.inject.Named;

import com.elasticpath.rest.resource.shipments.lineitems.option.rel.ShipmentLineItemOptionResourceRels;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * URI Builder for {@linkplain ShipmentLineItemOptionsUriBuilder}.
 */
@Named("shipmentLineItemOptionsUriBuilder")
public final class ShipmentLineItemOptionsUriBuilderImpl implements ShipmentLineItemOptionsUriBuilder {

	private String sourceUri;

	@Override
	public ShipmentLineItemOptionsUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public String build() {
		assert sourceUri != null : "sourceURI is required.";
		return URIUtil.format(sourceUri, ShipmentLineItemOptionResourceRels.LINE_ITEMS_OPTIONS_REL);
	}
}
