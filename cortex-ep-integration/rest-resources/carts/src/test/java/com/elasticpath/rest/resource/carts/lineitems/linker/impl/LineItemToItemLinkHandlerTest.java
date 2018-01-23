/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.linker.impl;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.carts.lineitems.rel.LineItemRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Tests the {@link LineItemToItemLinkHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemToItemLinkHandlerTest {

	public static final String SCOPE = "scope";
	public static final String ITEM_ID = "itemId";
	public static final String ITEM_URI = "/itemUri";
	@Mock
	private ItemsUriBuilderFactory itemsUriBuilderFactory;
	@InjectMocks
	private LineItemToItemLinkHandler lineItemToItemLinkHandler;
	@Mock
	private ItemsUriBuilder itemsUriBuilder;
	private ResourceState<LineItemEntity> lineItem;

	@Before
	public void setUpCommonTestComponents() {
		lineItem = ResourceState.Builder.create(LineItemEntity.builder()
																.withItemId(ITEM_ID)
																.build())
										.withScope(SCOPE)
										.build();

		given(itemsUriBuilderFactory.get()).willReturn(itemsUriBuilder);
		given(itemsUriBuilder.setScope(SCOPE)).willReturn(itemsUriBuilder);
		given(itemsUriBuilder.setItemId(ITEM_ID)).willReturn(itemsUriBuilder);
		given(itemsUriBuilder.build()).willReturn(ITEM_URI);
	}

	@Test
	public void ensureItemLinkIsCreatedCorrectly() {
		Iterable<ResourceLink> links = lineItemToItemLinkHandler.getLinks(lineItem);

		assertThat(links, hasItems(ResourceLinkFactory.createNoRev(ITEM_URI, ItemsMediaTypes.ITEM.id(), LineItemRepresentationRels.ITEM_REL)));
	}
}
