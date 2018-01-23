/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.purchases.integration.PurchaseLookupStrategy;
import com.elasticpath.rest.resource.purchases.transformer.PurchaseTransformer;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests {@link PurchaseLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PurchaseLookupImplTest {

	private static final String RESULT_SHOULD_BE_SUCCESSFUL = "Result should be successful";
	private static final String SCOPE = "rockjam";
	private static final String USER_ID = "user id";
	private static final String EXISTING_PURCHASE_ID = "DD22-DA7E-E648-BABB-DF1D5B23968F";
	private static final String EXISTING_ORDER_ID = "existing order id";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PurchaseTransformer mockPurchaseTransformer;
	@Mock
	private PurchaseLookupStrategy mockPurchaseLookupStrategy;

	@InjectMocks
	private PurchaseLookupImpl purchaseLookup;


	/**
	 * Test for existing purchase.
	 */
	@Test
	public void testFindPurchaseByIdAndScopeHappyPath() {
		String encodedExistingPurchaseId = Base32Util.encode(EXISTING_PURCHASE_ID);
		PurchaseEntity purchaseEntity = ResourceTypeFactory.createResourceEntity(PurchaseEntity.class);
		ExecutionResult<PurchaseEntity> lookupResult = ExecutionResultFactory.createReadOK(purchaseEntity);

		when(mockPurchaseLookupStrategy.getPurchase(SCOPE, EXISTING_PURCHASE_ID))
				.thenReturn(lookupResult);

		// we are only testing PurchaseLookupImpl, so don't need to return a valid representation.
		when(mockPurchaseTransformer.transformToRepresentation(SCOPE, purchaseEntity))
				.thenReturn(null);

		ExecutionResult<ResourceState<PurchaseEntity>> result = purchaseLookup.findPurchaseById(SCOPE, encodedExistingPurchaseId);
		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
	}

	/**
	 * Test FindPurchaseById when purchase result not found.
	 */
	@Test
	public void testFindPurchaseByIdWhenPurchaseResultNotFound() {
		String encodedExistingPurchaseId = Base32Util.encode(EXISTING_PURCHASE_ID);
		ExecutionResult<PurchaseEntity> lookupResult = ExecutionResultFactory.createNotFound();

		when(mockPurchaseLookupStrategy.getPurchase(SCOPE, EXISTING_PURCHASE_ID))
				.thenReturn(lookupResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLookup.findPurchaseById(SCOPE, encodedExistingPurchaseId);
	}

	/**
	 * Test FindPurchaseIds when purchase id result is Not Found.
	 */
	@Test
	public void testFindPurchaseIdsWhenPurchaseResultNotFound() {

		ExecutionResult<Collection<String>> lookupResult = ExecutionResultFactory.createNotFound();

		when(mockPurchaseLookupStrategy.getPurchaseIds(SCOPE, USER_ID))
				.thenReturn(lookupResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLookup.findPurchaseIds(SCOPE, USER_ID);
	}

	/**
	 * Test FindPurchaseIds when purchase id result is Successful.
	 */
	@Test
	public void testFindPurchaseIdsWhenPurchaseResultSuccessful() {

		Collection<String> purchaseIdList = Arrays.asList(Base32Util.encode(EXISTING_PURCHASE_ID));
		ExecutionResult<Collection<String>> lookupResult = ExecutionResultFactory.createReadOK(purchaseIdList);

		when(mockPurchaseLookupStrategy.getPurchaseIds(SCOPE, USER_ID))
				.thenReturn(lookupResult);

		ExecutionResult<Collection<String>> result = purchaseLookup.findPurchaseIds(SCOPE, USER_ID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
	}

	/**
	 * Test IsOrderPurchasable happy path.
	 */
	@Test
	public void testIsOrderPurchasable() {
		String encodedExistingOrderId = Base32Util.encode(EXISTING_ORDER_ID);
		ExecutionResult<Boolean> lookupResult = ExecutionResultFactory.createReadOK(true);

		when(mockPurchaseLookupStrategy.isOrderPurchasable(SCOPE, EXISTING_ORDER_ID))
				.thenReturn(lookupResult);

		ExecutionResult<Boolean> result = purchaseLookup.isOrderPurchasable(SCOPE, encodedExistingOrderId);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
	}
}
