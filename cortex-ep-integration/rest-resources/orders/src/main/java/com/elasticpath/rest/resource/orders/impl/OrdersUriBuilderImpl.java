/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.OrdersUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Creates order URIs.
 */
@Named("ordersUriBuilder")
public final class OrdersUriBuilderImpl implements OrdersUriBuilder {

	private final String resourceServerName;

	private String scope;
	private String orderId;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resource Server name.
	 */
	@Inject
	OrdersUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public OrdersUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public OrdersUriBuilder setOrderId(final String orderId) {
		this.orderId = orderId;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		assert orderId != null : "orderId required.";
		return URIUtil.format(resourceServerName, scope, orderId);
	}
}
