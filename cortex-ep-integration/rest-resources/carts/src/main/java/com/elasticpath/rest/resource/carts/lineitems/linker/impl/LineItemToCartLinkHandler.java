/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.linker.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.rel.CartRepresentationRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Adds cart links to line items.
 */
@Singleton
@Named("lineItemToCartLinkHandler")
public final class LineItemToCartLinkHandler implements ResourceStateLinkHandler<LineItemEntity> {
	private final CartsUriBuilderFactory cartsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param cartsUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.CartsUriBuilderFactory}.
	 */
	@Inject
	LineItemToCartLinkHandler(
			@Named("cartsUriBuilderFactory")
			final CartsUriBuilderFactory cartsUriBuilderFactory) {
		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LineItemEntity> lineItem) {
		String cartUri = cartsUriBuilderFactory.get()
				.setScope(lineItem.getScope())
				.setCartId(lineItem.getEntity()
									.getCartId())
				.build();
		ResourceLink cartLink = ResourceLinkFactory.createNoRev(cartUri, CartsMediaTypes.CART.id(), CartRepresentationRels.CART_REL);
		return Collections.singleton(cartLink);
	}
}
