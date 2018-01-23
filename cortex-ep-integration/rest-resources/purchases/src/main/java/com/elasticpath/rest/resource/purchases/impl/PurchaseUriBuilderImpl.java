/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Default implementation of {@link PurchaseUriBuilder}.
 */
@Named("purchaseUriBuilder")
public final class PurchaseUriBuilderImpl implements PurchaseUriBuilder {

	private final String resourceServerName;

	private String scope;
	private String purchaseId;

	/**
	 * Construct a PurchaseUriBuilderImpl.
	 *
	 * @param resourceServerName The resource server name.
	 */
	@Inject
	PurchaseUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public PurchaseUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public PurchaseUriBuilder setPurchaseId(final String encodedPurchaseId) {
		this.purchaseId = encodedPurchaseId;
		return this;
	}

	@Override
	public PurchaseUriBuilder setDecodedPurchaseId(final String decodedPurchaseId) {
		if (decodedPurchaseId != null) {
			this.purchaseId = Base32Util.encode(decodedPurchaseId);
		}
		return this;
	}
	
	@Override
	public String build() {
		assert scope != null;
		assert purchaseId != null;
		return URIUtil.format(resourceServerName, scope, purchaseId);
	}

}
