/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link OrderPaymentMethodUriBuilder}.
 */
@Named("orderPaymentMethodUriBuilder")
public class OrderPaymentMethodUriBuilderImpl implements OrderPaymentMethodUriBuilder {
	private final OrdersUriBuilderFactory ordersUriBuilderFactory;
	private final String resourceServerName;

	private String orderId;
	private String scope;

	/**
	 * Default constructor.
	 *
	 * @param ordersUriBuilderFactory the {@link OrdersUriBuilderFactory}
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public OrderPaymentMethodUriBuilderImpl(
			@Named("ordersUriBuilderFactory")
			final OrdersUriBuilderFactory ordersUriBuilderFactory,
			@Named("resourceServerName")
			final String resourceServerName) {
		this.ordersUriBuilderFactory = ordersUriBuilderFactory;
		this.resourceServerName = resourceServerName;
	}

	@Override
	public OrderPaymentMethodUriBuilder setOrderId(final String orderId) {
		this.orderId = orderId;
		return this;
	}

	@Override
	public OrderPaymentMethodUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		assert orderId != null : "orderId required.";

		String orderUri = ordersUriBuilderFactory.get()
				.setScope(scope)
				.setOrderId(orderId)
				.build();

		return URIUtil.format(resourceServerName, orderUri);
	}
}
