/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.NavigationsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Implementation of {@link NavigationsUriBuilder}.
 */
@Named("navigationsUriBuilder")
public final class NavigationsUriBuilderImpl implements NavigationsUriBuilder {

	private final String resourceServerName;

	private String scope;
	private String navigationId;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	NavigationsUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public NavigationsUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public NavigationsUriBuilder setNavigationId(final String navigationId) {
		this.navigationId = navigationId;
		return this;
	}

	@Override
	public String build() {
		assert navigationId != null : "navigationId required.";
		assert scope != null : "scope required.";

		return URIUtil.format(resourceServerName, scope, navigationId);
	}
}
