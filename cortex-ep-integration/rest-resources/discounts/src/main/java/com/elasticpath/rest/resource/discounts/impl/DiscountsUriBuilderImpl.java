/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.impl;

import com.elasticpath.rest.schema.uri.DiscountsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * URI Builder for discounts resource.
 */
@Named("discountsUriBuilder")
public final class DiscountsUriBuilderImpl implements DiscountsUriBuilder {

	private final String resourceServerName;
	private String sourceUri;

	/**
	 * Constructor.
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	public DiscountsUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public DiscountsUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public String build() {
		assert sourceUri != null : "sourceURI is required.";
		return URIUtil.format(resourceServerName, sourceUri);
	}
}
