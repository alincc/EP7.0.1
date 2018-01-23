/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.impl;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoWriter;
import com.elasticpath.rest.resource.orders.integration.billinginfo.BillingInfoWriterStrategy;
import com.elasticpath.rest.id.util.Base32Util;


/**
 * Tests for {@link BillingInfoWriter}.
 */
public final class BillingInfoWriterImplTest {

	private static final String DECODED_ADDRESS_ID = "ADDRESS_ID";
	private static final String DECODED_ORDER_ID = "ORDER_ID";
	private static final String ORDER_ID = Base32Util.encode(DECODED_ORDER_ID);
	private static final String SCOPE = "SCOPE";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final BillingInfoWriterStrategy billingInfoWriterStrategy = context.mock(BillingInfoWriterStrategy.class);

	private final BillingInfoWriter billingInfoWriter = new BillingInfoWriterImpl(billingInfoWriterStrategy);


	/**
	 * Test set address for order with given profile id same as subject user identifier.
	 */
	@Test
	public void testSetAddressForOrderWithGivenProfileIdSameAsSubjectUserId() {
		shouldSetBillingAddressWithResult(ExecutionResultFactory.createCreateOKWithData(true, false));
		ExecutionResult<Boolean> result = billingInfoWriter.setAddressForOrder(SCOPE, ORDER_ID, DECODED_ADDRESS_ID);
		assertTrue("This should be a successful operation.", result.isSuccessful());
	}

	private <T> void shouldSetBillingAddressWithResult(final ExecutionResult<T> result) {
		context.checking(new Expectations() {
			{
				oneOf(billingInfoWriterStrategy).setBillingAddress(SCOPE, DECODED_ADDRESS_ID, DECODED_ORDER_ID);
				will(returnValue(result));
			}
		});
	}
}
