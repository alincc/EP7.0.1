/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.impl;

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
import com.elasticpath.rest.resource.slots.integration.ItemIdLookupStrategy;
import com.elasticpath.rest.id.util.Base32Util;


/**
 * Test class for {@link ItemIdLookupImpl}.
 */
public final class ItemIdLookupImplTest {

	private static final String ITEM_CONFIGURATION_ID = "itemConfigurationId";
	private static final String SCOPE = "scope";
	private static final String PRODUCT_ID = "productId";
	private static final String DECODED_PRODUCT_ID = Base32Util.decode(PRODUCT_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ItemIdLookupStrategy mockConfigurationLookupStrategy =
			context.mock(ItemIdLookupStrategy.class);
	private final ItemIdLookupImpl lookup = new ItemIdLookupImpl(mockConfigurationLookupStrategy);


	/**
	 * Tests that getting the default item for a product will succeed.
	 */
	@Test
	public void testGetDefaultItemForProduct() {

		context.checking(new Expectations() {
			{
				allowing(mockConfigurationLookupStrategy).getDefaultItemIdForProduct(SCOPE, DECODED_PRODUCT_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(ITEM_CONFIGURATION_ID)));
			}
		});

		ExecutionResult<String> result = lookup.getDefaultItemIdForProduct(SCOPE, PRODUCT_ID);

		assertTrue(result.isSuccessful());
		assertEquals("The item configuration id returned should be the same as expected item configuration id",
				ITEM_CONFIGURATION_ID, result.getData());
	}

	/**
	 * Tests that getting the default item for a product will fail.
	 */
	@Test
	public void testGetDefaultItemForProductWillFail() {

		context.checking(new Expectations() {
			{
				allowing(mockConfigurationLookupStrategy).getDefaultItemIdForProduct(SCOPE, DECODED_PRODUCT_ID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookup.getDefaultItemIdForProduct(SCOPE, PRODUCT_ID);
	}
}
