/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

import javax.inject.Named;

import com.elasticpath.rest.resource.shipments.lineitems.rel.ShipmentLineItemResourceRels;
import com.elasticpath.rest.schema.uri.ShipmentLineItemsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * URI Builder for shipment line items resource.
 */
@Named("shipmentLineItemsUriBuilder")
public final class ShipmentLineItemsUriBuilderImpl implements ShipmentLineItemsUriBuilder {

	private String sourceUri;

	@Override
	public ShipmentLineItemsUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public String build() {
		assert sourceUri != null : "sourceURI is required.";
		return URIUtil.format(sourceUri, ShipmentLineItemResourceRels.LINE_ITEMS_REL);
	}
}
