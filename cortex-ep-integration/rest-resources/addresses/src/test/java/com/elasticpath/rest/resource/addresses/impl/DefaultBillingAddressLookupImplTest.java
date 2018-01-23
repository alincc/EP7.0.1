/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;


import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;

import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.addresses.integration.addresses.alias.DefaultAddressLookupStrategy;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.addresses.DefaultAddressLookup;

/**
 * Test for class {@link DefaultBillingAddressLookupImpl}.
 */
public final class DefaultBillingAddressLookupImplTest {

	private static final String USER_ID = "user-id";
	private static final String DECODED_ADDRESS_ID = "address-id";
	private static final String ADDRESS_ID = Base32Util.encode(DECODED_ADDRESS_ID);
	private static final String SCOPE = "scope";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ResourceOperationContext resourceOperationContext = context.mock(ResourceOperationContext.class);

	private final DefaultAddressLookupStrategy mockLookupStrategy = context.mock(DefaultAddressLookupStrategy.class);

	private final DefaultBillingAddressLookupImpl lookup = new TestDefaultBillingAddressLookupImpl(mockLookupStrategy);

	/**
	 * Tests that {@link DefaultAddressLookup#getDefaultAddressId(String)} returns the right ID.
	 */
	@Test
	public void testGetDefaultAddress() {
		context.checking(new Expectations() {
			{
				allowing(mockLookupStrategy).findPreferredAddressId(SCOPE, USER_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(DECODED_ADDRESS_ID)));

				allowing(resourceOperationContext).getUserIdentifier();
				will(returnValue(USER_ID));
			}
		});

		ExecutionResult<String> defaultAddressResult = lookup.getDefaultAddressId(SCOPE);

		Assert.assertTrue(defaultAddressResult.isSuccessful());
		Assert.assertEquals(ADDRESS_ID, defaultAddressResult.getData());
	}

	/**
	 * Tests that {@link DefaultAddressLookup#getDefaultAddressId(String)} propagates lookup strategy failures.
	 */
	@Test
	public void testGetDefaultAddressWithFailure() {

		final ResourceStatus executionFailureStatus = ResourceStatus.NOT_FOUND;
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		context.checking(new Expectations() {
			{
				allowing(mockLookupStrategy).findPreferredAddressId(SCOPE, USER_ID);
				will(returnValue(ExecutionResultFactory.create("Test induced failure", executionFailureStatus, null)));

				allowing(resourceOperationContext).getUserIdentifier();
				will(returnValue(USER_ID));
			}
		});

		lookup.getDefaultAddressId(SCOPE);
	}

	/**
	 * A test implementation of {@link DefaultBillingAddressLookupImpl}.
	 */
	private class TestDefaultBillingAddressLookupImpl extends DefaultBillingAddressLookupImpl implements DefaultAddressLookup {

		/**
		 * Constructor.
		 *
		 * @param defaultBillingAddressLookupStrategy the default address lookup strategy
		 */
		TestDefaultBillingAddressLookupImpl(final DefaultAddressLookupStrategy defaultBillingAddressLookupStrategy) {
			super(defaultBillingAddressLookupStrategy, resourceOperationContext);
		}
	}
}
