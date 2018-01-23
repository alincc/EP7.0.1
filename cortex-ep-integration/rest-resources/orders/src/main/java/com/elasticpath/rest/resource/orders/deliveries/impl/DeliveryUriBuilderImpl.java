/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
/*
 *
 */
package com.elasticpath.rest.resource.orders.deliveries.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.resource.orders.deliveries.Deliveries;
import com.elasticpath.rest.schema.uri.DeliveryUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds the URI of an order's delivery.
 */
@Named("deliveryUriBuilder")
public final class DeliveryUriBuilderImpl implements DeliveryUriBuilder {

	private final String resourceServerName;

	private String scope;
	private String orderId;
	private String deliveryId;


	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public DeliveryUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public DeliveryUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public DeliveryUriBuilder setOrderId(final String orderId) {
		this.orderId = orderId;
		return this;
	}

	@Override
	public DeliveryUriBuilder setDeliveryId(final String deliveryId) {
		this.deliveryId = deliveryId;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required";
		assert orderId != null : "orderId required";
		assert deliveryId != null : "deliveryId required";
		return URIUtil.format(resourceServerName, scope, orderId, Deliveries.URI_PART, deliveryId);
	}
}
