/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Create a link to item from price.
 */
@Singleton
@Named("priceToItemLinkHandler")
public final class PriceToItemLinkHandler implements ResourceStateLinkHandler<ItemPriceEntity> {
	private final ItemsUriBuilderFactory itemsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param itemsUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory}.
	 */
	@Inject
	PriceToItemLinkHandler(
			@Named("itemsUriBuilderFactory")
			final ItemsUriBuilderFactory itemsUriBuilderFactory) {
		this.itemsUriBuilderFactory = itemsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ItemPriceEntity> itemPrice) {
		String itemUri = itemsUriBuilderFactory.get()
											.setItemId(itemPrice.getEntity().getItemId())
											.setScope(itemPrice.getScope())
											.build();
		return Collections.singleton(ResourceLinkFactory.create(itemUri, ItemsMediaTypes.ITEM.id(),
																PriceRepresentationRels.ITEM_REL,
																PriceRepresentationRels.PRICE_REV));
	}
}
