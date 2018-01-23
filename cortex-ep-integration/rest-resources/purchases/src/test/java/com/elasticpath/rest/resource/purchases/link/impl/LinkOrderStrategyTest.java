/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.link.impl;

import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.purchases.PurchasesResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test linking of orders to purchase.
 */
public final class LinkOrderStrategyTest {

	private static final String PURCHASE_RESOURCE_NAME = "purchases";
	private static final String VALID_ORDER_URI = "/order/rockjam/123";


	private final PurchasesResourceLinkFactory purchaseLinkFactory = new PurchasesResourceLinkFactory();
	private final LinkOrderStrategy linkOrderStrategy = new LinkOrderStrategy(PURCHASE_RESOURCE_NAME, purchaseLinkFactory);


	/**
	 * Test add purchase link to order without existing purchase.
	 */
	@Test
	public void testAddPurchaseLinkToOrderWithoutExistingPurchase() {
		Collection<ResourceLink> resourceLinks = linkOrderStrategy.getLinks(createValidOrderRepresentation());
		assertThat("Only one link should be returned. Links: " + resourceLinks, resourceLinks, Matchers.hasSize(1));
		assertThat("The expected link should be contained within the collection of links.",
				resourceLinks, Matchers.hasItem(createExpectedPurchaseFormLink()));
	}

	private ResourceState<OrderEntity> createValidOrderRepresentation() {
		return ResourceState.Builder
				.create(OrderEntity.builder().build())
				.withSelf(SelfFactory.createSelf(VALID_ORDER_URI))
				.build();
	}

	private ResourceLink createExpectedPurchaseFormLink() {
		return purchaseLinkFactory.createPurchaseFormResourceLink(PURCHASE_RESOURCE_NAME, VALID_ORDER_URI);
	}
}
