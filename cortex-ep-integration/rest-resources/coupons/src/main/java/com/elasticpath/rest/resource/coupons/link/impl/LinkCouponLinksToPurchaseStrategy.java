/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.link.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Create a link to coupons on the purchase representation.
 */
@Singleton
@Named("linkCouponLinksToPurchaseStrategy")
public final class LinkCouponLinksToPurchaseStrategy implements ResourceStateLinkHandler<PurchaseEntity> {

	private final CouponsUriBuilderFactory couponsUriBuilderFactory;

	/**
	 * Constructor for injection.
	 *
	 * @param couponsUriBuilderFactory the coupons URI builder factory
	 */
	@Inject
	public LinkCouponLinksToPurchaseStrategy(
			@Named("couponsUriBuilderFactory")
			final CouponsUriBuilderFactory couponsUriBuilderFactory) {

		this.couponsUriBuilderFactory = couponsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<PurchaseEntity> resourceState) {

		String purchaseUri = ResourceStateUtil.getSelfUri(resourceState);

		String couponLinksUri = createCouponLinksUri(purchaseUri);
		ResourceLink link = ResourceLinkFactory.createNoRev(couponLinksUri, CollectionsMediaTypes.LINKS.id(), CouponsResourceRels.COUPONS_REL);

		return Collections.singletonList(link);
	}

	private String createCouponLinksUri(final String otherUri) {

		return couponsUriBuilderFactory.get()
				.setSourceUri(otherUri)
				.build();
	}
}
