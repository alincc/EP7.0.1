/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.TaxesUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Default implementation of {@link TaxesUriBuilder}.
 */
@Named("taxesUriBuilder")
public final class TaxesUriBuilderImpl implements TaxesUriBuilder {

	private final String resourceServerName;
	private String sourceUri;

	/**
	 * Constructor.
	 * 
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	public TaxesUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public TaxesUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public String build() {
		assert sourceUri != null : "sourceURI is required.";
		return URIUtil.format(resourceServerName, sourceUri);
	}
}
