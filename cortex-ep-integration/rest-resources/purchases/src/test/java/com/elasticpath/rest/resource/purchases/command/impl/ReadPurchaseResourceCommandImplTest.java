/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.command.impl;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.purchases.PurchaseLookup;
import com.elasticpath.rest.resource.purchases.command.ReadPurchaseResourceCommand;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Tests {@link ReadPurchaseResourceCommandImpl}.
 */
public final class ReadPurchaseResourceCommandImplTest {

	private static final String EXISTING_ORDER_ID = "555";
	private static final String SCOPE = "rockjam";
	private static final String ERROR_MESSAGE = "error message";
	private static final String EXISTING_PURCHASE_ID = "21";
	private static final String NON_EXISTING_PURCHASE_ID = "99999";
	private static final String ORDER_ID = EXISTING_ORDER_ID;

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final PurchaseLookup mockPurchaseLookup = context.mock(PurchaseLookup.class);


	/**
	 * Tests happy path.
	 */
	@Test
	public void testHappyPath() {
		ResourceState<PurchaseEntity> purchase = ResourceState.Builder
				.create(PurchaseEntity.builder()
						.withOrderId(ORDER_ID)
						.build())
				.withScope(SCOPE)
				.build();

		final ExecutionResult<ResourceState<PurchaseEntity>> lookupResult = ExecutionResultFactory.createReadOK(purchase);
		context.checking(new Expectations() {
			{
				oneOf(mockPurchaseLookup).findPurchaseById(SCOPE, EXISTING_PURCHASE_ID);
				will(returnValue(lookupResult));
			}
		});

		ReadPurchaseResourceCommand command = createCommand(SCOPE, EXISTING_PURCHASE_ID);
		ExecutionResult<ResourceState<PurchaseEntity>> result = command.execute();
		Assert.assertEquals(ResourceStatus.READ_OK, result.getResourceStatus());
		Assert.assertSame(purchase, result.getData());
	}

	/**
	 * Tests the case of a not found purchase.
	 */
	@Test
	public void testNotFound() {
		final ExecutionResult<ResourceState<PurchaseEntity>> lookupResult = ExecutionResultFactory.createNotFound(ERROR_MESSAGE);
		context.checking(new Expectations() {
			{
				oneOf(mockPurchaseLookup).findPurchaseById(SCOPE, NON_EXISTING_PURCHASE_ID);
				will(returnValue(lookupResult));
			}
		});

		ReadPurchaseResourceCommand command = createCommand(SCOPE, NON_EXISTING_PURCHASE_ID);
		ExecutionResult<ResourceState<PurchaseEntity>> result = command.execute();
		Assert.assertEquals(ResourceStatus.NOT_FOUND, result.getResourceStatus());
		Assert.assertNull(result.getData());
		Assert.assertEquals(ERROR_MESSAGE, result.getErrorMessage());
	}

	private ReadPurchaseResourceCommand createCommand(final String scope, final String purchaseId) {

		ReadPurchaseResourceCommandImpl command = new ReadPurchaseResourceCommandImpl(mockPurchaseLookup);
		ReadPurchaseResourceCommand.Builder builder = new ReadPurchaseResourceCommandImpl.BuilderImpl(command);

		return builder.setPurchaseId(purchaseId)
				.setScope(scope)
				.build();
	}
}
