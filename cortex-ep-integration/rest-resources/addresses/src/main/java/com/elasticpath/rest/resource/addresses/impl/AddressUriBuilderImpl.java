/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.AddressUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds the URI pointing to the specific address.
 */
@Named("addressUriBuilder")
public final class AddressUriBuilderImpl implements AddressUriBuilder {

	private final String resourceServerName;

	private String scope;
	private String addressId;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	AddressUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public AddressUriBuilderImpl setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		assert addressId != null : "addressId required.";
		return URIUtil.format(resourceServerName, scope, addressId);
	}

	@Override
	public AddressUriBuilderImpl setAddressId(final String addressId) {
		this.addressId = addressId;
		return this;
	}
}
