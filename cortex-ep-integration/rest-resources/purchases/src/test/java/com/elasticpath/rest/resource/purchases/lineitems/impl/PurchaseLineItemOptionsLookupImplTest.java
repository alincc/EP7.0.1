/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
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
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.purchases.lineitems.integration.PurchaseLineItemOptionsLookupStrategy;
import com.elasticpath.rest.resource.purchases.lineitems.transform.PurchaseLineItemOptionTransformer;
import com.elasticpath.rest.resource.purchases.lineitems.transform.PurchaseLineItemOptionValueTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Tests the {@link PurchaseLineItemOptionsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PurchaseLineItemOptionsLookupImplTest {

	private static final String TEST_LINEITEM_URI = "/lineitemuri";
	private static final String SCOPE = "scope";
	private static final String TEST_LINE_ITEM_ID = "testLineItemId";
	private static final String TEST_PURCHASE_ID = "testPurchaseId";
	private static final String ENCODED_TEST_PURCHASE_ID = Base32Util.encode(TEST_PURCHASE_ID);
	private static final String ENCODED_TEST_LINE_ITEM_ID = Base32Util.encode(TEST_LINE_ITEM_ID);
	private static final String TEST_OPTION_ID1 = "testOptionId1";
	private static final String TEST_OPTION_ID2 = "testOptionId2";
	private static final String ENCODED_TEST_OPTION_ID1 = Base32Util.encode(TEST_OPTION_ID1);
	private static final String ENCODED_TEST_OPTION_ID2 = Base32Util.encode(TEST_OPTION_ID2);
	private static final String SELECTED_VALUE_CODE = "selectedValueCode";
	private static final String OPTION_CODE = "optionCode";
	private static final String DISPLAY_NAME = "displayName";
	private static final String NAME = "name";
	private static final String TEST_VALUE_ID = "HD";
	private static final String ENCODED_TEST_VALUE_ID = Base32Util.encode(TEST_VALUE_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PurchaseLineItemOptionsLookupStrategy mockPurchaseLineItemOptionsLookupStrategy;
	@Mock
	private PurchaseLineItemOptionTransformer mockPurchaseLineItemOptionTransformer;
	@Mock
	private PurchaseLineItemOptionValueTransformer mockPurchaseLineItemOptionValueTransformer;

	@InjectMocks
	private PurchaseLineItemOptionsLookupImpl purchaseLineItemOptionsLookup;


	/**
	 * Test find option ids for line item.
	 */
	@Test
	public void testFindOptionIdsForLineItem() {
		Collection<String> expectedOptionIds = Arrays.asList(ENCODED_TEST_OPTION_ID1, ENCODED_TEST_OPTION_ID2);
		Collection<String> originalOptionIds = Arrays.asList(TEST_OPTION_ID1, TEST_OPTION_ID2);
		ExecutionResult<Collection<String>> lookupResult = ExecutionResultFactory.createReadOK(originalOptionIds);

		when(mockPurchaseLineItemOptionsLookupStrategy.findOptionIds(SCOPE, TEST_PURCHASE_ID, TEST_LINE_ITEM_ID))
				.thenReturn(lookupResult);

		ExecutionResult<Collection<String>> result = purchaseLineItemOptionsLookup.findOptionIdsForLineItem(SCOPE,
				ENCODED_TEST_PURCHASE_ID, ENCODED_TEST_LINE_ITEM_ID);
		assertTrue("The option ids returned should be equal", CollectionUtil.areSame(expectedOptionIds, result.getData()));
	}

	/**
	 * Test find option ids for line item with error.
	 */
	@Test
	public void testFindOptionIdsForLineItemWithError() {
		ExecutionResult<Collection<String>> lookupResult = ExecutionResultFactory.createNotFound();

		when(mockPurchaseLineItemOptionsLookupStrategy.findOptionIds(SCOPE, TEST_PURCHASE_ID, TEST_LINE_ITEM_ID))
				.thenThrow(new BrokenChainException(lookupResult));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemOptionsLookup.findOptionIdsForLineItem(SCOPE,
				ENCODED_TEST_PURCHASE_ID, ENCODED_TEST_LINE_ITEM_ID);
	}

	/**
	 * Test find option.
	 */
	@Test
	public void testFindOption() {
		PurchaseLineItemOptionEntity purchaseLineItemOptionEntity = createPurchaseLineItemOptionDto();
		ResourceState<PurchaseLineItemOptionEntity> representation = ResourceState.Builder
				.create(PurchaseLineItemOptionEntity.builder().build())
				.build();
		ExecutionResult<PurchaseLineItemOptionEntity> lookupResult = ExecutionResultFactory.createReadOK(purchaseLineItemOptionEntity);

		when(mockPurchaseLineItemOptionsLookupStrategy.findOption(SCOPE, TEST_PURCHASE_ID, TEST_LINE_ITEM_ID, TEST_OPTION_ID1))
				.thenReturn(lookupResult);
		when(mockPurchaseLineItemOptionTransformer.transformToRepresentation(purchaseLineItemOptionEntity, TEST_LINEITEM_URI))
				.thenReturn(representation);

		ExecutionResult<ResourceState<PurchaseLineItemOptionEntity>> result = purchaseLineItemOptionsLookup.findOption(SCOPE,
				ENCODED_TEST_PURCHASE_ID, ENCODED_TEST_LINE_ITEM_ID, ENCODED_TEST_OPTION_ID1, TEST_LINEITEM_URI);

		assertTrue("Operation should be successful.", result.isSuccessful());
		assertEquals("The representation returned should be the one from the transformer", representation, result.getData());

	}

	/**
	 * Test find option when option is not found.
	 */
	@Test
	public void testFindOptionWhenOptionIsNotFound() {
		ExecutionResult<PurchaseLineItemOptionEntity> lookupResult = ExecutionResultFactory.createNotFound();
		when(mockPurchaseLineItemOptionsLookupStrategy.findOption(SCOPE, TEST_PURCHASE_ID, TEST_LINE_ITEM_ID, TEST_OPTION_ID1))
				.thenReturn(lookupResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemOptionsLookup.findOption(SCOPE,
				ENCODED_TEST_PURCHASE_ID, ENCODED_TEST_LINE_ITEM_ID, ENCODED_TEST_OPTION_ID1, TEST_LINEITEM_URI);
	}

	/**
	 * Test find option value for line item.
	 */
	@Test
	public void testFindOptionValueForLineItem() {
		PurchaseLineItemOptionValueEntity purchaseValueDto = createPurchaseLineItemOptionValueDto();
		ResourceState<PurchaseLineItemOptionValueEntity> representation = ResourceState.Builder
				.create(PurchaseLineItemOptionValueEntity.builder().build())
				.build();
		ExecutionResult<PurchaseLineItemOptionValueEntity> lookupResult = ExecutionResultFactory.createReadOK(purchaseValueDto);

		when(mockPurchaseLineItemOptionsLookupStrategy.findOptionValue(SCOPE, TEST_PURCHASE_ID, TEST_LINE_ITEM_ID, TEST_OPTION_ID1,
				TEST_VALUE_ID))
				.thenReturn(lookupResult);
		when(mockPurchaseLineItemOptionValueTransformer.transformToRepresentation(TEST_LINEITEM_URI, ENCODED_TEST_OPTION_ID1, ENCODED_TEST_VALUE_ID,
				purchaseValueDto))
				.thenReturn(representation);

		ExecutionResult<ResourceState<PurchaseLineItemOptionValueEntity>> result = purchaseLineItemOptionsLookup.findOptionValueForLineItem(SCOPE,
				ENCODED_TEST_PURCHASE_ID, ENCODED_TEST_LINE_ITEM_ID, ENCODED_TEST_OPTION_ID1, ENCODED_TEST_VALUE_ID, TEST_LINEITEM_URI);

		assertTrue("Operation should have been successful", result.isSuccessful());
		assertEquals("The representation returned should be the one from the transformer", representation, result.getData());
	}

	/**
	 * Test find option value for line item when the strategy returns an error.
	 */
	@Test
	public void testFindOptionValueForLineItemWithError() {
		ExecutionResult<PurchaseLineItemOptionValueEntity> lookupResult = ExecutionResultFactory.createNotFound("not found");

		when(mockPurchaseLineItemOptionsLookupStrategy.findOptionValue(SCOPE, TEST_PURCHASE_ID, TEST_LINE_ITEM_ID, TEST_OPTION_ID1,
				TEST_VALUE_ID))
				.thenReturn(lookupResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemOptionsLookup.findOptionValueForLineItem(SCOPE,
				ENCODED_TEST_PURCHASE_ID, ENCODED_TEST_LINE_ITEM_ID, ENCODED_TEST_OPTION_ID1, ENCODED_TEST_VALUE_ID, TEST_LINEITEM_URI);
	}

	private PurchaseLineItemOptionEntity createPurchaseLineItemOptionDto() {
		return PurchaseLineItemOptionEntity.builder()
				.withName(NAME)
				.withDisplayName(DISPLAY_NAME)
				.withOptionId(OPTION_CODE)
				.withSelectedValueId(SELECTED_VALUE_CODE)
				.build();
	}

	private PurchaseLineItemOptionValueEntity createPurchaseLineItemOptionValueDto() {
		return PurchaseLineItemOptionValueEntity.builder()
				.withName(NAME)
				.withDisplayName(DISPLAY_NAME)
				.build();
	}
}
