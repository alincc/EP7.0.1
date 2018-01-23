/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
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
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemOptionsLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test that {@link com.elasticpath.rest.resource.purchases.lineitems.impl.PurchaseLineItemOptionValueResourceOperator} behaves as expected.
 */
public final class PurchaseLineItemOptionValueResourceOperatorTest {
	private static final String SCOPE = "mobee";
	private static final String LINEITEMURI = "/lineitemuri";
	private static final String LINE_ITEM_ID = "lineItemId";
	private static final String PURCHASE_ID = "purchaseId";
	private static final String OPTION_ID = "optionId";
	private static final String VALUE_ID = "valueId";
	private static final ResourceOperation READ = TestResourceOperationFactory.createRead(LINEITEMURI);

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final PurchaseLineItemOptionsLookup purchaseLineItemOptionsLookup = context.mock(PurchaseLineItemOptionsLookup.class);

	private PurchaseLineItemOptionValueResourceOperator command;

	@Before
	public void setUp() {
		command = new PurchaseLineItemOptionValueResourceOperator(purchaseLineItemOptionsLookup);
	}
	/**
	 * Test read purchase line item option value.
	 */
	@Test
	public void testReadPurchaseLineItemOptionValue() {
		ResourceState<PurchaseLineItemEntity> lineItemRepresentation = createPurchaseLineItem();
		final ResourceState<PurchaseLineItemOptionValueEntity> expected = ResourceState.Builder
				.create(PurchaseLineItemOptionValueEntity.builder().build())
				.build();
		context.checking(new Expectations() {
			{
				oneOf(purchaseLineItemOptionsLookup).findOptionValueForLineItem(SCOPE, PURCHASE_ID, LINE_ITEM_ID, OPTION_ID, VALUE_ID,
						LINEITEMURI);
				will(returnValue(ExecutionResultFactory.createReadOK(expected)));
			}
		});

		OperationResult result = command.processReadOptionValue(lineItemRepresentation, OPTION_ID, VALUE_ID, READ);

		assertTrue("The operation should have been successful", result.isSuccessful());
		assertEquals("The result data should have come from the lookup", expected, result.getResourceState());
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
