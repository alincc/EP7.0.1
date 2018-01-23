/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.link.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Strategy to attach a link to Discount from a {@link CartEntity}.
 */
@Singleton
@Named("linkDiscountsToCartStrategy")
public final class LinkDiscountsToCartStrategy implements ResourceStateLinkHandler<CartEntity> {

	private final DiscountsLinkCreator discountsLinkCreator;

	/**
	 * Constructor.
	 *
	 * @param discountsLinkCreator The Discounts Link creator.
	 */
	@Inject
	public LinkDiscountsToCartStrategy(
			@Named("discountsLinkCreator")
			final DiscountsLinkCreator discountsLinkCreator) {

		this.discountsLinkCreator = discountsLinkCreator;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<CartEntity> representation) {

		String otherUri = representation.getSelf()
				.getUri();
		return discountsLinkCreator.buildDiscountsLink(otherUri);

	}
}
