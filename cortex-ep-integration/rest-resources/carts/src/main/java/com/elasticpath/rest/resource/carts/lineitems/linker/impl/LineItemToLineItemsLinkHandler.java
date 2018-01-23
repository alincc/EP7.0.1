/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.linker.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Adds link to list of line items for a cart to a specific line item.
 */
@Singleton
@Named("lineItemToLineItemsLinkHandler")
public final class LineItemToLineItemsLinkHandler implements ResourceStateLinkHandler<LineItemEntity> {
	private final CartsUriBuilderFactory cartsUriBuilderFactory;
	private final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param cartsUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.CartsUriBuilderFactory}.
	 * @param cartLineItemsUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory}.
	 */
	@Inject
	LineItemToLineItemsLinkHandler(
			@Named("cartsUriBuilderFactory")
			final CartsUriBuilderFactory cartsUriBuilderFactory,
			@Named("cartLineItemsUriBuilderFactory")
			final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory) {
		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
		this.cartLineItemsUriBuilderFactory = cartLineItemsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LineItemEntity> lineItem) {
		String cartUri = cartsUriBuilderFactory.get()
				.setScope(lineItem.getScope())
				.setCartId(lineItem.getEntity()
									.getCartId())
				.build();
		String lineItemsUri = cartLineItemsUriBuilderFactory.get()
				.setSourceUri(cartUri)
				.build();
		ResourceLink lineItemsLink = ElementListFactory.createListWithoutElement(lineItemsUri, CollectionsMediaTypes.LINKS.id());
		return Collections.singleton(lineItemsLink);
	}
}
