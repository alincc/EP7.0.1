/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.billinginfo.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import org.junit.rules.ExpectedException;

/**
 * Test that {@link BillingInfoLookupStrategyImpl} behaves as expected.
 */
public class BillingInfoLookupStrategyImplTest {

	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";
	private static final String STORE_CODE = "STORE_CODE";
	private static final String ADDRESS_GUID = "ADDRESS_GUID";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final CartOrderRepository mockCartOrderRepository = context.mock(CartOrderRepository.class);

	private final BillingInfoLookupStrategyImpl billingInfoLookupStrategy = new BillingInfoLookupStrategyImpl(mockCartOrderRepository);

	/**
	 * Test the behaviour of get preferred billing address.
	 */
	@Test
	public void testGetPreferredBillingAddress() {
		final CartOrder expectedCartOrder = context.mock(CartOrder.class);
		final ExecutionResult<CartOrder> cartOrderResult = ExecutionResultFactory.createReadOK(expectedCartOrder);

		final Address expectedAddress = context.mock(Address.class);
		final ExecutionResult<Address> addressResult = ExecutionResultFactory.createReadOK(expectedAddress);

		context.checking(new Expectations() {
			{
				oneOf(mockCartOrderRepository).findByGuid(STORE_CODE, CART_ORDER_GUID);
				will(returnValue(cartOrderResult));

				oneOf(mockCartOrderRepository).getBillingAddress(expectedCartOrder);
				will(returnValue(addressResult));

				allowing(expectedAddress).getGuid();
				will(returnValue(ADDRESS_GUID));
			}
		});

		ExecutionResult<String> result = billingInfoLookupStrategy.getBillingAddress(STORE_CODE, CART_ORDER_GUID);
		assertTrue("The operation should have been successful", result.isSuccessful());
		assertEquals("The result should be the address guid", ADDRESS_GUID, result.getData());
	}

	/**
	 * Test the behaviour of get preferred billing address when cart order not found.
	 */
	@Test
	public void testGetPreferredBillingAddressWhenCartOrderNotFound() {
		final ExecutionResult<CartOrder> cartOrderResult = ExecutionResultFactory.createNotFound("not found");
		context.checking(new Expectations() {
			{
				oneOf(mockCartOrderRepository).findByGuid(STORE_CODE, CART_ORDER_GUID);
				will(returnValue(cartOrderResult));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		billingInfoLookupStrategy.getBillingAddress(STORE_CODE, CART_ORDER_GUID);
	}

	/**
	 * Test the behaviour of get preferred billing address when no address found.
	 */
	@Test
	public void testGetPreferredBillingAddressWhenNoAddressFound() {
		final CartOrder expectedCartOrder = context.mock(CartOrder.class);
		final ExecutionResult<CartOrder> cartOrderResult = ExecutionResultFactory.createReadOK(expectedCartOrder);

		final ExecutionResult<Address> addressResult = ExecutionResultFactory.createNotFound("not found");
		context.checking(new Expectations() {
			{
				oneOf(mockCartOrderRepository).findByGuid(STORE_CODE, CART_ORDER_GUID);
				will(returnValue(cartOrderResult));

				oneOf(mockCartOrderRepository).getBillingAddress(expectedCartOrder);
				will(returnValue(addressResult));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		billingInfoLookupStrategy.getBillingAddress(STORE_CODE, CART_ORDER_GUID);
	}

}
