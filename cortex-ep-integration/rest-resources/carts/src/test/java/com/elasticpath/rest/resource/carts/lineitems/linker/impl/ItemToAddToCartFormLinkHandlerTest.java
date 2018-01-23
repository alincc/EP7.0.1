/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.linker.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.carts.lineitems.rel.LineItemRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Test class for creating addToCartForm link to add to items.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ItemToAddToCartFormLinkHandlerTest {

	private static final String EXPECTED_URI = "/asd/asd";

	@Mock
	private CartsUriBuilderFactory cartsUriBuilderFactory;
	@Mock
	private CartsUriBuilder cartUriBuilder;
	@InjectMocks
	private ItemToAddToCartFormLinkHandler linkToItemStrategy;

	@Mock
	private ItemEntity itemEntity;

	/**
	 * Test links to be added to item.
	 */
	@Test
	public void testLinkToItem() {
		shouldBuildCartAddFormUri();
		ResourceState<ItemEntity> representation = ResourceState.Builder.create(itemEntity)
																.withSelf(SelfFactory.createSelf(EXPECTED_URI, ItemsMediaTypes.ITEM.id()))
																.build();
		Iterable<ResourceLink> links = linkToItemStrategy.getLinks(representation);

		ResourceLink expectedLink = ResourceLinkFactory.createNoRev(EXPECTED_URI, CartsMediaTypes.LINE_ITEM.id(),
				LineItemRepresentationRels.ADD_TO_CART_FORM_REL);

		assertThat("The expected link should be contained within the collection of links.", links, Matchers.hasItem(expectedLink));
	}

	private void shouldBuildCartAddFormUri() {
		when(cartsUriBuilderFactory.get()).thenReturn(cartUriBuilder);
		when(cartUriBuilder.setFormUri(any(String.class))).thenReturn(cartUriBuilder);
		when(cartUriBuilder.build()).thenReturn(EXPECTED_URI);
	}
}
