/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertResourceInfo.assertResourceInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.hamcrest.Matchers;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.Operation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationContextFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.lineitems.LineItems;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemLookup;
import com.elasticpath.rest.resource.purchases.lineitems.command.ReadPurchaseLineItemsCommand;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for ReadPurchaseLineItemsCommand.
 */
public final class ReadPurchaseLineItemsCommandImplTest {

	private static final String SCOPE = "scope";
	private static final String LINE_ITEM_ID = "lineItem_id";
	private static final String PURCHASE_ID = "purchase_id";
	private static final String PURCHASES = "purchases";
	private static final String PURCHASE_URI = URIUtil.format(PURCHASES, SCOPE, PURCHASE_ID);
	private static final String LINE_ITEMS_SELF_URI = URIUtil.format(PURCHASE_URI, LineItems.URI_PART);

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private PurchaseLineItemLookup mockPurchaseLineItemLookup;


	/**
	 * Test read purchase line items.
	 */
	@Test
	public void testReadPurchaseLineItems() {

		context.checking(new Expectations() {
			{
				allowing(mockPurchaseLineItemLookup).getLineItemIdsForPurchase(SCOPE, PURCHASE_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(Arrays.asList(LINE_ITEM_ID))));
			}
		});

		ReadPurchaseLineItemsCommand command = createReadPurchaseLineItemsCommand();
		ExecutionResult<ResourceState<LinksEntity>> result = command.execute();

		assertTrue(result.isSuccessful());
		ResourceState<LinksEntity> lineItemLinksRepresentation = result.getData();

		String expectedLineItemUri = URIUtil.format(LINE_ITEMS_SELF_URI, LINE_ITEM_ID);
		ResourceLink expectedLineItemLink = ElementListFactory.createElementOfList(expectedLineItemUri, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id());

		ResourceLink expectedPurchaseLink = ResourceLinkFactory.create(PURCHASE_URI, PurchasesMediaTypes.PURCHASE.id(),
				PurchaseResourceRels.PURCHASE_REL, PurchaseLineItemsResourceRels.PURCHASE_LINEITEMS_REV);

		Self expectedSelf = SelfFactory.createSelf(LINE_ITEMS_SELF_URI);

		assertThat(lineItemLinksRepresentation.getLinks(), Matchers.contains(expectedLineItemLink, expectedPurchaseLink));
		assertEquals(lineItemLinksRepresentation.getSelf(), expectedSelf);
		assertResourceInfo(lineItemLinksRepresentation.getResourceInfo())
			.maxAge(PurchaseResourceConstants.MAX_AGE);
	}

	/**
	 * Test read purchase line item with line items lookup error.
	 */
	@Test
	public void testReadPurchaseLineItemWithLineItemsLookupError() {
		context.checking(new Expectations() {
			{
				allowing(mockPurchaseLineItemLookup).getLineItemIdsForPurchase(SCOPE, PURCHASE_ID);
				will(returnValue(ExecutionResultFactory.createNotFound("not found")));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		ReadPurchaseLineItemsCommand command = createReadPurchaseLineItemsCommand();
		command.execute();
	}

	private ReadPurchaseLineItemsCommand createReadPurchaseLineItemsCommand() {
		ReadPurchaseLineItemsCommandImpl command = new ReadPurchaseLineItemsCommandImpl(PURCHASES,
				TestResourceOperationContextFactory.create(Operation.READ, LINE_ITEMS_SELF_URI), mockPurchaseLineItemLookup);
		ReadPurchaseLineItemsCommand.Builder builder = new ReadPurchaseLineItemsCommandImpl.BuilderImpl(command);

		builder.setPurchaseId(PURCHASE_ID)
				.setScope(SCOPE);

		return builder.build();
	}
}
