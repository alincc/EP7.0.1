/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.rates.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.rates.RatesMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.rates.integration.ItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.rel.RateRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Create a Link to rates on an Item Representation.
 */
@Singleton
@Named("linkItemStrategy")
public final class LinkItemStrategy implements ResourceStateLinkHandler<ItemEntity> {

	private final String resourceServerName;
	private final ItemRateLookupStrategy itemRateLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resource Server Name
	 * @param itemRateLookupStrategy price lookup.
	 */
	@Inject
	LinkItemStrategy(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("itemRateLookupStrategy")
			final ItemRateLookupStrategy itemRateLookupStrategy) {

		this.resourceServerName = resourceServerName;
		this.itemRateLookupStrategy = itemRateLookupStrategy;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<ItemEntity> item) {

		final Collection<ResourceLink> linksToAdd;

		String itemUri = ResourceStateUtil.getSelfUri(item);
		String uri = URIUtil.format(resourceServerName, itemUri);
		String itemId = item.getEntity().getItemId();
		ExecutionResult<Boolean> rateExists = itemRateLookupStrategy.rateExists(item.getScope(), itemId);
		if (rateExists.isSuccessful() && rateExists.getData()) {
			ResourceLink link = ResourceLinkFactory.create(uri, RatesMediaTypes.RATE.id(), RateRepresentationRels.RATE_REL,
					RateRepresentationRels.ITEM_REV);
			linksToAdd = Collections.singleton(link);
		} else {
			linksToAdd = Collections.emptyList();
		}

		return linksToAdd;
	}
}
