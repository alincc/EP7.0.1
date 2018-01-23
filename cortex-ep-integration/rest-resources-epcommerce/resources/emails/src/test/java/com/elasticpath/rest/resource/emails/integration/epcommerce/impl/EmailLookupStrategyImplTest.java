/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.emails.integration.epcommerce.transform.EmailTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.common.authentication.AuthenticationConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Tests the {@link EmailLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailLookupStrategyImplTest {

	private static final String STORE_CODE = "store_code";
	private static final String CUSTOMER_GUID = "customer_guid";
	private static final String EMAIL_ID = "email_id";
	private static final String OTHER_EMAIL_ID = "other_email_id";
	private static final String ANONYMOUS_EMAIL_ID = AuthenticationConstants.ANONYMOUS_USER_ID;
	private static final String EMPTY_EMAIL_ID = "";

	private static final String OK_MESSAGE = "The resource status should be 200 OK.";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private Customer customer;
	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private EmailTransformer emailTransformer;
	@Mock
	private ResourceOperationContext resourceOperationContext;

	@InjectMocks
	private EmailLookupStrategyImpl emailLookupStrategyImpl;

	/**
	 * Tests finding an emailDto.
	 */
	@Test
	public void testSuccessfulFindEmail() {
		EmailEntity expectedEntity = createEmailEntity();
		ExecutionResult<Customer> customerResult = ExecutionResultFactory.createReadOK(customer);

		shouldReturnCustomerGuid();
		shouldReturnCustomer(customerResult);
		shouldReturnCustomerEmail(EMAIL_ID);
		shouldTransformToEntity(expectedEntity);

		ExecutionResult<EmailEntity> result = emailLookupStrategyImpl.findEmail(STORE_CODE, EMAIL_ID);

		assertEquals("This should return the expected entity.", expectedEntity, result.getData());
		assertEquals(OK_MESSAGE, ResourceStatus.READ_OK, result.getResourceStatus());
	}

	/**
	 * Tests finding an email where the email Id and customer email Id don't match.
	 */
	@Test
	public void testFindNotMatchingEmail() {
		ExecutionResult<Customer> customerResult = ExecutionResultFactory.createReadOK(customer);

		shouldReturnCustomerGuid();
		shouldReturnCustomer(customerResult);
		shouldReturnCustomerEmail(OTHER_EMAIL_ID);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		emailLookupStrategyImpl.findEmail(STORE_CODE, EMAIL_ID);
	}

	/**
	 * Tests finding an email where the customer cannot be found.
	 */
	@Test
	public void testFindEmailFindsNoCustomer() {
		ExecutionResult<Customer> customerResult = ExecutionResultFactory.createNotFound();

		shouldReturnCustomerGuid();
		shouldReturnCustomer(customerResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		emailLookupStrategyImpl.findEmail(STORE_CODE, EMAIL_ID);
	}

	/**
	 * Tests finding an email Id.
	 */
	@Test
	public void testSuccessfulFindEmailIds() {
		ExecutionResult<Customer> customerResult = ExecutionResultFactory.createReadOK(customer);

		shouldReturnCustomer(customerResult);
		shouldReturnCustomerEmail(EMAIL_ID);

		ExecutionResult<Collection<String>> result = emailLookupStrategyImpl.findEmailIds(STORE_CODE, CUSTOMER_GUID);

		assertEquals("This should return the email id.", Arrays.asList(EMAIL_ID), result.getData());
		assertEquals(OK_MESSAGE, ResourceStatus.READ_OK, result.getResourceStatus());
	}

	/**
	 * Tests finding an anonymous email id.
	 */
	@Test
	public void testFindAnonymousEmailIds() {
		ExecutionResult<Customer> customerResult = ExecutionResultFactory.createReadOK(customer);

		shouldReturnCustomer(customerResult);
		shouldReturnCustomerEmail(ANONYMOUS_EMAIL_ID);

		ExecutionResult<Collection<String>> result = emailLookupStrategyImpl.findEmailIds(STORE_CODE, CUSTOMER_GUID);

		assertThat("This should return an empty list.", result.getData(), Matchers.empty());
		assertEquals(OK_MESSAGE, ResourceStatus.READ_OK, result.getResourceStatus());
	}

	/**
	 * Tests finding an empty email id.
	 */
	@Test
	public void testFindEmptyEmailIds() {
		ExecutionResult<Customer> customerResult = ExecutionResultFactory.createReadOK(customer);

		shouldReturnCustomer(customerResult);
		shouldReturnCustomerEmail(EMPTY_EMAIL_ID);

		ExecutionResult<Collection<String>> result = emailLookupStrategyImpl.findEmailIds(STORE_CODE, CUSTOMER_GUID);

		assertThat("This should return an empty list.", result.getData(), Matchers.empty());
		assertEquals(OK_MESSAGE, ResourceStatus.READ_OK, result.getResourceStatus());
	}

	/**
	 * Tests finding an email id when customer not found.
	 */
	@Test
	public void testFindEmailIdsFindsNoCustomer() {
		ExecutionResult<Customer> customerResult = ExecutionResultFactory.createNotFound();

		shouldReturnCustomer(customerResult);
		shouldReturnCustomerEmail(EMPTY_EMAIL_ID);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		emailLookupStrategyImpl.findEmailIds(STORE_CODE, CUSTOMER_GUID);
	}

	private void shouldReturnCustomerGuid() {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(CUSTOMER_GUID);
	}

	private void shouldReturnCustomer(final ExecutionResult<Customer> result) {
		when(customerRepository.findCustomerByGuid(CUSTOMER_GUID)).thenReturn(result);
	}

	private void shouldReturnCustomerEmail(final String emailId) {
		when(customer.getEmail()).thenReturn(emailId);
	}

	private void shouldTransformToEntity(final EmailEntity entity) {
		when(emailTransformer.transformToEntity(EMAIL_ID)).thenReturn(entity);
	}

	private EmailEntity createEmailEntity() {
		return EmailEntity.builder()
								.build();
	}
}
