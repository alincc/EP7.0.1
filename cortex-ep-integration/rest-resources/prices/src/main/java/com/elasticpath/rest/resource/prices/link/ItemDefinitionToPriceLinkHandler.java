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
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.definition.prices.PricesMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.prices.ItemPriceLookup;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PricesUriBuilderFactory;

/**
 * Create a Link to a price range on an item definition representation if it has components.
 */
@Singleton
@Named("itemDefinitionToPriceLinkHandler")
public final class ItemDefinitionToPriceLinkHandler implements ResourceStateLinkHandler<ItemDefinitionEntity> {
	private final ItemPriceLookup priceLookup;
	private final PricesUriBuilderFactory pricesUriBuilderFactory;


	/**
	 * Constructor.
	 *
	 * @param priceLookup price lookup.
	 * @param pricesUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.PricesUriBuilderFactory}
	 */
	@Inject
	ItemDefinitionToPriceLinkHandler(
			@Named("itemPriceLookup")
			final ItemPriceLookup priceLookup,
			@Named("pricesUriBuilderFactory")
			final PricesUriBuilderFactory pricesUriBuilderFactory) {
		this.priceLookup = priceLookup;
		this.pricesUriBuilderFactory = pricesUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ItemDefinitionEntity> itemDefinition) {
		final Collection<ResourceLink> linksToAdd;

		String scope = itemDefinition.getScope();

		ExecutionResult<PriceRangeEntity> itemDefinitionPriceResult = priceLookup.getItemPriceRange(scope, itemDefinition.getEntity().getItemId());

		if (itemDefinitionPriceResult.isSuccessful()) {
			String priceUri = pricesUriBuilderFactory.get()
													.setSourceUri(itemDefinition.getSelf().getUri())
													.build();

			ResourceLink link = ResourceLinkFactory.create(priceUri, PricesMediaTypes.PRICE_RANGE.id(), PriceRepresentationRels.FROM_PRICE_REL,
					PriceRepresentationRels.ITEM_DEFINITION_REV);
			linksToAdd = Collections.singleton(link);
		} else {
			linksToAdd = Collections.emptyList();
		}

		return linksToAdd;
	}
}
