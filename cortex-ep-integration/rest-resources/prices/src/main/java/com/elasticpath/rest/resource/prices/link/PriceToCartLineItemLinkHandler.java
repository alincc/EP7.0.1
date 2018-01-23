/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Create a link to a cart line item from price.
 */

@Singleton
@Named("priceToCartLineItemLinkHandler")
public final class PriceToCartLineItemLinkHandler implements ResourceStateLinkHandler<CartLineItemPriceEntity> {
	private final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;
	private final CartsUriBuilderFactory cartsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param cartLineItemsUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory}
	 * @param cartsUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.CartsUriBuilderFactory}
	 */
	@Inject
	PriceToCartLineItemLinkHandler(
			@Named("cartLineItemsUriBuilderFactory")
			final CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory,
			@Named("cartsUriBuilderFactory")
			final CartsUriBuilderFactory cartsUriBuilderFactory) {
		this.cartLineItemsUriBuilderFactory = cartLineItemsUriBuilderFactory;
		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<CartLineItemPriceEntity> cartLineItemPrice) {
		CartLineItemPriceEntity cartLineItemEntity = cartLineItemPrice.getEntity();
		String cartUri = cartsUriBuilderFactory.get()
											.setCartId(cartLineItemEntity.getCartId())
											.setScope(cartLineItemPrice.getScope())
											.build();
		String lineItemUri = cartLineItemsUriBuilderFactory.get()
													.setSourceUri(cartUri)
													.setLineItemId(cartLineItemEntity.getLineItemId())
													.build();
		return Collections.singleton(ResourceLinkFactory.create(lineItemUri, CartsMediaTypes.LINE_ITEM.id(),
																PriceRepresentationRels.LINE_ITEM_REL,
																PriceRepresentationRels.PRICE_REV));
	}
}
