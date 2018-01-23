/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.link.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.lookups.LookupsMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.lookups.rels.LookupResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Adds a code link to item.
 */
@Singleton
@Named("addCodeLinkToItem")
public final class AddCodeLinkToItem implements ResourceStateLinkHandler<ItemEntity> {

	private final ItemLookupUriBuilderFactory itemLookupUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param itemLookupUriBuilderFactory the itemLookupUriBuilderFactory
	 */
	@Inject
	AddCodeLinkToItem(
			@Named("itemLookupUriBuilderFactory")
			final ItemLookupUriBuilderFactory itemLookupUriBuilderFactory) {

		this.itemLookupUriBuilderFactory = itemLookupUriBuilderFactory;
	}


	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ItemEntity> resourceState) {
		String otherUri = ResourceStateUtil.getSelfUri(resourceState);
		String uri = itemLookupUriBuilderFactory.get().setSourceUri(otherUri).build();
		ResourceLink resourceLink = ResourceLinkFactory.create(
				uri, LookupsMediaTypes.CODE.id(), LookupResourceRels.CODE_REL, LookupResourceRels.ITEM_REL);
		return Collections.singleton(resourceLink);
	}
}
