/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Info;
import com.elasticpath.rest.schema.uri.CouponsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * URI Builder for coupons resource.
 */
@Named("couponsUriBuilder")
public final class CouponsUriBuilderImpl implements CouponsUriBuilder {

	private final String resourceServerName;

	private String couponId;
	private String sourceUri;
	private String infoPathPart;
	private String formPathPart;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	@Inject
	public CouponsUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public CouponsUriBuilder setCouponId(final String couponId) {
		this.couponId = couponId;
		return this;
	}

	@Override
	public CouponsUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public CouponsUriBuilder setInfoUri() {
		this.infoPathPart = Info.URI_PART;
		return this;
	}
	
	@Override
	public CouponsUriBuilder setFormUri() {
		this.formPathPart = Form.URI_PART;
		return this;
	}

	@Override
	public String build() {
		assert sourceUri != null : "Source uri must be set.";
		assert !(couponId != null && infoPathPart != null && formPathPart != null) : "Only one of couponId, infoUri or formUri can be set.";

		return URIUtil.format(resourceServerName, sourceUri, couponId, infoPathPart, formPathPart);
	}
}
