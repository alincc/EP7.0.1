/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * URI Builder for shipments resource.
 */
@Named("shipmentsUriBuilder")
public final class ShipmentsUriBuilderImpl implements ShipmentsUriBuilder {

	private final String resourceServerName;

	private String shipmentId;
	private String sourceUri;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	public ShipmentsUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public ShipmentsUriBuilder setShipmentId(final String shipmentId) {
		if (shipmentId != null) {
			this.shipmentId = Base32Util.encode(shipmentId);
		}
		return this;
	}

	@Override
	public ShipmentsUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}


	@Override
	public String build() {
		assert sourceUri != null : "Source uri must be set.";

		return URIUtil.format(resourceServerName, sourceUri, shipmentId);
	}
}
