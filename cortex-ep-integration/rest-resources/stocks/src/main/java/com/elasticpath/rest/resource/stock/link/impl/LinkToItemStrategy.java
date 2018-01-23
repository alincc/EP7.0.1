/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.stock.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.stocks.StocksMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.stock.integration.StockLookupStrategy;
import com.elasticpath.rest.resource.stock.rel.StockResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.StockUriBuilder;

/**
 * For linking stock to items.
 */
@Singleton
@Named("linkToItemStrategy")
public class LinkToItemStrategy implements ResourceStateLinkHandler<ItemEntity> {

	private final StockLookupStrategy stockLookupStrategy;

	private final StockUriBuilder stockUriBuilder;

	/**
	 * Default constructor.
	 *
	 * @param stockLookupStrategy strategy for looking up stock
	 * @param stockUriBuilder builder for stock URIs
	 */
	@Inject
	public LinkToItemStrategy(
			@Named("stockLookupStrategy")
			final StockLookupStrategy stockLookupStrategy,
			@Named("stockUriBuilder")
			final StockUriBuilder stockUriBuilder) {

		this.stockLookupStrategy = stockLookupStrategy;
		this.stockUriBuilder = stockUriBuilder;
	}

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<ItemEntity> item) {
		String scope = item.getScope();
		String itemId = item.getEntity().getItemId();

		Collection<ResourceLink> links;
		if (isStockDisplayed(scope, itemId)) {
			String stockUri = stockUriBuilder
					.setScope(scope)
					.setItemId(itemId)
					.build();
			ResourceLink resourceLink = ResourceLinkFactory
					.create(stockUri, StocksMediaTypes.STOCK.id(), StockResourceRels.STOCK_REL, StockResourceRels.ITEM_REV);
			links = Collections.singleton(resourceLink);
		} else {
			links = Collections.emptyList();
		}
		return links;
	}

	/**
	 * Determines whether the stock link should exist for this item.
	 *
	 * @param stockScope the stock's scope
	 * @param itemId the item ID
	 * @return whether link should exist
	 */
	protected Boolean isStockDisplayed(final String stockScope, final String itemId) {
		ExecutionResult<Boolean> isStockDisplayedResult = stockLookupStrategy.isStockDisplayedForItem(stockScope, itemId);
		if (isStockDisplayedResult.isSuccessful()) {
			return isStockDisplayedResult.getData();
		} else {
			return Boolean.FALSE;
		}
	}

}
