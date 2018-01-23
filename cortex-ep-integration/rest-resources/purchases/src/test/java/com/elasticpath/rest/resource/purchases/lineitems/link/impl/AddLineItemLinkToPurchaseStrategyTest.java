/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.link.impl;

import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.purchases.lineitems.LineItems;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests {@link com.elasticpath.rest.resource.purchases.lineitems.link.impl.AddLineItemLinkToPurchaseStrategy}.
 */
public final class AddLineItemLinkToPurchaseStrategyTest {

	private static final String PURCHASE_URI = "purchases/mobee/123456";
	private static final String LINKS_URI = URIUtil.format(PURCHASE_URI, LineItems.URI_PART);

	private final ResourceState<PurchaseEntity> representation = ResourceState.Builder
			.create(PurchaseEntity.builder().build())
			.withSelf(SelfFactory.createSelf(PURCHASE_URI, PurchasesMediaTypes.PURCHASE.id()))
			.build();

	private final AddLineItemLinkToPurchaseStrategy strategy = new AddLineItemLinkToPurchaseStrategy();


	/**
	 * Tests that the link strategy returns a link.
	 */
	@Test
	public void testGettingLinkToRepresentation() {
		Collection<ResourceLink> links = strategy.getLinks(representation);
		ResourceLink expectedLink = ResourceLinkFactory.create(LINKS_URI, CollectionsMediaTypes.LINKS.id(),
				PurchaseLineItemsResourceRels.PURCHASE_LINEITEMS_REL, PurchaseResourceRels.PURCHASE_REV);

		assertThat("The expected link should be contained within the collection of links.", links, Matchers.hasItem(expectedLink));
	}


}
