/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.linker.impl;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.Lists;

import org.hamcrest.Matchers;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.orders.OrderLookup;
import com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link com.elasticpath.rest.resource.orders.linker.impl.LinkToCartStrategy}.
 */
public final class LinkToCartStrategyTest {

	private static final String ORDER_RESOURCE = "orders";
	private static final String SCOPE = "SCOPE";
	private static final String CART_ID = "CART_ID";
	private static final String CART_ID_WITHOUT_ASSOCIATED_ORDER = "CART_ID_WITHOUT_ASSOCIATED_ORDER";
	private static final String ORDER_ID = "ORDER_ID";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final OrderLookup orderLookup = context.mock(OrderLookup.class);

	private LinkToCartStrategy linkToCartStrategy;

	/**
	 * Set up test environment.
	 */
	@Before
	public void setUp() {

		linkToCartStrategy = new LinkToCartStrategy(ORDER_RESOURCE, orderLookup);
	}

	/**
	 * Test link to cart with valid order.
	 */
	@Test
	public void testLinkToCartWithValidOrder() {

		OrderEntity orderEntity = OrderEntity.builder()
													.withOrderId(ORDER_ID)
													.build();
		ResourceState<OrderEntity> orderRepresentation = ResourceState.Builder
																	.create(orderEntity)
																	.build();

		shouldFindOrderByCartIdWithResult(SCOPE, CART_ID, ExecutionResultFactory.createReadOK(orderRepresentation));

		ResourceState<CartEntity> cartResourceState = createCartResourceState(SCOPE, CART_ID);

		Iterable<ResourceLink> linksToAdd = linkToCartStrategy.getLinks(cartResourceState);

		String expectedUri = URIUtil.format(ORDER_RESOURCE, SCOPE, ORDER_ID);
		ResourceLink expectedLink = ResourceLinkFactory.create(expectedUri,
				OrdersMediaTypes.ORDER
								.id(),
				OrdersRepresentationRels.ORDER_REL,
				OrdersRepresentationRels.CART_REV);

		assertThat("Should contain link to order.", linksToAdd, Matchers.hasItem(expectedLink));
	}

	/**
	 * Test link to cart with not found order.
	 */
	@Test
	public void testLinkToCartWithNotFoundOrder() {

		shouldFindOrderByCartIdWithResult(SCOPE, CART_ID_WITHOUT_ASSOCIATED_ORDER, ExecutionResultFactory.createNotFound("Not found."));

		ResourceState<CartEntity> cartResourceState = createCartResourceState(SCOPE, CART_ID_WITHOUT_ASSOCIATED_ORDER);

		Iterable<ResourceLink> linksToAdd = linkToCartStrategy.getLinks(cartResourceState);

		assertTrue("Should contain no links.", Lists.newArrayList(linksToAdd)
													.isEmpty());
	}

	private <T> void shouldFindOrderByCartIdWithResult(final String scope,
														final String cartId,
														final ExecutionResult<T> result) {

		context.checking(new Expectations() {
			{
				oneOf(orderLookup).findOrderByCartId(scope, cartId);
				will(returnValue(result));
			}
		});
	}

	private ResourceState<CartEntity> createCartResourceState(final String scope,
																final String cartId) {

		CartEntity cartEntity = CartEntity.builder()
												.withCartId(cartId)
												.build();
		return ResourceState.Builder
							.create(cartEntity)
							.withScope(scope)
							.build();
	}
}
