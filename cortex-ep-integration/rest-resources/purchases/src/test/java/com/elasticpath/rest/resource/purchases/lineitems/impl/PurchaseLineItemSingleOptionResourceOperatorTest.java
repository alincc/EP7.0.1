/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemOptionsLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test class for {@link com.elasticpath.rest.resource.purchases.lineitems.impl.PurchaseLineItemSingleOptionResourceOperator}.
 */
public final class PurchaseLineItemSingleOptionResourceOperatorTest {

	private static final String LINEITEMURI = "/lineitemuri";
	private static final String LINE_ITEM_ID = "lineItemId";
	private static final String PURCHASE_ID = "purchaseId";
	private static final String SCOPE = "testScope";
	private static final String PURCHASE_LINE_ITEM_URI = "/testLineItemUri";
	private static final String OPTION_ID = "optionId";
	private static final ResourceOperation READ = TestResourceOperationFactory.createRead(PURCHASE_LINE_ITEM_URI);

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private PurchaseLineItemOptionsLookup mockPurchaseLineItemOptionsLookup;
	private PurchaseLineItemSingleOptionResourceOperator command;

	@Before
	public void setUp() {
		command = new PurchaseLineItemSingleOptionResourceOperator(mockPurchaseLineItemOptionsLookup);
	}

	/**
	 * Test read purchase line item single option.
	 */
	@Test
	public void testReadPurchaseLineItemSingleOption() {
		ResourceState<PurchaseLineItemEntity> purchaseLineItemRepresentation = createPurchaseLineItem();
		final ResourceState<PurchaseLineItemOptionEntity> expected = ResourceState.Builder
				.create(PurchaseLineItemOptionEntity.builder().build())
				.build();
		context.checking(new Expectations() {
			{
				allowing(mockPurchaseLineItemOptionsLookup).findOption(SCOPE, PURCHASE_ID, LINE_ITEM_ID, OPTION_ID, LINEITEMURI);
				will(returnValue(ExecutionResultFactory.createReadOK(expected)));
			}
		});

		OperationResult result = command.processReadOption(purchaseLineItemRepresentation, OPTION_ID, READ);

		assertTrue("Operation should have been successful.", result.isSuccessful());
		assertEquals("Operation result should contain expected data.", expected, result.getResourceState());
	}

	private ResourceState<PurchaseLineItemEntity> createPurchaseLineItem() {
		Self self = SelfFactory.createSelf(LINEITEMURI, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id());

		return ResourceState.Builder
				.create(PurchaseLineItemEntity.builder()
						.withPurchaseId(PURCHASE_ID)
						.withLineItemId(LINE_ITEM_ID)
						.build())
				.withScope(SCOPE)
				.withSelf(self)
				.build();
	}
}
