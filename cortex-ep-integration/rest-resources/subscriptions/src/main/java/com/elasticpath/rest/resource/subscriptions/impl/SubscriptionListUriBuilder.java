/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.subscriptions.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.ScopedUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * A builder for creating a list of subscriptions.
 */
@Named("subscriptionListUriBuilder")
public final class SubscriptionListUriBuilder implements ScopedUriBuilder<SubscriptionListUriBuilder> {
	private final String resourceServerName;
	private String scope;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resourceServer name
	 */
	@Inject
	SubscriptionListUriBuilder(
			// @formatter:off
			@Named("resourceServerName")
			final String resourceServerName) {
			// @formatter:on
		this.resourceServerName = resourceServerName;
	}

	@Override
	public SubscriptionListUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		return URIUtil.format(resourceServerName, scope);
	}
}
