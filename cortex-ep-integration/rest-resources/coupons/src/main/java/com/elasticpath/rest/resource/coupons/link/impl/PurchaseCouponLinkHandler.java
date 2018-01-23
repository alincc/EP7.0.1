/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.link.impl;

import static com.elasticpath.rest.definition.coupons.CouponsMediaTypes.COUPON;
import static com.elasticpath.rest.id.util.Base32Util.decode;
import static com.elasticpath.rest.id.util.Base32Util.encode;
import static com.elasticpath.rest.schema.ResourceLinkFactory.createNoRev;
import static com.elasticpath.rest.rel.ListElementRels.ELEMENT;
import static com.google.common.collect.Iterables.transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.coupons.constant.CouponsConstants;
import com.elasticpath.rest.resource.coupons.integration.PurchaseCouponsLookupStrategy;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;

/**
 * Link Handler.
 */
@Singleton
@Named("purchaseCouponLinkHandler")
public class PurchaseCouponLinkHandler implements ResourceStateLinkHandler<LinksEntity> {

	@Inject
	@Named("couponsUriBuilderFactory")
	private CouponsUriBuilderFactory couponsUriBuilderFactory;

	@Inject
	@Named("purchaseCouponsLookupStrategy")
	private PurchaseCouponsLookupStrategy purchaseCouponsLookupStrategy;

	@Inject
	@Named("purchaseUriBuilderFactory")
	private PurchaseUriBuilderFactory purchaseUriBuilderFactory;

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LinksEntity> resourceState) {
		LinksEntity linksEntity = resourceState.getEntity();

		if (!CouponsConstants.PURCHASES_FOR_COUPONS_LIST
				.equals(linksEntity.getName())) {
			return ImmutableList.of();
		}

		String purchaseId = linksEntity
				.getElementListId();
		String scope = resourceState.getScope();
		String purchaseUri = purchaseUriBuilderFactory.get()
				.setPurchaseId(purchaseId)
				.setScope(scope)
				.build();

		return ImmutableList.<ResourceLink>builder()
				.addAll(createCouponLinks(
						scope,
						purchaseId,
						purchaseUri)
				)
				.build();
	}

	private Iterable<? extends ResourceLink> createCouponLinks(
			final String scope,
			final String purchaseId,
			final String purchaseUri) {

		Iterable<String> couponIds = purchaseCouponsLookupStrategy.getCouponsForPurchase(scope, decode(purchaseId));

		return transform(couponIds, couponId -> createCouponLink(purchaseUri, couponId));
	}

	private ResourceLink createCouponLink(final String sourceUri, final String couponId) {
		return createNoRev(
				couponsUriBuilderFactory.get()
						.setSourceUri(sourceUri)
						.setCouponId(encode(couponId))
						.build(),
				COUPON.id(),
				ELEMENT
		);
	}
}
