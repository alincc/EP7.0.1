/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.customer.producer.impl;

import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.customer.helper.CustomerEmailPropertyHelper;
import com.elasticpath.email.util.EmailComposer;
import com.elasticpath.service.customer.CustomerService;

/**
 * Test class for {@link AnonymousCustomerRegisteredEmailProducerTest}.
 */
public class AnonymousCustomerRegisteredEmailProducerTest {

	private static final String PASSWORD_KEY = "password";

	private AnonymousCustomerRegisteredEmailProducer emailProducer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final CustomerEmailPropertyHelper customerEmailPropertyHelper = context.mock(CustomerEmailPropertyHelper.class);

	private final EmailComposer emailComposer = context.mock(EmailComposer.class);

	private final CustomerService customerService = context.mock(CustomerService.class);

	private static final String CUSTOMER_GUID = "customerGuid1";

	@Before
	public void setUp() {
		emailProducer = new AnonymousCustomerRegisteredEmailProducer();

		emailProducer.setEmailComposer(emailComposer);
		emailProducer.setCustomerEmailPropertyHelper(customerEmailPropertyHelper);
		emailProducer.setCustomerService(customerService);
	}

	@Test
	public void verifyNewlyRegisteredCustomerEmailIsCreatedFromCustomerGuid() throws Exception {
		final Email expectedEmail = new SimpleEmail();

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				allowing(customerService).findByGuid(CUSTOMER_GUID);
				will(returnValue(context.mock(Customer.class)));

				oneOf(customerEmailPropertyHelper).getNewlyRegisteredCustomerEmailProperties(with(any(Customer.class)), with(any(String.class)));
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Map<String, Object> additionalData = buildValidAdditionalData();
		final Email actualEmail = emailProducer.createEmail(CUSTOMER_GUID, additionalData);
		assertSame("Unexpected email created by producer", expectedEmail, actualEmail);
	}

	@Test(expected = EmailException.class)
	public void verifyEmailExceptionNotCaught() throws Exception {

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				allowing(customerService).findByGuid(with(any(String.class)));
				will(returnValue(context.mock(Customer.class)));

				oneOf(customerEmailPropertyHelper).getNewlyRegisteredCustomerEmailProperties(with(any(Customer.class)), with(any(String.class)));
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(throwException(new EmailException("Oh no!")));
			}
		});

		final Map<String, Object> additionalData = buildValidAdditionalData();
		emailProducer.createEmail(CUSTOMER_GUID, additionalData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNoCustomerIsFound() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(customerService).findByGuid(CUSTOMER_GUID);
				will(returnValue(null));
			}
		});
		final Map<String, Object> additionalData = buildValidAdditionalData();
		emailProducer.createEmail(CUSTOMER_GUID, additionalData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNoPasswordIsSupplied() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(customerService).findByGuid(CUSTOMER_GUID);
				will(returnValue(null));
			}
		});
		final Map<String, Object> additionalData = buildValidAdditionalData();
		additionalData.remove(PASSWORD_KEY);
		emailProducer.createEmail(CUSTOMER_GUID, additionalData);
	}

	private Map<String, Object> buildValidAdditionalData() {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put(PASSWORD_KEY, "newPassword");
		return additionalData;
	}

}
