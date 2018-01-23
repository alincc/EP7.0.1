/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.definition.prices.PricesMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.prices.CartLineItemPriceLookup;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PricesUriBuilderFactory;

/**
 * Create a Link to Price on a Cart Line Item Representation.
 */
@Singleton
@Named("lineItemToPriceLinkHandler")
public final class LineItemToPriceLinkHandler implements ResourceStateLinkHandler<LineItemEntity> {
	private final CartLineItemPriceLookup cartLineItemPriceLookup;
	private final PricesUriBuilderFactory pricesUriBuilderFactory;


	/**
	 * Constructor.
	 *
	 * @param cartLineItemPriceLookup Line Item price lookup.
	 * @param pricesUriBuilderFactory  the {@link PricesUriBuilderFactory}.
	 */
	@Inject
	LineItemToPriceLinkHandler(
			@Named("cartLineItemPriceLookup")
			final CartLineItemPriceLookup cartLineItemPriceLookup,
			@Named("pricesUriBuilderFactory")
			final PricesUriBuilderFactory pricesUriBuilderFactory) {
		this.cartLineItemPriceLookup = cartLineItemPriceLookup;
		this.pricesUriBuilderFactory = pricesUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LineItemEntity> lineItem) {
		ExecutionResult<CartLineItemPriceEntity> lineItemPriceResult = cartLineItemPriceLookup.getLineItemPrice(lineItem);

		Collection<ResourceLink> linksToAdd;
		if (lineItemPriceResult.isSuccessful()) {
			String priceUri = pricesUriBuilderFactory.get()
												.setSourceUri(lineItem.getSelf().getUri())
												.build();
			ResourceLink link = ResourceLinkFactory.create(priceUri,
														PricesMediaTypes.CART_LINE_ITEM_PRICE.id(), PriceRepresentationRels.PRICE_REL,
														PriceRepresentationRels.LINE_ITEM_REV);

			linksToAdd = Collections.singleton(link);
		} else {
			linksToAdd = Collections.emptyList();
		}

		return linksToAdd;
	}
}
