/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.link.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemLookup;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Test class for {@link com.elasticpath.rest.resource.purchases.lineitems.link.impl.AddComponentsLinkToPurchaseLineItemStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class AddComponentsLinkToPurchaseLineItemStrategyTest {

	private static final String MOCK_SELF_URI = "/mock/self/uri";
	private static final String PURCHASE_LINE_ITEM_ID = "purchaseLineItemId";
	private static final String PURCHASE_ID = "purchaseId";
	private static final String SCOPE = "scope";

	@Mock
	private PurchaseLineItemLookup mockPurchaseLineItemLookup;

	@InjectMocks
	private AddComponentsLinkToPurchaseLineItemStrategy strategy;

	/**
	 * Tests that a components link is added for a bundle lineitem.
	 */
	@Test
	public void testComponentsLinkAddedForBundle() {
		when(mockPurchaseLineItemLookup.isLineItemBundle(SCOPE, PURCHASE_ID, PURCHASE_LINE_ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(true));

		Collection<ResourceLink> createdLinks = strategy.getLinks(createPurchaseLineItemRepresentation());

		String componentsUri = URIUtil.format(MOCK_SELF_URI, Components.URI_PART);
		ResourceLink expectedComponentsLink = ResourceLinkFactory.create(componentsUri, CollectionsMediaTypes.LINKS.id(),
				PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_COMPONENTS_REL,
				PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_REV);

		assertThat("There should have been a components link created", createdLinks, Matchers.contains(expectedComponentsLink));
	}

	/**
	 * Tests that no links are added for a non-bundle lineitem.
	 */
	@Test
	public void testNoLinksAddedForNonBundle() {
		when(mockPurchaseLineItemLookup.isLineItemBundle(SCOPE, PURCHASE_ID, PURCHASE_LINE_ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(false));

		Collection<ResourceLink> createdLinks = strategy.getLinks(createPurchaseLineItemRepresentation());

		assertThat("There should have been no links created", createdLinks, Matchers.empty());
	}

	private ResourceState<PurchaseLineItemEntity> createPurchaseLineItemRepresentation() {
		return ResourceState.Builder
				.create(PurchaseLineItemEntity.builder()
								.withLineItemId(PURCHASE_LINE_ITEM_ID)
								.withPurchaseId(PURCHASE_ID)
								.build())
				.withSelf(SelfFactory.createSelf(MOCK_SELF_URI, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id()))
				.withScope(SCOPE)
				.build();
	}
}
