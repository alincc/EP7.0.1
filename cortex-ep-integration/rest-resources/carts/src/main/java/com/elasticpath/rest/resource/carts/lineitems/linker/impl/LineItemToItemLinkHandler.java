/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.linker.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.carts.lineitems.rel.LineItemRepresentationRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Adds item links to line items.
 */
@Singleton
@Named("lineItemToItemLinkHandler")
public final class LineItemToItemLinkHandler implements ResourceStateLinkHandler<LineItemEntity> {

	private final ItemsUriBuilderFactory itemsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param itemsUriBuilderFactory the {@link ItemsUriBuilderFactory}.
	 */
	@Inject
	LineItemToItemLinkHandler(
			@Named("itemsUriBuilderFactory")
			final ItemsUriBuilderFactory itemsUriBuilderFactory) {
		this.itemsUriBuilderFactory = itemsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LineItemEntity> lineItem) {
		String itemUri = itemsUriBuilderFactory.get()
				.setScope(lineItem.getScope())
				.setItemId(lineItem.getEntity().getItemId())
				.build();
		ResourceLink itemLink = ResourceLinkFactory.createNoRev(itemUri, ItemsMediaTypes.ITEM.id(), LineItemRepresentationRels.ITEM_REL);
		return Collections.singleton(itemLink);
	}
}
