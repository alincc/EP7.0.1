/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.shipping.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.resource.addresses.shipping.Shipping;
import com.elasticpath.rest.schema.uri.ShippingAddressListUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds the URI pointing to the Shipping addresses list resource.
 */
@Named("shippingAddressListUriBuilder")
public final class ShippingAddressListUriBuilderImpl implements ShippingAddressListUriBuilder {

	private final String resourceServerName;

	private String scope;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name.
	 */
	@Inject
	ShippingAddressListUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public ShippingAddressListUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		return URIUtil.format(resourceServerName, scope, Shipping.URI_PART);
	}
}
