/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.linker.impl;

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
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.lineitems.LineItemLookup;
import com.elasticpath.rest.resource.carts.lineitems.rel.LineItemRepresentationRels;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Tests the {@link AddActionToLineItemFormHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddActionToLineItemFormHandlerTest {
	private static final String ITEM_ID = "itemId";
	private static final String SCOPE = "scope";
	private static final String ITEM_URI = "/itemUri";
	private static final String CART_LINE_ITEM_URI = "/cartLineItemUri";

	@Mock
	private LineItemLookup lineItemLookup;
	@Mock
	private ItemsUriBuilderFactory itemsUriBuilderFactory;
	@Mock
	private CartsUriBuilderFactory cartsUriBuilderFactory;
	@InjectMocks
	private AddActionToLineItemFormHandler addActionToLineItemFormHandler;

	private ResourceState<LineItemEntity> lineitem;

	@Before
	public void setUpCommonTestComponents() {
		ItemsUriBuilder itemsUriBuilder = mock(ItemsUriBuilder.class);
		given(itemsUriBuilderFactory.get()).willReturn(itemsUriBuilder);
		given(itemsUriBuilder.setItemId(ITEM_ID)).willReturn(itemsUriBuilder);
		given(itemsUriBuilder.setScope(SCOPE)).willReturn(itemsUriBuilder);
		given(itemsUriBuilder.build()).willReturn(ITEM_URI);

		CartsUriBuilder cartsUriBuilder = mock(CartsUriBuilder.class);
		given(cartsUriBuilderFactory.get()).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setCartId(Default.URI_PART)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setScope(SCOPE)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setItemUri(ITEM_URI)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.build()).willReturn(CART_LINE_ITEM_URI);

		lineitem = ResourceState.Builder.create(LineItemEntity.builder()
																.withItemId(ITEM_ID)
																.build())
										.withScope(SCOPE)
										.build();
	}

	@Test
	public void ensureAddToCartLinkIsReturnedForPurchasableItem() {
		given(lineItemLookup.isItemPurchasable(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(true));

		Iterable<ResourceLink> links = addActionToLineItemFormHandler.getLinks(lineitem);

		assertThat(links, hasItems(ResourceLinkFactory.createUriRel(CART_LINE_ITEM_URI,
																	LineItemRepresentationRels.ADD_TO_DEFAULT_CART_ACTION_REL)));
	}

	@Test
	public void ensureNoLinksAreReturnedForNonPurchasableItem() {
		given(lineItemLookup.isItemPurchasable(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(false));

		Iterable<ResourceLink> links = addActionToLineItemFormHandler.getLinks(lineitem);

		assertTrue("There should be no links returned when item is not purchasable", Iterables.isEmpty(links));
	}

	@Test
	public void ensureNoLinksAreReturnedWhenPurchasableQueryFails() {
		given(lineItemLookup.isItemPurchasable(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.<Boolean>createNotFound());

		Iterable<ResourceLink> links = addActionToLineItemFormHandler.getLinks(lineitem);

		assertTrue("There should be no links returned when item is not purchasable", Iterables.isEmpty(links));
	}
}
