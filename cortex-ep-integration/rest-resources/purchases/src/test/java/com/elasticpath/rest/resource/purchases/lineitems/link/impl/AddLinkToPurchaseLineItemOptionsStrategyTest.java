/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.link.impl;

import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemOptionsLookup;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link com.elasticpath.rest.resource.purchases.lineitems.link.impl.AddLinkToPurchaseLineItemOptionsStrategy}.
 */
public final class AddLinkToPurchaseLineItemOptionsStrategyTest {

	private static final String SCOPE = "scope";
	private static final String OPTION_ID = "optionId";
	private static final String LINE_ITEM_ID = "lineItemId";
	private static final String PURCHASE_ID = "purchaseId";
	private static final String LINEITEMURI = "/lineitemuri";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final PurchaseLineItemOptionsLookup mockPurchaseLineItemOptionsLookup = context.mock(PurchaseLineItemOptionsLookup.class);
	private final AddLinkToPurchaseLineItemOptionsStrategy linksStrategy =
			new AddLinkToPurchaseLineItemOptionsStrategy(mockPurchaseLineItemOptionsLookup);

	/**
	 * Test create links.
	 */
	@Test
	public void testCreateLinks() {
		ResourceState<PurchaseLineItemEntity> purchaseLineItem = createPurchaseLineItemRepresentation();

		context.checking(new Expectations() {
			{
				allowing(mockPurchaseLineItemOptionsLookup).findOptionIdsForLineItem(SCOPE, PURCHASE_ID, LINE_ITEM_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(Collections.singleton(OPTION_ID))));
			}
		});

		Collection<ResourceLink> links = linksStrategy.getLinks(purchaseLineItem);

		String expectedUri = URIUtil.format(LINEITEMURI, Options.URI_PART);
		ResourceLink expectedLink = ResourceLinkFactory.create(expectedUri, CollectionsMediaTypes.LINKS.id(),
				PurchaseLineItemsResourceRels.OPTIONS_REL, PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_REV);

		assertThat("Link strategy not creating all expected links.", links, Matchers.contains(expectedLink));
	}

	/**
	 * Test create links with empty option IDs.
	 */
	@Test
	public void testCreateLinksWithEmptyOptionIds() {
		ResourceState<PurchaseLineItemEntity> purchaseLineItem = createPurchaseLineItemRepresentation();

		context.checking(new Expectations() {
			{
				allowing(mockPurchaseLineItemOptionsLookup).findOptionIdsForLineItem(SCOPE, PURCHASE_ID, LINE_ITEM_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(Collections.emptyList())));
			}
		});

		Collection<ResourceLink> links = linksStrategy.getLinks(purchaseLineItem);
		assertThat("Link strategy should not create any links.", links, Matchers.empty());
	}

	/**
	 * Test create links with error when finding option ids.
	 */
	@Test
	public void testCreateLinksWithErrorWhenFindingOptionIds() {
		ResourceState<PurchaseLineItemEntity> purchaseLineItem = createPurchaseLineItemRepresentation();

		context.checking(new Expectations() {
			{
				allowing(mockPurchaseLineItemOptionsLookup).findOptionIdsForLineItem(SCOPE, PURCHASE_ID, LINE_ITEM_ID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});

		Collection<ResourceLink> links = linksStrategy.getLinks(purchaseLineItem);
		assertThat("Link strategy should not create any links.", links, Matchers.empty());
	}

	private ResourceState<PurchaseLineItemEntity> createPurchaseLineItemRepresentation() {
		return ResourceState.Builder
				.create(PurchaseLineItemEntity.builder()
						.withPurchaseId(PURCHASE_ID)
						.withLineItemId(LINE_ITEM_ID)
						.build())
				.withSelf(SelfFactory.createSelf(LINEITEMURI, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id()))
				.withScope(SCOPE)
				.build();
	}
}
