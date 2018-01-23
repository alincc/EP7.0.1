/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.billing.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.resource.addresses.billing.Billing;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds the URI pointing to the billing address list.
 */
@Named("billingAddressListUriBuilder")
public final class BillingAddressListUriBuilderImpl implements BillingAddressListUriBuilder {

	private final String resourceServerName;

	private String scope;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	BillingAddressListUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public BillingAddressListUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		return URIUtil.format(resourceServerName, scope, Billing.URI_PART);
	}
}
