/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.BDDMockito.given;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.resource.prices.integration.ItemPriceLookupStrategy;

/**
 * Tests the {@link ItemPriceLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemPriceLookupImplTest {
	public static final String ITEM_ID = "itemId";
	public static final String SCOPE = "scope";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private ItemPriceLookupStrategy itemPriceLookupStrategy;
	@InjectMocks
	private ItemPriceLookupImpl itemPriceLookup;
	@Mock
	private ItemPriceEntity itemPriceEntity;
	@Mock
	private PriceRangeEntity priceRangeEntity;

	@Test
	public void ensureTrueReturnedIfPriceExists() {
		given(itemPriceLookupStrategy.priceExists(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(true));

		assertExecutionResult(itemPriceLookup.priceExists(SCOPE, ITEM_ID))
				.data(true);
	}

	@Test
	public void ensureFalseReturnedIfPriceDoesNotExist() {
		given(itemPriceLookupStrategy.priceExists(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(false));

		assertExecutionResult(itemPriceLookup.priceExists(SCOPE, ITEM_ID))
				.data(false);
	}

	@Test
	public void ensureErrorReturnedWhenPriceExistsQueryFails() {
		given(itemPriceLookupStrategy.priceExists(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.<Boolean>createNotFound());

		assertExecutionResult(itemPriceLookup.priceExists(SCOPE, ITEM_ID))
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureItemPriceCanBeRetrieved() {
		given(itemPriceLookupStrategy.getItemPrice(SCOPE, ITEM_ID))
				.willReturn(ExecutionResultFactory.createReadOK(itemPriceEntity));

		assertExecutionResult(itemPriceLookup.getItemPrice(SCOPE, ITEM_ID)).data(itemPriceEntity);
	}

	@Test
	public void ensureErrorReturnedWhenItemPriceQueryFails() {
		given(itemPriceLookupStrategy.getItemPrice(SCOPE, ITEM_ID))
				.willReturn(ExecutionResultFactory.<ItemPriceEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		assertExecutionResult(itemPriceLookup.getItemPrice(SCOPE, ITEM_ID)).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureItemPriceRangeCanBeRetrieved() {
		given(itemPriceLookupStrategy.getItemPriceRange(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(priceRangeEntity));

		assertExecutionResult(itemPriceLookup.getItemPriceRange(SCOPE, ITEM_ID)).data(priceRangeEntity);
	}

	@Test
	public void ensureErrorReturnedWhenPriceRangeQueryFails() {
		given(itemPriceLookupStrategy.getItemPriceRange(SCOPE, ITEM_ID))
				.willReturn(ExecutionResultFactory.<PriceRangeEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		assertExecutionResult(itemPriceLookup.getItemPriceRange(SCOPE, ITEM_ID)).resourceStatus(ResourceStatus.NOT_FOUND);
	}
}
