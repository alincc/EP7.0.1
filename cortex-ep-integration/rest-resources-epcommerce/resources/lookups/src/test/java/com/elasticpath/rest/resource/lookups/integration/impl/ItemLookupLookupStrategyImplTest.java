/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.integration.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsActionIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsIdentifier;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.definition.lookups.LookupsIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.id.type.StringListIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Tests for {@link ItemLookupLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemLookupLookupStrategyImplTest {

	private static final String SKU_CODE = "SKU_CODE";
	private static final String ITEM_ID = "ITEM_ID";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ProductSkuRepository productSkuRepository;
	@Mock
	private ItemRepository itemRepository;
	@InjectMocks
	public ItemLookupLookupStrategyImpl classUnderTest;
	@Mock
	private ProductSku mockProductSku;
	@Mock
	IdentifierPart mockItemId;


	@Test
	public void testGetItemIdSuccessfully() {
		when(productSkuRepository.getProductSkuWithAttributesByCode(SKU_CODE)).thenReturn(ExecutionResultFactory.createReadOK(mockProductSku));
		when(itemRepository.getItemIdForSku(mockProductSku)).thenReturn(ExecutionResultFactory.createReadOK(ITEM_ID));

		ExecutionResult<String> result = classUnderTest.getItemIdByCode(SKU_CODE);

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(ITEM_ID);
	}

	@Test
	public void testCannotFindProductSkuWithExternalItemId() {
		when(productSkuRepository.getProductSkuWithAttributesByCode(SKU_CODE)).thenReturn(ExecutionResultFactory.<ProductSku>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		classUnderTest.getItemIdByCode(SKU_CODE);
	}

	@Test
	public void testCannotFindItemForProductSku() {
		when(productSkuRepository.getProductSkuWithAttributesByCode(SKU_CODE)).thenReturn(ExecutionResultFactory.createReadOK(mockProductSku));
		when(itemRepository.getItemIdForSku(mockProductSku)).thenReturn(ExecutionResultFactory.<String>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		classUnderTest.getItemIdByCode(SKU_CODE);
	}

	@Test
	public void testGetItemSuccessfully() {
		CodeEntity expectedCodeEntity = buildExpectedCodeEntity();
		when(itemRepository.getSkuCodeForItemId(ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(SKU_CODE));

		ExecutionResult<CodeEntity> result = classUnderTest.getItemLookupByItem(ITEM_ID);

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(expectedCodeEntity);
	}

	@Test
	public void testCannotFindSkuCodeForItem() {
		when(itemRepository.getSkuCodeForItemId(ITEM_ID)).thenReturn(ExecutionResultFactory.<String>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		classUnderTest.getItemLookupByItem(ITEM_ID);
	}

	@Test
	public void testGetBatchIdForCodes() {
		IdentifierPart<List<String>> actual = classUnderTest.getBatchIdForCodes(Arrays.asList("1", "2", "3"));
		assertThat(actual.getValue(), Matchers.contains("1", "2", "3"));
	}

	@Test
	@SuppressWarnings("checkstyle:magicnumber")
	public void testGetItemIdsForBatchId() {
		when(productSkuRepository.getProductSkuWithAttributesByCode(anyString()))
				.thenReturn(ExecutionResultFactory.createReadOK(mockProductSku));
		when(itemRepository.getItemIdForProductSku(mockProductSku))
				.thenReturn(mockItemId);

		IdentifierPart batchId = StringListIdentifier.of("1", "2", "3");
		LookupsIdentifier lookupsIdentifier = LookupsIdentifier.builder()
				.withScope(StringIdentifier.of("scope"))
				.build();

		BatchItemsIdentifier batchItemsIdentifier = BatchItemsIdentifier.builder()
				.withBatchId(batchId)
				.withBatchItemsAction(BatchItemsActionIdentifier.builder().withLookups(lookupsIdentifier).build())
				.build();

		Iterable<ItemIdentifier> actual = classUnderTest.getItemIdsForBatchId(batchItemsIdentifier);

		assertEquals(3, Iterables.size(actual));
	}

	@Test
	public void testGetItemIdsForBatchIdFails() {
		when(productSkuRepository.getProductSkuWithAttributesByCode(anyString()))
				.thenReturn(ExecutionResultFactory.<ProductSku>createNotFound());

		IdentifierPart batchId = StringListIdentifier.of("1", "2", "3");
		LookupsIdentifier lookupsIdentifier = LookupsIdentifier.builder()
				.withScope(StringIdentifier.of("scope"))
				.build();

		BatchItemsIdentifier batchItemsIdentifier = BatchItemsIdentifier.builder()
				.withBatchId(batchId)
				.withBatchItemsAction(BatchItemsActionIdentifier.builder().withLookups(lookupsIdentifier).build())
				.build();

		Iterable<ItemIdentifier> actual = classUnderTest.getItemIdsForBatchId(batchItemsIdentifier);
		assertTrue(Iterables.isEmpty(actual));
	}

	private CodeEntity buildExpectedCodeEntity() {
		return CodeEntity.builder().withCode(SKU_CODE).build();
	}
}