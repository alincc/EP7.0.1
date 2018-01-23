/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.link.impl;

import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.purchases.addresses.BillingAddress;
import com.elasticpath.rest.resource.purchases.addresses.rel.BillingAddressResourceRels;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests AddBillingAddressLinkToPurchaseStrategy.
 */
public class AddBillingAddressLinkToPurchaseStrategyTest {
	private static final String PURCHASE_URI = "purchases/mobee/123456";
	private static final String BILLING_ADDRESS_URI = URIUtil.format(PURCHASE_URI, BillingAddress.URI_PART);

	private final ResourceState<PurchaseEntity> representation = ResourceState.Builder
			.create(PurchaseEntity.builder().build())
			.withSelf(SelfFactory.createSelf(PURCHASE_URI))
			.build();

	private final AddBillingAddressLinkToPurchaseStrategy strategy = new AddBillingAddressLinkToPurchaseStrategy();

	/**
	 * Tests that the link strategy returns a link.
	 */
	@Test
	public void testGettingLinkToRepresentation() {
		Collection<ResourceLink> links = strategy.getLinks(representation);
		ResourceLink expectedLink = ResourceLinkFactory.create(BILLING_ADDRESS_URI, AddressesMediaTypes.ADDRESS.id(),
				BillingAddressResourceRels.BILLING_ADDRESS_REL, PurchaseResourceRels.PURCHASE_REV);

		assertThat("The expected link should be contained within the collection of links.", links, Matchers.hasItem(expectedLink));
	}

}
