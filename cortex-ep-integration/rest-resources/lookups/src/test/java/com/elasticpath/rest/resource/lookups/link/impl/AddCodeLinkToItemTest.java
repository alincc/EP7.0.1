/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.link.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.lookups.LookupsMediaTypes;
import com.elasticpath.rest.resource.lookups.rels.LookupResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilder;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilderFactory;

/**
 * Tests {@link AddCodeLinkToItem}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class AddCodeLinkToItemTest  {

	private static final String LOOKUP_URI = "/asd/ads/asd";
	private static final String ITEM_URI = "/im/an/item";
	@Mock
	private ResourceState<ItemEntity> itemState;
	@Mock
	private Self self;
	@Mock
	private ItemLookupUriBuilderFactory itemLookupUriBuilderFactory;
	@Mock
	private ItemLookupUriBuilder itemLookupUriBuilder;
	@InjectMocks
	private AddCodeLinkToItem addCodeLinkToItem;

	@Test
	public void getLinks() {
		when(itemState.getSelf()).thenReturn(self);
		when(self.getUri()).thenReturn(ITEM_URI);
		when(itemLookupUriBuilderFactory.get()).thenReturn(itemLookupUriBuilder);
		when(itemLookupUriBuilder.setSourceUri(ITEM_URI)).thenReturn(itemLookupUriBuilder);
		when(itemLookupUriBuilder.build()).thenReturn(LOOKUP_URI);
		ResourceLink expectedLink = ResourceLinkFactory.create(
				LOOKUP_URI, LookupsMediaTypes.CODE.id(), LookupResourceRels.CODE_REL, LookupResourceRels.ITEM_REL);

		Iterable<ResourceLink> links = addCodeLinkToItem.getLinks(itemState);

		assertThat(links, contains(expectedLink));
	}
}
