/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.link.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Create a link to coupon info on the order representation.
 */
@Singleton
@Named("linkCouponInfoToOrderStrategy")
public final class LinkCouponInfoToOrderStrategy implements ResourceStateLinkHandler<OrderEntity> {

	private final CouponsUriBuilderFactory couponsUriBuilderFactory;

	/**
	 * Constructor for injection.
	 *
	 * @param couponsUriBuilderFactory the coupons URI builder factory
	 */
	@Inject
	public LinkCouponInfoToOrderStrategy(
			@Named("couponsUriBuilderFactory")
			final CouponsUriBuilderFactory couponsUriBuilderFactory) {

		this.couponsUriBuilderFactory = couponsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<OrderEntity> resourceState) {

		String orderUri = ResourceStateUtil.getSelfUri(resourceState);

		String couponInfoUri = createCouponInfoUri(orderUri);
		ResourceLink couponInfoLink = ResourceLinkFactory.create(couponInfoUri, ControlsMediaTypes.INFO.id(),
				CouponsResourceRels.COUPONINFO_REL, CouponsResourceRels.ORDER_REV);

		return Collections.singletonList(couponInfoLink);
	}

	/**
	 * Creates a coupon info URI from the given order URI.
	 *
	 * @param orderUri the order URI
	 * @return the coupon info URI
	 */
	private String createCouponInfoUri(final String orderUri) {

		return couponsUriBuilderFactory.get()
				.setSourceUri(orderUri)
				.setInfoUri()
				.build();
	}
}
