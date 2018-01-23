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

import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.rel.CartRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Tests the {@link LineItemToCartLinkHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemToCartLinkHandlerTest {
	public static final String SCOPE = "scope";
	public static final String CART_ID = "cartId";
	public static final String CART_URI = "/cartUri";
	@Mock
	private CartsUriBuilderFactory cartsUriBuilderFactory;
	@InjectMocks
	private LineItemToCartLinkHandler lineItemToCartLinkHandler;
	@Mock
	private CartsUriBuilder cartsUriBuilder;
	private ResourceState<LineItemEntity> lineItem;

	@Before
	public void setUpCommonTestComponents() {
		lineItem = ResourceState.Builder.create(LineItemEntity.builder()
																.withCartId(CART_ID)
																.build())
										.withScope(SCOPE)
										.build();

		given(cartsUriBuilderFactory.get()).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setScope(SCOPE)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setCartId(CART_ID)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.build()).willReturn(CART_URI);
	}

	@Test
	public void ensureCartLinkIsCreatedCorrectly() {
		Iterable<ResourceLink> links = lineItemToCartLinkHandler.getLinks(lineItem);

		assertThat(links, hasItems(ResourceLinkFactory.createNoRev(CART_URI, CartsMediaTypes.CART.id(), CartRepresentationRels.CART_REL)));
	}
}
