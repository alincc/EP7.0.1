/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.linker.impl;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.elasticpath.rest.chain.BrokenChainException;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.Lists;

import org.hamcrest.Matchers;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.orders.OrderLookup;
import com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link com.elasticpath.rest.resource.orders.linker.impl.LinkToPurchaseStrategy}.
 */
public final class LinkToPurchaseStrategyTest {

	private static final String ORDER_RESOURCE = "orders";
	private static final String SCOPE = "SCOPE";
	private static final String ORDER_ID = "ORDER_ID";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final OrderLookup orderLookup = context.mock(OrderLookup.class);
	private final LinkToPurchaseStrategy linkToPurchaseStrategy = new LinkToPurchaseStrategy(ORDER_RESOURCE, orderLookup);

	/**
	 * Test link is created for valid order.
	 */
	@Test
	public void testLinkToPurchaseWithValidOrder() {

		final ResourceState<OrderEntity> orderResourceState = createOrderResourceState(ORDER_ID);

		shouldFindOrderByOrderIdWithResult(ExecutionResultFactory.createReadOK(orderResourceState));

		ResourceState<PurchaseEntity> purchaseResourceState = createPurchaseResourceState(SCOPE, ORDER_ID);

		Iterable<ResourceLink> linksToAdd = linkToPurchaseStrategy.getLinks(purchaseResourceState);

		String expectedUri = URIUtil.format(ORDER_RESOURCE, SCOPE, Base32Util.encode(ORDER_ID));
		ResourceLink expectedLink = ResourceLinkFactory.createNoRev(expectedUri,
				OrdersMediaTypes.ORDER
								.id(),
				OrdersRepresentationRels.ORDER_REL);

		assertThat("Should contain link to order.", linksToAdd, Matchers.hasItem(expectedLink));
	}

	/**
	 * Test that no link is created if no order is found for a purchase.
	 */
	@Test
	public void testLinkToPurchaseWithNotFoundOrder() {
		shouldFailToFindOrderByOrderIdWithResult(ExecutionResultFactory.createNotFound("Not found."));

		ResourceState<PurchaseEntity> purchaseResourceState = createPurchaseResourceState(SCOPE, ORDER_ID);
		Iterable<ResourceLink> linksToAdd = linkToPurchaseStrategy.getLinks(purchaseResourceState);

		assertTrue("There should be no links created.", Lists.newArrayList(linksToAdd).isEmpty());
	}

	private <T> void shouldFailToFindOrderByOrderIdWithResult(final ExecutionResult<T> result) {

		context.checking(new Expectations() {
			{
				oneOf(orderLookup).findOrderByOrderId(SCOPE, ORDER_ID);
				will(returnValue(result));
				will(throwException(new BrokenChainException(result)));
			}
		});
	}

	private <T> void shouldFindOrderByOrderIdWithResult(final ExecutionResult<T> result) {

		context.checking(new Expectations() {
			{
				oneOf(orderLookup).findOrderByOrderId(SCOPE, ORDER_ID);
				will(returnValue(result));
			}
		});
	}

	private ResourceState<OrderEntity> createOrderResourceState(final String orderId) {

		OrderEntity orderEntity = OrderEntity.builder()
													.withOrderId(orderId)
													.build();
		return ResourceState.Builder
							.create(orderEntity)
							.build();
	}

	private ResourceState<PurchaseEntity> createPurchaseResourceState(final String scope,
																		final String orderId) {

		PurchaseEntity purchaseEntity = PurchaseEntity.builder()
														.withOrderId(orderId)
														.build();
		return ResourceState.Builder
							.create(purchaseEntity)
							.withScope(scope)
							.build();
	}
}
