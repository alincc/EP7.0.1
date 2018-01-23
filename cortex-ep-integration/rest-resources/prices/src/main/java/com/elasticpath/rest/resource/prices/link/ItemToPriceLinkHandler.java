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
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.prices.PricesMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.prices.ItemPriceLookup;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PricesUriBuilderFactory;

/**
 * Create a Link to Prices on an Item Representation.
 */
@Singleton
@Named("itemToPriceLinkHandler")
public final class ItemToPriceLinkHandler implements ResourceStateLinkHandler<ItemEntity> {

	private final ItemPriceLookup priceLookup;
	private final PricesUriBuilderFactory pricesUriBuilderFactory;


	/**
	 * Constructor.
	 *
	 * @param priceLookup price lookup.
	 * @param pricesUriBuilderFactory the {@link PricesUriBuilderFactory}
	 */
	@Inject
	ItemToPriceLinkHandler(
			@Named("itemPriceLookup")
			final ItemPriceLookup priceLookup,
			@Named("pricesUriBuilderFactory")
			final PricesUriBuilderFactory pricesUriBuilderFactory) {
		this.priceLookup = priceLookup;
		this.pricesUriBuilderFactory = pricesUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ItemEntity> item) {
		final Collection<ResourceLink> linksToAdd;


		ExecutionResult<Boolean> priceExists = priceLookup.priceExists(item.getScope(), item.getEntity().getItemId());
		if (priceExists.isSuccessful() && priceExists.getData()) {
			String priceUri = pricesUriBuilderFactory.get()
													.setSourceUri(item.getSelf().getUri())
													.build();

			ResourceLink link = ResourceLinkFactory.create(priceUri, PricesMediaTypes.ITEM_PRICE.id(), PriceRepresentationRels.PRICE_REL,
					PriceRepresentationRels.ITEM_REV);
			linksToAdd = Collections.singleton(link);
		} else {
			linksToAdd = Collections.emptyList();
		}

		return linksToAdd;
	}
}
