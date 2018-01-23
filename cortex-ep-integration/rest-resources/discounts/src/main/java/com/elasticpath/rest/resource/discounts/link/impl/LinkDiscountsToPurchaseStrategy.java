/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.link.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Strategy to attach a link to Discount from a {@link PurchaseEntity}.
 */
@Singleton
@Named("linkDiscountsToPurchaseStrategy")
public final class LinkDiscountsToPurchaseStrategy implements ResourceStateLinkHandler<PurchaseEntity> {

	private final DiscountsLinkCreator discountsLinkCreator;

	/**
	 * Constructor.
	 *
	 * @param discountsLinkCreator The Discounts Link creator.
	 */
	@Inject
	public LinkDiscountsToPurchaseStrategy(
			@Named("discountsLinkCreator")
			final DiscountsLinkCreator discountsLinkCreator) {

		this.discountsLinkCreator = discountsLinkCreator;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<PurchaseEntity> representation) {

		if (representation.getEntity()
				.getPurchaseId() == null) {
			return Collections.emptyList();
		}
		String otherUri = representation.getSelf()
				.getUri();
		return discountsLinkCreator.buildDiscountsLink(otherUri);
	}
}
