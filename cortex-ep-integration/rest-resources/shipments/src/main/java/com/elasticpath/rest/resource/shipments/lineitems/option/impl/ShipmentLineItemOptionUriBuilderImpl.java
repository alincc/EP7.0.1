/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.impl;

import javax.inject.Named;

import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipments.lineitems.option.rel.ShipmentLineItemOptionResourceRels;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * URI Builder for {@linkplain ShipmentLineItemOptionUriBuilder}.
 */
@Named("shipmentLineItemOptionUriBuilder")
public final class ShipmentLineItemOptionUriBuilderImpl implements ShipmentLineItemOptionUriBuilder {

	private String sourceUri;

	private String optionId;

	@Override
	public ShipmentLineItemOptionUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public ShipmentLineItemOptionUriBuilder setOptionId(final String optionId) {
		if (optionId != null) {
			this.optionId = Base32Util.encode(optionId);
		}
		return this;
	}

	@Override
	public String build() {
		assert sourceUri != null : "sourceURI is required.";
		return URIUtil.format(sourceUri, ShipmentLineItemOptionResourceRels.LINE_ITEMS_OPTIONS_REL, optionId);
	}

}
