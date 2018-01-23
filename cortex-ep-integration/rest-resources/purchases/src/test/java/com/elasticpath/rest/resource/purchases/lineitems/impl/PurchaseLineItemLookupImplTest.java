/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.purchases.lineitems.integration.PurchaseLineItemLookupStrategy;
import com.elasticpath.rest.resource.purchases.lineitems.transform.PurchaseLineItemTransformer;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests {@link PurchaseLineItemLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PurchaseLineItemLookupImplTest {

	private static final String THE_RESULT_SHOULD_BE_A_FAILURE = "The result should be a failure";
	private static final String THE_RESULT_SHOULD_BE_SUCCESSFUL = "The result should be successful";
	private static final String PARENT_URI = "/mock/parent/uri";
	private static final String PURCHASE_ID = "giydambq=";
	private static final String DECODED_PURCHASE_ID = Base32Util.decode(PURCHASE_ID);
	private static final String LINEITEM_ID = "nx4xi2kolrfggzsm4c66prwkry";
	private static final String PARENT_LINE_ITEM = "parent line item";
	private static final String DECODED_LINEITEM_ID = Base32Util.decode(LINEITEM_ID);
	private static final String SCOPE = "scope";
	private static final String COMPONENT_ID = "nx4xi2kolrfggzsm4c66prwkry";

	private final ResourceState<PurchaseLineItemEntity> representation = ResourceState.Builder
			.create(PurchaseLineItemEntity.builder().build())
			.build();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PurchaseLineItemTransformer mockPurchaseLineItemTransformer;
	@Mock
	private PurchaseLineItemLookupStrategy mockPurchaseLineItemLookupStrategy;

	@InjectMocks
	private PurchaseLineItemLookupImpl purchaseItemLookup;


	/**
	 * Tests successfully getting a line item.
	 */
	@Test
	public void testGettingLineItem() {
		String parentSelfUri = "";
		PurchaseLineItemEntity purchaseEntity = ResourceTypeFactory.createResourceEntity(PurchaseLineItemEntity.class);
		ExecutionResult<PurchaseLineItemEntity> lookupResult = ExecutionResultFactory.createReadOK(purchaseEntity);

		when(mockPurchaseLineItemLookupStrategy.getLineItem(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID, null))
				.thenReturn(lookupResult);
		when(mockPurchaseLineItemTransformer.transformToRepresentation(SCOPE, PURCHASE_ID, LINEITEM_ID, purchaseEntity, parentSelfUri))
				.thenReturn(representation);

		ExecutionResult<ResourceState<PurchaseLineItemEntity>> result = purchaseItemLookup.getPurchaseLineItem(SCOPE, PURCHASE_ID, LINEITEM_ID,
				parentSelfUri, null);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("The expected result should return a representation", representation, result.getData());

	}

	/**
	 * Tests successfully getting a line item when parent line item is not null.
	 */
	@Test
	public void testGettingLineItemWhenParentLineItemNotNull() {
		String parentSelfUri = "";
		PurchaseLineItemEntity purchaseEntity = ResourceTypeFactory.createResourceEntity(PurchaseLineItemEntity.class);
		String encodedParentLineItem = Base32Util.encode(PARENT_LINE_ITEM);
		ExecutionResult<PurchaseLineItemEntity> lookupResult = ExecutionResultFactory.createReadOK(purchaseEntity);

		when(mockPurchaseLineItemLookupStrategy.getLineItem(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID, PARENT_LINE_ITEM))
				.thenReturn(lookupResult);
		when(mockPurchaseLineItemTransformer.transformToRepresentation(SCOPE, PURCHASE_ID, LINEITEM_ID, purchaseEntity, parentSelfUri))
				.thenReturn(representation);

		ExecutionResult<ResourceState<PurchaseLineItemEntity>> result = purchaseItemLookup.getPurchaseLineItem(SCOPE, PURCHASE_ID, LINEITEM_ID,
				parentSelfUri, encodedParentLineItem);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("The expected result should return a representation", representation, result.getData());
	}

	/**
	 * Tests that a failure in the strategy results in an error execution result.
	 */
	@Test
	public void testGettingLineItemWhenStrategyReturnsError() {
		ExecutionResult<PurchaseLineItemEntity> lookupResult = ExecutionResultFactory.createNotFound("Line Item Not found");
		when(mockPurchaseLineItemLookupStrategy.getLineItem(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID, null))
				.thenReturn(lookupResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseItemLookup.getPurchaseLineItem(SCOPE, PURCHASE_ID, LINEITEM_ID,
				"", null);
	}

	/**
	 * Tests successfully getting a line item component.
	 */
	@Test
	public void testGettingLineItemComponent() {
		PurchaseLineItemEntity purchaseEntity = ResourceTypeFactory.createResourceEntity(PurchaseLineItemEntity.class);
		ExecutionResult<PurchaseLineItemEntity> lookupResult = ExecutionResultFactory.createReadOK(purchaseEntity);

		when(mockPurchaseLineItemLookupStrategy.getLineItem(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID, null))
				.thenReturn(lookupResult);
		when(mockPurchaseLineItemTransformer.transformToRepresentation(SCOPE, PURCHASE_ID, LINEITEM_ID, purchaseEntity, PARENT_URI))
				.thenReturn(representation);

		ExecutionResult<ResourceState<PurchaseLineItemEntity>> result = purchaseItemLookup.getPurchaseLineItem(SCOPE, PURCHASE_ID, LINEITEM_ID,
				PARENT_URI, null);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("The expected result should be the representation", representation, result.getData());
	}

	/**
	 * Tests that a failure in the strategy results in an error execution result.
	 */
	@Test
	public void testGettingLineItemComponentWhenStrategyReturnsError() {
		ExecutionResult<PurchaseLineItemEntity> lookupResult = ExecutionResultFactory.createNotFound("Line Item Not Found");

		when(mockPurchaseLineItemLookupStrategy.getLineItem(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID, null))
				.thenReturn(lookupResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseItemLookup.getPurchaseLineItem(SCOPE, PURCHASE_ID, LINEITEM_ID,
				PARENT_URI, null);
	}

	/**
	 * Tests that isLineItemBundle is true when the core strategy returns true.
	 */
	@Test
	public void testIsLineItemBundleTrue() {
		ExecutionResult<Boolean> lookupResult = ExecutionResultFactory.createReadOK(true);

		when(mockPurchaseLineItemLookupStrategy.isLineItemBundle(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID))
				.thenReturn(lookupResult);

		ExecutionResult<Boolean> result = purchaseItemLookup.isLineItemBundle(SCOPE, PURCHASE_ID, LINEITEM_ID);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertTrue("The expected result should be true", result.getData());
	}

	/**
	 * Tests that isLineItemBundle is false when the core strategy returns false.
	 */
	@Test
	public void testIsLineItemBundleFalse() {
		ExecutionResult<Boolean> lookupResult = ExecutionResultFactory.createReadOK(false);

		when(mockPurchaseLineItemLookupStrategy.isLineItemBundle(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID))
				.thenReturn(lookupResult);

		ExecutionResult<Boolean> result = purchaseItemLookup.isLineItemBundle(SCOPE, PURCHASE_ID, LINEITEM_ID);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertFalse("The expected result should be false", result.getData());
	}

	/**
	 * Tests that isLineItemBundle fails when the core strategy fails.
	 */
	@Test
	public void testIsLineItemBundleWithFailure() {
		ExecutionResult<Boolean> lookupResult = ExecutionResultFactory.createNotFound("Line Item Not Found");

		when(mockPurchaseLineItemLookupStrategy.isLineItemBundle(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID))
				.thenReturn(lookupResult);

		ExecutionResult<Boolean> result = purchaseItemLookup.isLineItemBundle(SCOPE, PURCHASE_ID, LINEITEM_ID);

		assertTrue(THE_RESULT_SHOULD_BE_A_FAILURE, result.isFailure());
		assertEquals("The resource status should be NOT_FOUND", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	/**
	 * Test for Getting component Ids for line item id.
	 */
	@Test
	public void testGetComponentIdsForLineItemId() {
		Collection<String> expectedComponentIds = Collections.singleton(COMPONENT_ID);
		ExecutionResult<Collection<String>> lookupResult = ExecutionResultFactory.createReadOK(expectedComponentIds);
		ExecutionResult<Boolean> lookupBooleanResult = ExecutionResultFactory.createReadOK(true);

		when(mockPurchaseLineItemLookupStrategy.getComponentIdsForLineItemId(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID))
				.thenReturn(lookupResult);

		when(mockPurchaseLineItemLookupStrategy.isLineItemBundle(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID))
				.thenReturn(lookupBooleanResult);

		ExecutionResult<Collection<String>> result = purchaseItemLookup.getComponentIdsForLineItemId(SCOPE, PURCHASE_ID, LINEITEM_ID);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		Collection<String> ids = result.getData();
		assertThat("The collection should contain the encoded COMPONENT_ID", ids, Matchers.contains(Base32Util.encode(COMPONENT_ID)));
	}

	/**
	 * Test for Getting component Ids for line item id.
	 */
	@Test
	public void testGetComponentIdsForLineItemIdWhenBundleNotFound() {
		ExecutionResult<Boolean> lookupBooleanResult = ExecutionResultFactory.createNotFound();

		when(mockPurchaseLineItemLookupStrategy.isLineItemBundle(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID))
				.thenReturn(lookupBooleanResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseItemLookup.getComponentIdsForLineItemId(SCOPE, PURCHASE_ID, LINEITEM_ID);
	}

	/**
	 * Test for Getting component Ids for line item id when strategy fails.
	 */
	@Test
	public void testGetComponentIdsForLineItemIdWhenStrategyFails() {
		ExecutionResult<Collection<String>> lookupResult = ExecutionResultFactory.createNotImplemented("not implemented");
		ExecutionResult<Boolean> lookupBooleanResult = ExecutionResultFactory.createReadOK(true);

		when(mockPurchaseLineItemLookupStrategy.getComponentIdsForLineItemId(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID))
				.thenReturn(lookupResult);

		when(mockPurchaseLineItemLookupStrategy.isLineItemBundle(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID))
				.thenReturn(lookupBooleanResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_IMPLEMENTED));

		purchaseItemLookup.getComponentIdsForLineItemId(SCOPE, PURCHASE_ID, LINEITEM_ID);
	}

	/**
	 * Test for Getting component Ids for non-bundle.
	 */
	@Test
	public void testGetComponentIdsForLineItemIdForNonBundle() {
		ExecutionResult<Boolean> lookupResult = ExecutionResultFactory.createReadOK(false);

		when(mockPurchaseLineItemLookupStrategy.isLineItemBundle(SCOPE, DECODED_PURCHASE_ID, DECODED_LINEITEM_ID))
				.thenReturn(lookupResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseItemLookup.getComponentIdsForLineItemId(SCOPE, PURCHASE_ID, LINEITEM_ID);
	}

	/**
	 * Test for getting line items ids for purchase when line item ids are not found.
	 */
	@Test
	public void testGetLineItemIdsForPurchaseWhenLineItemIdsNotFound() {
		ExecutionResult<Collection<String>> lookupResult = ExecutionResultFactory.createNotFound();

		when(mockPurchaseLineItemLookupStrategy.findLineItemIds(SCOPE, DECODED_PURCHASE_ID))
				.thenReturn(lookupResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseItemLookup.getLineItemIdsForPurchase(SCOPE, PURCHASE_ID);
	}

	/**
	 * Test for getting line items ids for purchase when line item ids are not found.
	 */
	@Test
	public void testGetLineItemIdsForPurchase() {
		Collection<String> expectedComponentIds = Collections.singleton(COMPONENT_ID);
		ExecutionResult<Collection<String>> lookupResult = ExecutionResultFactory.createReadOK(expectedComponentIds);

		when(mockPurchaseLineItemLookupStrategy.findLineItemIds(SCOPE, DECODED_PURCHASE_ID))
				.thenReturn(lookupResult);

		Base32Util.encodeAll(expectedComponentIds);
		ExecutionResult<Collection<String>> result = purchaseItemLookup.getLineItemIdsForPurchase(SCOPE, PURCHASE_ID);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
	}
}