/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

import javax.inject.Named;

import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipments.lineitems.rel.ShipmentLineItemResourceRels;
import com.elasticpath.rest.schema.uri.ShipmentLineItemUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * URI Builder for shipment line item resource.
 */
@Named("shipmentLineItemUriBuilder")
public final class ShipmentLineItemUriBuilderImpl implements ShipmentLineItemUriBuilder {

	private String sourceUri;

	private String lineItemId;

	@Override
	public ShipmentLineItemUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public ShipmentLineItemUriBuilder setLineItemId(final String lineItemId) {
		if (lineItemId != null) {
			this.lineItemId = Base32Util.encode(lineItemId);
		}
		return this;
	}

	@Override
	public String build() {
		assert sourceUri != null : "sourceURI is required.";
		assert lineItemId != null : "lineItemId is required.";
		return URIUtil.format(sourceUri, ShipmentLineItemResourceRels.LINE_ITEMS_REL, lineItemId);
	}

}
