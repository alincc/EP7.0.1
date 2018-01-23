/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.prices.PricesMediaTypes;
import com.elasticpath.rest.resource.prices.ItemPriceLookup;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PricesUriBuilder;
import com.elasticpath.rest.schema.uri.PricesUriBuilderFactory;

/**
 * Tests the {@link com.elasticpath.rest.resource.prices.link.ItemToPriceLinkHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemToPriceLinkHandlerTest {
	private static final String ITEM_ID = "itemId";
	private static final String ITEM_URI = "/itemUri";
	private static final String SCOPE = "scope";
	private static final String ITEM_PRICE_URI = "itemPriceUri";

	@Mock
	private ItemPriceLookup itemPriceLookup;
	@Mock
	private PricesUriBuilderFactory pricesUriBuilderFactory;
	@InjectMocks
	private ItemToPriceLinkHandler linkHandler;

	private ResourceState<ItemEntity> item;

	@Before
	public void setupCommonTestComponents() {
		PricesUriBuilder pricesUriBuilder = mock(PricesUriBuilder.class);
		given(pricesUriBuilderFactory.get()).willReturn(pricesUriBuilder);
		given(pricesUriBuilder.setSourceUri(ITEM_URI)).willReturn(pricesUriBuilder);
		given(pricesUriBuilder.build()).willReturn(ITEM_PRICE_URI);

		item = ResourceState.Builder.create(ItemEntity.builder()
															.withItemId(ITEM_ID)
															.build())
									.withSelf(SelfFactory.createSelf(ITEM_URI))
									.withScope(SCOPE)
									.build();
	}

	@Test
	public void ensureItemPriceLinkIsReturnedForItemWithPrice() {
		given(itemPriceLookup.priceExists(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(true));

		Iterable<ResourceLink> links = linkHandler.getLinks(item);

		assertThat(links, hasItems(ResourceLinkFactory.create(ITEM_PRICE_URI,
															PricesMediaTypes.ITEM_PRICE.id(),
															PriceRepresentationRels.PRICE_REL,
															PriceRepresentationRels.ITEM_REV)));
	}

	@Test
	public void ensureNoLinksAreReturnedWhenItemHasNoPrice() {
		given(itemPriceLookup.priceExists(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(false));

		assertTrue("No links should be returned when item has no price", Iterables.isEmpty(linkHandler.getLinks(item)));
	}

	@Test
	public void ensureNoLinksAreReturnecWhenItemPriceQueryFails() {
		given(itemPriceLookup.priceExists(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.<Boolean>createNotFound());

		assertTrue("No links should be returned when item price query fails", Iterables.isEmpty(linkHandler.getLinks(item)));
	}
}
