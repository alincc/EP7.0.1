/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.linker.impl;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.carts.lineitems.LineItemLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilder;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Tests the {@link LineItemsToLineItemLinkHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemsToLineItemLinkHandlerTest {
	private static final String CART_ID = "cartId";
	private static final String SCOPE = "scope";
	private static final String CARTS_URI = "/cartsUri";
	private static final String LINE_ITEM_ID_2 = "lineItemId2";
	private static final String LINE_ITEM_ID = "lineItemId";
	private static final String CART_LINE_ITEM_URI1 = "/cartLineItemUri1";
	private static final String CART_LINE_ITEM_URI2 = "/cartLineItemUri2";

	@Mock
	private CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;
	@Mock
	private CartsUriBuilderFactory cartsUriBuilderFactory;
	@Mock
	private LineItemLookup lineItemLookup;
	@InjectMocks
	private LineItemsToLineItemLinkHandler linksHandler;

	private ResourceState<LinksEntity> linksResourceState;

	@Before
	public void setupCommonTestComponents() {
		CartsUriBuilder cartsUriBuilder = mock(CartsUriBuilder.class);
		given(cartsUriBuilderFactory.get()).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setCartId(CART_ID)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setScope(SCOPE)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.build()).willReturn(CARTS_URI);

		CartLineItemsUriBuilder cartLineItemsUriBuilder = mock(CartLineItemsUriBuilder.class);
		given(cartLineItemsUriBuilderFactory.get()).willReturn(cartLineItemsUriBuilder);
		given(cartLineItemsUriBuilder.setSourceUri(CARTS_URI)).willReturn(cartLineItemsUriBuilder);

		CartLineItemsUriBuilder cartLineItemUriBuilder1 = mock(CartLineItemsUriBuilder.class);
		given(cartLineItemsUriBuilder.setLineItemId(LINE_ITEM_ID)).willReturn(cartLineItemUriBuilder1);
		given(cartLineItemUriBuilder1.build()).willReturn(CART_LINE_ITEM_URI1);

		CartLineItemsUriBuilder cartLineItemUriBuilder2 = mock(CartLineItemsUriBuilder.class);
		given(cartLineItemsUriBuilder.setLineItemId(LINE_ITEM_ID_2)).willReturn(cartLineItemUriBuilder2);
		given(cartLineItemUriBuilder2.build()).willReturn(CART_LINE_ITEM_URI2);
		linksResourceState = ResourceState.Builder.create(LinksEntity.builder()
																			.withElementListId(CART_ID)
																			.withElementListType(CartsMediaTypes.CART.id())
																			.build())
												.withScope(SCOPE)
												.build();
	}

	@Test
	public void ensureLineItemLinksAreReturnedForCartWithLineItems() {
		given(lineItemLookup.findIdsForCart(CART_ID, SCOPE))
				.willReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Arrays.asList(LINE_ITEM_ID, LINE_ITEM_ID_2)));

		Iterable<ResourceLink> links = linksHandler.getLinks(linksResourceState);


		assertThat(links, hasItems(ElementListFactory.createElementOfList(CART_LINE_ITEM_URI1, CartsMediaTypes.LINE_ITEM.id()),
								ElementListFactory.createElementOfList(CART_LINE_ITEM_URI2, CartsMediaTypes.LINE_ITEM.id())));
	}

	@Test
	public void ensureNoLinksAreReturnedIfElementListTypeIsNotHandled() {
		linksResourceState = ResourceState.Builder.create(LinksEntity.builder()
													.withElementListType("unhandledElementListType")
													.build())
							.build();

		Iterable<ResourceLink> links = linksHandler.getLinks(linksResourceState);

		assertTrue("No links should be returned for unhandled element list type", Iterables.isEmpty(links));
	}

	@Test
	public void ensureNoLinksAreReturnedWhenCartHasNoLineItems() {
		given(lineItemLookup.findIdsForCart(CART_ID, SCOPE))
				.willReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.<String>emptyList()));

		Iterable<ResourceLink> links = linksHandler.getLinks(linksResourceState);

		assertTrue("No links should be returned when cart has no line items", Iterables.isEmpty(links));
	}
}
