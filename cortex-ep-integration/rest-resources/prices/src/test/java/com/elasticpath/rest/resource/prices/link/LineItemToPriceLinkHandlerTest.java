/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
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
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.definition.prices.PricesMediaTypes;
import com.elasticpath.rest.resource.prices.CartLineItemPriceLookup;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PricesUriBuilder;
import com.elasticpath.rest.schema.uri.PricesUriBuilderFactory;

/**
 * Tests the {@link com.elasticpath.rest.resource.prices.link.LineItemToPriceLinkHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemToPriceLinkHandlerTest {
	private static final String LINE_ITEM_URI = "/lineItemUri";
	private static final String LINE_ITEM_PRICE_URI = "/lineItemPriceUri";

	@Mock
	private CartLineItemPriceLookup cartLineItemPriceLookup;
	@Mock
	private PricesUriBuilderFactory pricesUriBuilderFactory;
	@InjectMocks
	private LineItemToPriceLinkHandler linkHandler;

	private ResourceState<LineItemEntity> lineItem;

	@Before
	public void setupCommonTestComponents() {
		PricesUriBuilder pricesUriBuilder = mock(PricesUriBuilder.class);
		given(pricesUriBuilderFactory.get()).willReturn(pricesUriBuilder);
		given(pricesUriBuilder.setSourceUri(LINE_ITEM_URI)).willReturn(pricesUriBuilder);
		given(pricesUriBuilder.build()).willReturn(LINE_ITEM_PRICE_URI);

		lineItem = ResourceState.Builder.create(LineItemEntity.builder()
																.build())
										.withSelf(SelfFactory.createSelf(LINE_ITEM_URI))
										.build();
	}

	@Test
	public void ensureLineItemPriceLinkIsReturnedForLineItemWithPrice() {
		given(cartLineItemPriceLookup.getLineItemPrice(lineItem)).willReturn(ExecutionResultFactory.<CartLineItemPriceEntity>createReadOK(null));

		Iterable<ResourceLink> links = linkHandler.getLinks(lineItem);

		assertThat(links, hasItems(ResourceLinkFactory.create(LINE_ITEM_PRICE_URI,
															PricesMediaTypes.CART_LINE_ITEM_PRICE.id(),
															PriceRepresentationRels.PRICE_REL,
															PriceRepresentationRels.LINE_ITEM_REV)));
	}

	@Test
	public void ensureNoLinksAreReturnedWhenLineItemPriceQueryFails() {
		given(cartLineItemPriceLookup.getLineItemPrice(lineItem)).willReturn(ExecutionResultFactory.<CartLineItemPriceEntity>createNotFound());

		Iterable<ResourceLink> links = linkHandler.getLinks(lineItem);

		assertTrue("There should be no links returned when line item has no price or price fails to be looked up", Iterables.isEmpty(links));
	}
}
