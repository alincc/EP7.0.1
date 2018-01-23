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

import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.resource.carts.lineitems.rel.LineItemRepresentationRels;
import com.elasticpath.rest.resource.carts.rel.CartRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilder;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;

/**
 * Test class for creating line items link to add to cart representation.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CartToLineItemsLinkHandlerTest {

	private static final String EXPECTED_URI = "/asd/asd";

	@Mock
	private CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;
	@Mock
	private CartLineItemsUriBuilder cartLineItemsUriBuilder;
	@InjectMocks
	private CartToLineItemsLinkHandler linkToCartStrategy;

	@Mock
	private CartEntity cartEntity;

	/**
	 * Test the links to be added to cart.
	 */
	@Test
	public void testLinkToCart() {
		shouldBuildCartLineItemLinkUri();
		ResourceState<CartEntity> representation = ResourceState.Builder.create(cartEntity)
																.withSelf(SelfFactory.createSelf(EXPECTED_URI, CartsMediaTypes.CART.id()))
																.build();
		Iterable<ResourceLink> links = linkToCartStrategy.getLinks(representation);

		ResourceLink expectedLink = ResourceLinkFactory.create(EXPECTED_URI, CollectionsMediaTypes.LINKS.id(),
				LineItemRepresentationRels.LINE_ITEMS_REL, CartRepresentationRels.CART_REV);

		assertThat("The expected link should be contained within the collection of links.", links, Matchers.hasItem(expectedLink));
	}

	private void shouldBuildCartLineItemLinkUri() {
		when(cartLineItemsUriBuilderFactory.get()).thenReturn(cartLineItemsUriBuilder);
		when(cartLineItemsUriBuilder.setSourceUri(any(String.class))).thenReturn(cartLineItemsUriBuilder);
		when(cartLineItemsUriBuilder.build()).thenReturn(EXPECTED_URI);
	}
}
