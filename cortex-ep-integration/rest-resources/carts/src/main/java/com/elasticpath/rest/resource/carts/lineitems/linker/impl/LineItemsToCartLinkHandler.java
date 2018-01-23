/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.linker.impl;

import static com.elasticpath.rest.definition.carts.CartsMediaTypes.CART;
import static com.elasticpath.rest.resource.carts.lineitems.rel.LineItemRepresentationRels.LINE_ITEMS_REV;
import static com.elasticpath.rest.resource.carts.rel.CartRepresentationRels.CART_REL;
import static com.elasticpath.rest.schema.ResourceLinkFactory.create;
import static com.google.common.collect.Lists.newArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Adds a link to LinkEntity objects that reference a CartEntity.
 */
@Singleton
@Named("lineItemsToCartLinkHandler")
public class LineItemsToCartLinkHandler implements ResourceStateLinkHandler<LinksEntity> {

	private final CartsUriBuilderFactory cartsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param cartsUriBuilderFactory carts uri builder factory
	 */
	@Inject
	LineItemsToCartLinkHandler(
			@Named("cartsUriBuilderFactory") final CartsUriBuilderFactory cartsUriBuilderFactory) {
		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LinksEntity> representation) {

		if (!CART.id()
				.equals(representation.getEntity()
						.getElementListType())) {
			return newArrayList();
		}

		String cartId = representation.getEntity()
				.getElementListId();

		ResourceLink cartLink = create(
				cartsUriBuilderFactory.get()
						.setCartId(cartId)
						.setScope(representation.getScope())
						.build(),
				CART.id(),
				CART_REL,
				LINE_ITEMS_REV
		);

		return newArrayList(cartLink);
	}
}
