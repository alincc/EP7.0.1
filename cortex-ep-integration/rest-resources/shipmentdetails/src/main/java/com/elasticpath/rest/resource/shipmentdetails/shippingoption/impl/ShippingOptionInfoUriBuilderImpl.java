/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.resource.shipmentdetails.ShippingOptionInfo;
import com.elasticpath.rest.schema.uri.ShippingOptionInfoUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link ShippingOptionInfoUriBuilder}.
 */
@Named("shippingOptionInfoUriBuilder")
public class ShippingOptionInfoUriBuilderImpl implements ShippingOptionInfoUriBuilder {
	private final String resourceServerName;
	private String shipmentDetailsId;
	private String scope;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public ShippingOptionInfoUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public ShippingOptionInfoUriBuilder setShipmentDetailsId(final String shipmentDetailsId) {
		this.shipmentDetailsId = shipmentDetailsId;
		return this;
	}

	@Override
	public ShippingOptionInfoUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope must be set";
		assert shipmentDetailsId != null : "shipment details id must be set";

		return URIUtil.format(resourceServerName, scope, shipmentDetailsId, ShippingOptionInfo.URI_PART);
	}
}
