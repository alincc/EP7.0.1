/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.linker.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.resource.carts.lineitems.rel.LineItemRepresentationRels;
import com.elasticpath.rest.resource.carts.rel.CartRepresentationRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;

/**
 * Strategy for adding line items link to cart.
 */
@Singleton
@Named("cartToLineItemsLinkHandler")
public final class CartToLineItemsLinkHandler implements ResourceStateLinkHandler<CartEntity> {

	private final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param cartLineItemsUriBuilderFactory the cart line items URI builder factory.
	 */
	@Inject
	CartToLineItemsLinkHandler(
			@Named("cartLineItemsUriBuilderFactory")
			final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory) {

		this.cartLineItemsUriBuilderFactory = cartLineItemsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<CartEntity> representation) {

		String cartUri = representation.getSelf()
				.getUri();
		String lineItemsUri = cartLineItemsUriBuilderFactory.get()
				.setSourceUri(cartUri)
				.build();
		ResourceLink lineItemsLink = ResourceLinkFactory.create(lineItemsUri,
				CollectionsMediaTypes.LINKS.id(),
				LineItemRepresentationRels.LINE_ITEMS_REL,
				CartRepresentationRels.CART_REV);
		return Collections.singleton(lineItemsLink);
	}
}
