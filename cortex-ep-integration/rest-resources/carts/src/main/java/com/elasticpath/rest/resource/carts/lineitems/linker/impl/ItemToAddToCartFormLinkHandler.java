/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.linker.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.carts.lineitems.rel.LineItemRepresentationRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Strategy to assign addToCart form link to items.
 */
@Singleton
@Named("itemToAddToCartFormLinkHandler")
public final class ItemToAddToCartFormLinkHandler implements ResourceStateLinkHandler<ItemEntity> {

	private final CartsUriBuilderFactory cartsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param cartsUriBuilderFactory the carts URI builder factory.
	 */
	@Inject
	ItemToAddToCartFormLinkHandler(
			@Named("cartsUriBuilderFactory")
			final CartsUriBuilderFactory cartsUriBuilderFactory) {

		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ItemEntity> representation) {

		String addToCartFormUri = cartsUriBuilderFactory.get()
				.setFormUri(representation.getSelf()
						.getUri())
				.build();
		ResourceLink addToCartFormLink = ResourceLinkFactory.createNoRev(addToCartFormUri,
				CartsMediaTypes.LINE_ITEM.id(),
				LineItemRepresentationRels.ADD_TO_CART_FORM_REL);
		return Collections.singleton(addToCartFormLink);
	}
}
