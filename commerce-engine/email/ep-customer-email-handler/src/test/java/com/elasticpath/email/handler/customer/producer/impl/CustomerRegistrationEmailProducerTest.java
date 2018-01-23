/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.customer.producer.impl;

import static org.junit.Assert.assertSame;

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
 * Test class for {@link CustomerRegistrationEmailProducer}.
 */
public class CustomerRegistrationEmailProducerTest {

	private CustomerRegistrationEmailProducer emailProducer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final CustomerEmailPropertyHelper customerEmailPropertyHelper = context.mock(CustomerEmailPropertyHelper.class);

	private final EmailComposer emailComposer = context.mock(EmailComposer.class);

	private final CustomerService customerService = context.mock(CustomerService.class);

	private static final String CUSTOMER_GUID = "customerGuid1";

	@Before
	public void setUp() {
		emailProducer = new CustomerRegistrationEmailProducer();

		emailProducer.setEmailComposer(emailComposer);
		emailProducer.setCustomerEmailPropertyHelper(customerEmailPropertyHelper);
		emailProducer.setCustomerService(customerService);
	}

	@Test
	public void verifyCustomerRegistrationEmailIsCreatedFromCustomerGuid() throws Exception {
		final Email expectedEmail = new SimpleEmail();

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				allowing(customerService).findByGuid(CUSTOMER_GUID);
				will(returnValue(context.mock(Customer.class)));

				oneOf(customerEmailPropertyHelper).getNewAccountEmailProperties(with(any(Customer.class)));
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Email actualEmail = emailProducer.createEmail(CUSTOMER_GUID, null);
		assertSame("Unexpected email created by producer", expectedEmail, actualEmail);
	}

	@Test(expected = EmailException.class)
	public void verifyEmailExceptionNotCaught() throws Exception {

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				allowing(customerService).findByGuid(with(any(String.class)));
				will(returnValue(context.mock(Customer.class)));

				allowing(customerEmailPropertyHelper).getNewAccountEmailProperties(with(any(Customer.class)));
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(throwException(new EmailException("Oh no!")));
			}
		});

		emailProducer.createEmail(CUSTOMER_GUID, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNoCustomerIsFound() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(customerService).findByGuid(CUSTOMER_GUID);
				will(returnValue(null));
			}
		});

		emailProducer.createEmail(CUSTOMER_GUID, null);
	}

}
