/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.link.impl;

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

import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.resource.discounts.rel.DiscountsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Tests the {@link AddCartLinkToDiscountHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddCartLinkToDiscountHandlerTest {
	private static final String CART_ID = "cartId";
	private static final String SCOPE = "scope";
	private static final String CARTS_URI = "/cartsUri";

	@Mock
	private CartsUriBuilderFactory cartsUriBuilderFactory;
	@InjectMocks
	private AddCartLinkToDiscountHandler linkHandler;

	private ResourceState<DiscountEntity> discount;

	@Before
	public void setupCommonTestComponents() {
		CartsUriBuilder cartsUriBuilder = mock(CartsUriBuilder.class);
		given(cartsUriBuilderFactory.get()).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setCartId(CART_ID)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.setScope(SCOPE)).willReturn(cartsUriBuilder);
		given(cartsUriBuilder.build()).willReturn(CARTS_URI);

		discount = ResourceState.Builder.create(DiscountEntity.builder()
																.withCartId(CART_ID)
																.build())
										.withScope(SCOPE)
										.build();
	}

	@Test
	public void ensureCartLinkIsReturnedForCartDiscountAssociation() {
		ResourceLink expectedCartLink = ResourceLinkFactory.create(CARTS_URI, CartsMediaTypes.CART.id(),
															DiscountsResourceRels.CART_REL, DiscountsResourceRels.DISCOUNT_REV);

		assertThat(linkHandler.getLinks(discount), hasItems(expectedCartLink));
	}

	@Test
	public void ensureNoLinksAreReturnedForDiscountWithoutCartAssociation() {
		discount = ResourceState.Builder.create(DiscountEntity.builder()
																.build())
										.build();

		assertTrue("There should be no links returned when discount is not associated with cart", Iterables.isEmpty(linkHandler.getLinks(discount)));
	}
}
