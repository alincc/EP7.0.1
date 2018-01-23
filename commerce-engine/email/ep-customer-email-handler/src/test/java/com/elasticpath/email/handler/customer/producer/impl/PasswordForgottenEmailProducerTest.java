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
 * Unit test for {@link PasswordForgottenEmailProducer}.
 */
public class PasswordForgottenEmailProducerTest {

	private PasswordForgottenEmailProducer emailProducer;

	// CHECKSTYLE:OFF
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	// CHECKSTYLE:ON

	private final CustomerEmailPropertyHelper customerEmailPropertyHelper = context.mock(CustomerEmailPropertyHelper.class);

	private final EmailComposer emailComposer = context.mock(EmailComposer.class);

	private final CustomerService customerService = context.mock(CustomerService.class);

	private static final String CUSTOMER_GUID = "customerGuid1";

	private static final String NEW_PASSWORD_VALUE = "newPassword";

	private static final String NEW_PASSWORD_KEY = "newPassword";

	/**
	 * Set up.
	 */
	@Before
	public void setUp() {
		emailProducer = new PasswordForgottenEmailProducer();

		emailProducer.setEmailComposer(emailComposer);
		emailProducer.setCustomerEmailPropertyHelper(customerEmailPropertyHelper);
		emailProducer.setCustomerService(customerService);
	}

	/**
	 * verifyPasswordForgottenEmailIsCreatedFromCustomerGuid.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public void verifyPasswordForgottenEmailIsCreatedFromCustomerGuid() throws Exception {
		final Email expectedEmail = new SimpleEmail();
		final String customerGuid = "customerGuid1";

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				allowing(customerService).findByGuid(customerGuid);
				will(returnValue(context.mock(Customer.class)));

				oneOf(customerEmailPropertyHelper).getForgottenPasswordEmailProperties(with(any(Customer.class)), with(equal(NEW_PASSWORD_VALUE)));
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Map<String, Object> data = new HashMap<>();
		data.put(NEW_PASSWORD_KEY, NEW_PASSWORD_VALUE);
		final Email actualEmail = emailProducer.createEmail(customerGuid, data);
		assertSame("Unexpected email created by producer", expectedEmail, actualEmail);
	}

	/**
	 * verifyEmailExceptionNotCaught.
	 * 
	 * @throws Exception exception
	 */
	@Test(expected = EmailException.class)
	public void verifyEmailExceptionNotCaught() throws Exception {

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				allowing(customerService).findByGuid(CUSTOMER_GUID);
				will(returnValue(context.mock(Customer.class)));

				oneOf(customerEmailPropertyHelper).getForgottenPasswordEmailProperties(with(any(Customer.class)), with(equal(NEW_PASSWORD_VALUE)));
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(throwException(new EmailException("Oh no!")));
			}
		});

		final Map<String, Object> data = new HashMap<>();
		data.put(NEW_PASSWORD_KEY, NEW_PASSWORD_VALUE);
		emailProducer.createEmail(CUSTOMER_GUID, data);
	}

	/**
	 * verifyExceptionThrownWhenNoNewPasswordProvided.
	 * 
	 * @throws Exception exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNoNewPasswordProvided() throws Exception {

		final Map<String, Object> data = new HashMap<>();
		emailProducer.createEmail(CUSTOMER_GUID, data);
	}

	/**
	 * verifyExceptionThrownWhenNoCustomerIsFound.
	 * 
	 * @throws Exception exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNoCustomerIsFound() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(customerService).findByGuid(CUSTOMER_GUID);
				will(returnValue(null));
			}
		});

		final Map<String, Object> data = new HashMap<>();
		data.put(NEW_PASSWORD_KEY, NEW_PASSWORD_VALUE);
		emailProducer.createEmail(CUSTOMER_GUID, data);
	}
}
