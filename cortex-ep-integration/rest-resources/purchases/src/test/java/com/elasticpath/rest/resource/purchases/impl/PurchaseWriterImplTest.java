/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.purchases.PurchaseWriter;
import com.elasticpath.rest.resource.purchases.integration.PurchaseWriterStrategy;
import com.elasticpath.rest.id.util.Base32Util;

/**
 * Test class for {@link PurchaseWriterImplTest}.
 */
public final class PurchaseWriterImplTest {

	private static final String PURCHASE_CORRELATION_ID = "purchase id";
	private static final String PURCHASE_ID = Base32Util.encode(PURCHASE_CORRELATION_ID);
	private static final String DECODED_ORDER_ID = "order id";
	private static final String ORDER_ID = Base32Util.encode(DECODED_ORDER_ID);
	private static final String SCOPE = "scope";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final PurchaseWriterStrategy mockPurchaseWriterStrategy = context.mock(PurchaseWriterStrategy.class);

	private final PurchaseWriter purchaseWriter = new PurchaseWriterImpl(mockPurchaseWriterStrategy);

	/**
	 * Test create purchase.
	 */
	@Test
	public void testCreatePurchase() {
		context.checking(new Expectations() {
			{
				oneOf(mockPurchaseWriterStrategy).createPurchase(SCOPE, DECODED_ORDER_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(PURCHASE_CORRELATION_ID)));
			}
		});

		ExecutionResult<String> result = purchaseWriter.createPurchase(SCOPE, ORDER_ID);

		assertTrue("Operation should be successful", result.isSuccessful());
		assertEquals("Purchase ID does not match expected value.", PURCHASE_ID, result.getData());
	}

	/**
	 * Test create purchase with error on writer.
	 */
	@Test
	public void testCreatePurchaseWithErrorOnWriter() {
		context.checking(new Expectations() {
			{
				oneOf(mockPurchaseWriterStrategy).createPurchase(SCOPE, DECODED_ORDER_ID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseWriter.createPurchase(SCOPE, ORDER_ID);
	}
}
