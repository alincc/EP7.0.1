/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.value.impl;

import javax.inject.Named;

import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipments.lineitems.option.value.rel.ShipmentLineItemOptionValueResourceRels;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionValueUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * URI Builder for shipment line item option value resources.
 */
@Named("shipmentLineItemOptionValueUriBuilder")
public final class ShipmentLineItemOptionValueUriBuilderImpl implements ShipmentLineItemOptionValueUriBuilder {

	private String sourceUri;

	private String optionValueId;

	@Override
	public ShipmentLineItemOptionValueUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public ShipmentLineItemOptionValueUriBuilder setOptionValueId(final String optionValueId) {
		if (optionValueId != null) {
			this.optionValueId = Base32Util.encode(optionValueId);
		}
		return this;
	}

	@Override
	public String build() {
		assert sourceUri != null : "sourceURI is required.";
		assert optionValueId != null : "optionValueId is required.";
		return URIUtil.format(sourceUri, ShipmentLineItemOptionValueResourceRels.LINE_ITEMS_OPTION_VALUES_REL, optionValueId);
	}

}
