/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.PurchaseListUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Creates purchase list Uri.
 */
@Named("purchaseListUriBuilder")
public final class PurchaseListUriBuilderImpl implements PurchaseListUriBuilder {

	private final String resourceServerName;

	private String scope;


	/**
	 * Construct an PurchaseListUriBuilder.
	 *
	 * @param resourceServerName The resourceServer name.
	 */
	@Inject
	PurchaseListUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public PurchaseListUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		return URIUtil.format(resourceServerName, scope);
	}
}
