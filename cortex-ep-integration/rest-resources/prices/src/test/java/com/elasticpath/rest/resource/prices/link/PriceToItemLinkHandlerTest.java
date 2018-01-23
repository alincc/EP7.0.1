/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Tests {@link com.elasticpath.rest.resource.prices.link.PriceToItemLinkHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PriceToItemLinkHandlerTest {
	private static final String ITEM_ID = "itemId";
	private static final String SCOPE = "scope";
	private static final String ITEM_URI = "/itemUri";

	@Mock
	private ItemsUriBuilderFactory itemsUriBuilderFactory;
	@InjectMocks
	private PriceToItemLinkHandler linkHandler;

	private ResourceState<ItemPriceEntity> itemPrice;

	@Before
	public void setUpCommonTestComponents() {
		ItemsUriBuilder itemsUriBuilder = mock(ItemsUriBuilder.class);
		given(itemsUriBuilderFactory.get()).willReturn(itemsUriBuilder);
		given(itemsUriBuilder.setItemId(ITEM_ID)).willReturn(itemsUriBuilder);
		given(itemsUriBuilder.setScope(SCOPE)).willReturn(itemsUriBuilder);
		given(itemsUriBuilder.build()).willReturn(ITEM_URI);

		itemPrice = ResourceState.Builder.create(ItemPriceEntity.builder()
															.withItemId(ITEM_ID)
															.build())
									.withScope(SCOPE)
									.build();
	}

	@Test
	public void ensureItemLinkIsReturned() {
		assertThat(linkHandler.getLinks(itemPrice), hasItems(ResourceLinkFactory.create(ITEM_URI, ItemsMediaTypes.ITEM.id(),
																							PriceRepresentationRels.ITEM_REL,
																							PriceRepresentationRels.PRICE_REV)));
	}
}
