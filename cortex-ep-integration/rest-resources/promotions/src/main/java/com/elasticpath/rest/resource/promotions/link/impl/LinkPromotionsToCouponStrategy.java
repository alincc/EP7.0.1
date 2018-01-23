/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.link.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Strategy to add promotion link to a coupon.
 */
@Singleton
@Named("linkPromotionsToCouponStrategy")
public final class LinkPromotionsToCouponStrategy implements ResourceStateLinkHandler<CouponEntity> {

	private final PromotionsLinkCreator promotionsLinkCreator;

	/**
	 * Constructor.
	 *
	 * @param promotionsLinkCreator the promotions link creator
	 */
	@Inject
	LinkPromotionsToCouponStrategy(
			@Named("promotionsLinkCreator")
			final PromotionsLinkCreator promotionsLinkCreator) {
		this.promotionsLinkCreator = promotionsLinkCreator;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<CouponEntity> couponRepresentation) {
		String couponUri = couponRepresentation.getSelf().getUri();
		return promotionsLinkCreator.buildAppliedPromotionsLink(couponUri);
	}
}
