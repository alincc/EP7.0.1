/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.TotalsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * TotalsUriBuilderImpl.
 */
@Named("totalsUriBuilder")
public final class TotalsUriBuilderImpl implements TotalsUriBuilder {

	private final String resourceServerName;

	private String sourceUri;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	public TotalsUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public TotalsUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public String build() {
		assert sourceUri != null : "sourceUri required.";
		return URIUtil.format(resourceServerName, sourceUri);
	}
}
