/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.linker.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.carts.lineitems.LineItemLookup;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Adds link to list of line items for a cart to a specific line item.
 */
@Singleton
@Named("lineItemsToLineItemLinkHandler")
public final class LineItemsToLineItemLinkHandler implements ResourceStateLinkHandler<LinksEntity> {
	private final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;
	private final CartsUriBuilderFactory cartsUriBuilderFactory;
	private final LineItemLookup lineItemLookup;

	/**
	 * Constructor.
	 *
	 * @param cartLineItemsUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory}
	 * @param cartsUriBuilderFactory         the {@link com.elasticpath.rest.schema.uri.CartsUriBuilderFactory}
	 * @param lineItemLookup                 the {@link com.elasticpath.rest.resource.carts.lineitems.LineItemLookup}
	 */
	@Inject
	LineItemsToLineItemLinkHandler(
			@Named("cartLineItemsUriBuilderFactory")
			final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory,
			@Named("cartsUriBuilderFactory")
			final CartsUriBuilderFactory cartsUriBuilderFactory,
			@Named("lineItemLookup")
			final LineItemLookup lineItemLookup) {
		this.cartLineItemsUriBuilderFactory = cartLineItemsUriBuilderFactory;
		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
		this.lineItemLookup = lineItemLookup;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LinksEntity> linksEntity) {
		ArrayList<ResourceLink> lineItemLinks = new ArrayList<>();

		if (CartsMediaTypes.CART.id()
								.equals(linksEntity.getEntity()
												.getElementListType())) {
			String cartId = linksEntity.getEntity()
									.getElementListId();
			String scope = linksEntity.getScope();
			ExecutionResult<Collection<String>> lineItemIdsResult = lineItemLookup.findIdsForCart(cartId, scope);

			String cartUri = cartsUriBuilderFactory.get()
												.setCartId(cartId)
												.setScope(scope)
												.build();

			for (String lineItemId : lineItemIdsResult.getData()) {
				String uri = cartLineItemsUriBuilderFactory.get()
														.setSourceUri(cartUri)
														.setLineItemId(lineItemId)
														.build();
				ResourceLink link = ElementListFactory.createElementOfList(uri, CartsMediaTypes.LINE_ITEM.id());
				lineItemLinks.add(link);
			}
		}

		return lineItemLinks;
	}
}
