/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.exception.EpValidationException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.common.validator.EpDomainValidator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Test for {@link EmailWriterStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailWriterStrategyImplTest {

	private static final String EXPECTED_RESOURCE_STATUS = "Should contain the expected resource status.";
	private static final String VALID_EMAIL = "email@elasticapth.com";
	private static final String INVALID_EMAIL = "gjkhgjk";
	private static final String EMAIL_ID = Base32Util.encode(VALID_EMAIL);
	private static final String CUSTOMER_GUID = "CUSTOMER_GUID";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private EpDomainValidator customerValidator;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@Mock
	private Customer customer;

	@InjectMocks
	private EmailWriterStrategyImpl emailWriterStrategy;

	private static final List<StructuredErrorMessage> STRUCTURED_ERROR_MESSAGES = Arrays.asList(mock(StructuredErrorMessage.class));

	/**
	 * Test successfully creating an email.
	 */
	@Test
	public void testSuccessfulCreateEmail() {
		EmailEntity emailEntity = createEmailEntity(VALID_EMAIL);

		shouldCustomerRepositoryUpdateResult(ExecutionResultFactory.<Void>createUpdateOK());
		shouldGetUserIdentifier(CUSTOMER_GUID);
		shouldFindCustomerByGuidWithResult(CUSTOMER_GUID, ExecutionResultFactory.createReadOK(customer));

		ExecutionResult<Void> result = emailWriterStrategy.createEmail(emailEntity);

		assertTrue("Should result in a successful operation.", result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.UPDATE_OK, result.getResourceStatus());
	}

	/**
	 * Test create email when user not found.
	 */
	@Test
	public void testCreateEmailWhenUserNotFound() {
		EmailEntity emailEntity = createEmailEntity(VALID_EMAIL);

		shouldGetUserIdentifier(CUSTOMER_GUID);
		shouldFindCustomerByGuidWithResult(CUSTOMER_GUID, ExecutionResultFactory.<Customer>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		emailWriterStrategy.createEmail(emailEntity);
	}

	/**
	 * Test create email when email not set.
	 */
	@Test
	public void testCreateEmailWhenEmailNotSet() {
		EmailEntity emailEntity = createEmailEntity(StringUtils.EMPTY);

		shouldGetUserIdentifier(CUSTOMER_GUID);
		shouldFindCustomerByGuidWithResult(CUSTOMER_GUID, ExecutionResultFactory.createReadOK(customer));

		when(customerRepository.updateCustomer(customer)).thenThrow(new EpValidationException("Validation Error", STRUCTURED_ERROR_MESSAGES));
		thrown.expect(EpValidationException.class);

		emailWriterStrategy.createEmail(emailEntity);
	}

	/**
	 * Test create email when email not set for anonymous user.
	 */
	@Test
	public void testCreateEmailWhenEmailNotSetAnonymousUser() {
		EmailEntity emailEntity = createEmailEntity(StringUtils.EMPTY);

		shouldGetUserIdentifier(CUSTOMER_GUID);
		shouldFindCustomerByGuidWithResult(CUSTOMER_GUID, ExecutionResultFactory.createReadOK(customer));
		shouldValidatePropertyForAnonymousUser(customer, ExecutionResultFactory.<Void>createBadRequestBody("Email is missing."));

		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		emailWriterStrategy.createEmail(emailEntity);
	}

	/**
	 * Test create email when email is null.
	 */
	@Test
	public void testCreateEmailWhenEmailIsNull() {
		EmailEntity emailEntity = createEmailEntity(null);

		shouldGetUserIdentifier(CUSTOMER_GUID);
		shouldFindCustomerByGuidWithResult(CUSTOMER_GUID, ExecutionResultFactory.createReadOK(customer));

		when(customerRepository.updateCustomer(customer)).thenThrow(new EpValidationException("Validation Error", STRUCTURED_ERROR_MESSAGES));
		thrown.expect(EpValidationException.class);

		emailWriterStrategy.createEmail(emailEntity);
	}

	/**
	 * Test create email when email is null for anonymous user.
	 */
	@Test
	public void testCreateEmailWhenEmailIsNullForAnonymousUser() {
		EmailEntity emailEntity = createEmailEntity(null);

		shouldGetUserIdentifier(CUSTOMER_GUID);
		shouldFindCustomerByGuidWithResult(CUSTOMER_GUID, ExecutionResultFactory.createReadOK(customer));
		shouldValidatePropertyForAnonymousUser(customer, ExecutionResultFactory.<Void>createBadRequestBody("Email is missing."));

		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		emailWriterStrategy.createEmail(emailEntity);
	}

	/**
	 * Test create email when email is invalid.
	 */
	@Test
	public void testCreateEmailWhenEmailIsInvalid() {
		EmailEntity emailEntity = createEmailEntity(INVALID_EMAIL);

		shouldGetUserIdentifier(CUSTOMER_GUID);
		shouldFindCustomerByGuidWithResult(CUSTOMER_GUID, ExecutionResultFactory.createReadOK(customer));

		when(customerRepository.updateCustomer(customer)).thenThrow(new EpValidationException("Validation Error", STRUCTURED_ERROR_MESSAGES));
		thrown.expect(EpValidationException.class);

		emailWriterStrategy.createEmail(emailEntity);
	}

	/**
	 * Test create email when email is invalid for anonymous user.
	 */
	@Test
	public void testCreateEmailWhenEmailIsInvalidForAnonymousUser() {
		EmailEntity emailEntity = createEmailEntity(INVALID_EMAIL);

		shouldGetUserIdentifier(CUSTOMER_GUID);
		shouldFindCustomerByGuidWithResult(CUSTOMER_GUID, ExecutionResultFactory.createReadOK(customer));
		shouldValidatePropertyForAnonymousUser(customer, ExecutionResultFactory.<Void>createBadRequestBody("Email is invalid."));

		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		emailWriterStrategy.createEmail(emailEntity);
	}

	private void shouldCustomerRepositoryUpdateResult(final ExecutionResult<Void> expectedResult) {
		when(customerRepository.updateCustomer(customer))
				.thenReturn(expectedResult);
	}

	private void shouldGetUserIdentifier(final String result) {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(result);
	}

	private void shouldFindCustomerByGuidWithResult(final String customerGuid, final ExecutionResult<Customer> result) {
		when(customerRepository.findCustomerByGuid(customerGuid)).thenReturn(result);
	}

	private void shouldValidatePropertyForAnonymousUser(final Customer customer, final ExecutionResult<Void> result) {
		when(customer.isAnonymous()).thenReturn(true);
		when(customerValidator.validateProperty(customer, "email")).thenReturn(result);
	}

	private EmailEntity createEmailEntity(final String email) {
		return EmailEntity.builder()
				.withEmail(email)
				.withEmailId(EMAIL_ID)
				.build();
	}
}
