/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.emailinfo.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.common.authentication.AuthenticationConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Tests the {@link EmailInfoLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailInfoLookupStrategyImplTest {
	private static final String SCOPE = "scope";
	private static final String DECODED_ORDER_ID = "decodedOrderId";
	private static final String CUSTOMER_EMAIL = "customerEmail";
	private static final String CUSTOMER_GUID = "customerGuid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private Customer customer;
	@Mock
	private ResourceOperationContext resourceOperationContext;

	@InjectMocks
	private EmailInfoLookupStrategyImpl emailInfoLookupStrategy;

	/**
	 * Sets up common elements of the test.
	 */
	@Before
	public void setUp() {
		when(resourceOperationContext.getUserIdentifier())
				.thenReturn(CUSTOMER_GUID);
	}

	/**
	 * Tests getting an email for an order when a customer has an email.
	 */
	@Test
	public void testGetEmailForOrder() {
		shouldFindCustomerForCustomerGuid(ExecutionResultFactory.createReadOK(customer));
		shouldFindEmailForCustomer(CUSTOMER_EMAIL);

		ExecutionResult<String> result = emailInfoLookupStrategy.getEmailIdForOrder(SCOPE, DECODED_ORDER_ID);

		assertEquals("The email returned should be the same as expected", CUSTOMER_EMAIL,
				result.getData());

	}

	/**
	 * Tests getting an email for an order when a customer has no email selected.
	 */
	@Test
	public void testGetEmailForOrderWhenNoEmailIsSelected() {
		shouldFindCustomerForCustomerGuid(ExecutionResultFactory.createReadOK(customer));
		shouldFindEmailForCustomer(null);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		emailInfoLookupStrategy.getEmailIdForOrder(SCOPE, DECODED_ORDER_ID);
	}

	/**
	 * Tests getting an email for an order when a customer is not found.
	 */
	@Test
	public void testGetEmailForOrderWhenCustomerNotFound() {
		shouldFindCustomerForCustomerGuid(ExecutionResultFactory.<Customer>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		emailInfoLookupStrategy.getEmailIdForOrder(SCOPE, DECODED_ORDER_ID);
	}

	/**
	 * Tests getting an email for an order when a customer email is anonymous.
	 */
	@Test
	public void testGetEmailForOrderWhenCustomerEmailIsAnonymous() {
		shouldFindCustomerForCustomerGuid(ExecutionResultFactory.createReadOK(customer));
		shouldFindEmailForCustomer(AuthenticationConstants.ANONYMOUS_USER_ID);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		emailInfoLookupStrategy.getEmailIdForOrder(SCOPE, DECODED_ORDER_ID);
	}

	private void shouldFindEmailForCustomer(final String email) {
		when(customer.getEmail()).thenReturn(email);
	}

	private void shouldFindCustomerForCustomerGuid(final ExecutionResult<Customer> result) {
		when(customerRepository.findCustomerByGuid(CUSTOMER_GUID)).thenReturn(result);
	}
}
