/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.orders.integration.billinginfo.BillingInfoLookupStrategy;
import com.elasticpath.rest.id.util.Base32Util;
import org.junit.rules.ExpectedException;

/**
 * Test that {@link BillingInfoLookupImpl} behaves as expected.
 */
public final class BillingInfoLookupImplTest {

	private static final String SCOPE = "SCOPE";
	private static final String DECODED_ORDER_ID = "DECODED_ORDER_ID";
	private static final String ORDER_ID = Base32Util.encode(DECODED_ORDER_ID);
	private static final String DECODED_ADDRESS_ID = "DECODED_ADDRESS_ID";
	private static final String ADDRESS_ID = Base32Util.encode(DECODED_ADDRESS_ID);

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final BillingInfoLookupStrategy billingInfoLookupStrategy = context.mock(BillingInfoLookupStrategy.class);
	private final BillingInfoLookupImpl billingInfoLookup = new BillingInfoLookupImpl(billingInfoLookupStrategy);


	/**
	 * Test the behaviour of find selected address for order.
	 */
	@Test
	public void testFindSelectedAddressForOrder() {
		shouldGetBillingAddressWithResult(ExecutionResultFactory.createReadOK(DECODED_ADDRESS_ID));

		ExecutionResult<String> result = billingInfoLookup.findAddressForOrder(SCOPE, ORDER_ID);

		assertTrue("The operation should have been successful", result.isSuccessful());
		assertEquals("The address Id should be as expected", ADDRESS_ID, result.getData());
	}

	/**
	 * Test the behaviour of find selected address when none found.
	 */
	@Test
	public void testFindSelectedAddressWhenNoneFound() {
		shouldGetBillingAddressWithResult(ExecutionResultFactory.createNotFound("Not found."));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		billingInfoLookup.findAddressForOrder(SCOPE, ORDER_ID);
	}

	private <T> void shouldGetBillingAddressWithResult(final ExecutionResult<T> result) {
		context.checking(new Expectations() {
			{
				oneOf(billingInfoLookupStrategy).getBillingAddress(SCOPE, DECODED_ORDER_ID);
				will(returnValue(result));
			}
		});
	}
}
