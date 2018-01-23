/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;

/**
 * Test class for {@link ItemIdLookupStrategyImpl}.
 */
@SuppressWarnings("unchecked")
public class ItemIdLookupStrategyImplTest {

	private static final String STORE_CODE = "some store code";
	private static final String PRODUCT_CODE = "productCode";
	private static final String DEFAULT_ID = "DEFAULT_ID";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final StoreProductRepository storeProductRepository = context.mock(StoreProductRepository.class);
	private final ItemRepository itemRepository = context.mock(ItemRepository.class);
	private final Product mockProduct = context.mock(Product.class);
	private final ItemIdLookupStrategyImpl strategy = new ItemIdLookupStrategyImpl(
			itemRepository, storeProductRepository);

	/**
	 * Tests the item for product lookup.
	 */
	@Test
	public void testItemForProductLookup() {
		mockExpectationsForQueryService(mockProduct);
		mockGetDefaultItemId(DEFAULT_ID);

		ExecutionResult<String> result = strategy.getDefaultItemIdForProduct(STORE_CODE, PRODUCT_CODE);

		assertTrue("The result should be successful", result.isSuccessful());
		assertEquals("The value should be DEFAULT_ID as expected", DEFAULT_ID, result.getData());
	}

	/**
	 * Tests the item for product lookup with product not found.
	 */
	@Test
	public void testItemForProductLookupWithProductNotFound() {
		mockExpectationsForQueryService(null);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.getDefaultItemIdForProduct(STORE_CODE, PRODUCT_CODE);
	}

	private void mockExpectationsForQueryService(final Product product) {

		context.checking(new Expectations() {
			{
				allowing(storeProductRepository).findByGuid(with(any(String.class)));
				will(returnValue(product));
			}
		});
	}

	private void mockGetDefaultItemId(final String itemId) {
		context.checking(new Expectations() {
			{
				allowing(itemRepository).getDefaultItemIdForProduct(mockProduct);
				will(returnValue(ExecutionResultFactory.createReadOK(itemId)));
			}
		});
	}
}
