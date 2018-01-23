/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.billinginfo.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.elasticpath.rest.ResourceStatus;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.orders.integration.billinginfo.BillingInfoWriterStrategy;
import org.junit.rules.ExpectedException;

/**
 * Test that {@link BillingInfoLookupStrategyImpl} behaves as expected.
 */
public class BillingInfoWriterStrategyImplTest {

	private static final String BILLING_ADDRESS_GUID = "BILLING_ADDRESS_GUID";
	private static final String ALTERNATE_BILLING_ADDRESS_GUID = "ALTERNATE_BILLING_ADDRESS_GUID";
	private static final String STORE_CODE = "STORE_CODE";
	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";
	private static final String INVALID_CART_ORDER_GUID = "INVALID_CART_ORDER_GUID";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final CartOrderRepository cartOrderRepository = context.mock(CartOrderRepository.class);
	private final BillingInfoWriterStrategy billingInfoWriterStrategy = new BillingInfoWriterStrategyImpl(cartOrderRepository);


	/**
	 * Test setting billing address on a non-existent cart order.
	 */
	@Test
	public void testSetBillingAddressOnNonExistentCartOrder() {
		shouldFindByGuidWithResult(STORE_CODE, INVALID_CART_ORDER_GUID, ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		billingInfoWriterStrategy.setBillingAddress(STORE_CODE, BILLING_ADDRESS_GUID, INVALID_CART_ORDER_GUID);
	}

	/**
	 * Test setting billing address on a cart order which causes a failed save cart order.
	 */
	@Test
	public void testSetBillingAddressOnCartOrderWhichCausesSaveFailure() {
		final CartOrder cartOrder = createMockCartOrder(BILLING_ADDRESS_GUID);

		shouldFindByGuidWithResult(STORE_CODE, CART_ORDER_GUID, ExecutionResultFactory.createCreateOKWithData(cartOrder, false));
		shouldSaveCartOrderWithResult(cartOrder, ExecutionResultFactory.createServerError("error on save"));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		billingInfoWriterStrategy.setBillingAddress(STORE_CODE, BILLING_ADDRESS_GUID, CART_ORDER_GUID);
	}

	/**
	 * Test the behaviour of setting billing address for cart orders that don't have an address.
	 */
	@Test
	public void testGetPreferredBillingAddressCartOrderWithNoAddress() {
		final CartOrder cartOrder = createMockCartOrder(null);

		shouldFindByGuidWithResult(STORE_CODE, CART_ORDER_GUID, ExecutionResultFactory.createCreateOKWithData(cartOrder, false));
		shouldSaveCartOrderWithResult(cartOrder, ExecutionResultFactory.createUpdateOK());

		ExecutionResult<Boolean> result = billingInfoWriterStrategy.setBillingAddress(STORE_CODE, BILLING_ADDRESS_GUID, CART_ORDER_GUID);

		assertTrue("The operation should have been successful", result.isSuccessful());
		assertFalse("Expected false to mean there was not a previously set address", result.getData());
	}

	/**
	 * Test the behaviour of setting billing address for cart orders that already have an address.
	 */
	@Test
	public void testGetPreferredBillingAddressCartOrderWithExistingAddress() {
		final CartOrder cartOrder = createMockCartOrder(BILLING_ADDRESS_GUID);

		shouldFindByGuidWithResult(STORE_CODE, CART_ORDER_GUID, ExecutionResultFactory.createCreateOKWithData(cartOrder, false));
		shouldSaveCartOrderWithResult(cartOrder, ExecutionResultFactory.createUpdateOK());

		ExecutionResult<Boolean> result = billingInfoWriterStrategy.setBillingAddress(STORE_CODE, ALTERNATE_BILLING_ADDRESS_GUID, CART_ORDER_GUID);

		assertTrue("The operation should have been successful", result.isSuccessful());
		assertTrue("Expected true to mean there was an existing address", result.getData());
	}

	private <T> void shouldFindByGuidWithResult(final String storeCode, final String cartOrderGuid, final ExecutionResult<T> result) {
		context.checking(new Expectations() {
			{
				oneOf(cartOrderRepository).findByGuid(storeCode, cartOrderGuid);
				will(returnValue(result));
			}
		});
	}

	private <T> void shouldSaveCartOrderWithResult(final CartOrder cartOrder, final ExecutionResult<T> result) {
		context.checking(new Expectations() {
			{
				oneOf(cartOrderRepository).saveCartOrder(cartOrder);
				will(returnValue(result));
			}
		});
	}

	private CartOrder createMockCartOrder(final String billingAddressGuid) {
		final CartOrder cartOrder = context.mock(CartOrder.class);

		context.checking(new Expectations() {
			{
				allowing(cartOrder).getBillingAddressGuid();
				will(returnValue(billingAddressGuid));

				ignoring(cartOrder).setBillingAddressGuid(with(any(String.class)));
			}
		});

		return cartOrder;
	}

}
