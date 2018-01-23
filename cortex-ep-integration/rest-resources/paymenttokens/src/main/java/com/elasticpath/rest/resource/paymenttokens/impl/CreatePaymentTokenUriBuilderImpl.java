/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilder}.
 */
@Named("createPaymentTokenUriBuilder")
public class CreatePaymentTokenUriBuilderImpl implements CreatePaymentTokenUriBuilder {
	private final String resourceServerName;
	private String ownerUri;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public CreatePaymentTokenUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}


	@Override
	public CreatePaymentTokenUriBuilder setSourceUri(final String sourceUri) {
		this.ownerUri = sourceUri;
		return this;
	}

	@Override
	public String build() {
		assert ownerUri != null : "The owner URI is required.";
		return URIUtil.format(resourceServerName, ownerUri);
	}
}
