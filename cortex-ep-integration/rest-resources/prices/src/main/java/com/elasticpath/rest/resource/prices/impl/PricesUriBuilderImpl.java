/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.PricesUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds a URI for a price.
 */
@Named("pricesUriBuilder")
public final class PricesUriBuilderImpl implements PricesUriBuilder {
	private final String resourceServerName;
	private String associationUri;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public PricesUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public PricesUriBuilder setSourceUri(final String associationUri) {
		this.associationUri = associationUri;
		return this;
	}

	@Override
	public String build() {
		return URIUtil.format(resourceServerName, associationUri);
	}
}
