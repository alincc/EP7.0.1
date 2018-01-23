/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Info;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implements {@link PaymentMethodInfoUriBuilder}.
 */
@Named("paymentMethodInfoUriBuilder")
public class PaymentMethodInfoUriBuilderImpl implements PaymentMethodInfoUriBuilder {
	private final String resourceServerName;
	private String orderUri;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public PaymentMethodInfoUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public PaymentMethodInfoUriBuilder setSourceUri(final String orderUri) {
		this.orderUri = orderUri;
		return this;
	}

	@Override
	public String build() {
		assert orderUri != null : "orderUri must be specified";
		return URIUtil.format(resourceServerName, Info.URI_PART, orderUri);
	}
}
