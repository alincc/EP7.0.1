/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.schema.uri.AddressFormUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds the URI pointing to the address form.
 */
@Named("addressFormUriBuilder")
public final class AddressFormUriBuilderImpl implements AddressFormUriBuilder {

	private final String resourceServerName;

	private String scope;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	AddressFormUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public AddressFormUriBuilderImpl setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		return URIUtil.format(resourceServerName, scope, Form.URI_PART);
	}
}
