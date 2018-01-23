/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Builds the URI pointing to the payment method list.
 */
@Named("paymentMethodListUriBuilder")
public final class PaymentMethodListUriBuilderImpl implements PaymentMethodListUriBuilder {

	private final String resourceServerName;

	private String scope;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	PaymentMethodListUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public PaymentMethodListUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		return URIUtil.format(resourceServerName, scope);
	}
}
