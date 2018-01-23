/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email.handler.order.producer.impl;

import static org.junit.Assert.assertSame;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.EmailNotificationHelper;
import com.elasticpath.email.util.EmailComposer;

/**
 * Test class for {@link OrderConfirmationEmailProducer}.
 */
public class OrderConfirmationEmailProducerTest {

	private OrderConfirmationEmailProducer producer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final EmailNotificationHelper emailNotificationHelper = context.mock(EmailNotificationHelper.class);

	private final EmailComposer emailComposer = context.mock(EmailComposer.class);

	@Before
	public void setUp() {
		producer = new OrderConfirmationEmailProducer();
		producer.setEmailComposer(emailComposer);
		producer.setEmailNotificationHelper(emailNotificationHelper);
	}

	@Test
	public void verifyOrderConfirmationEmailIsCreatedFromOrderNumber() throws Exception {
		final Email expectedEmail = new SimpleEmail();
		final String orderNumber = "order123";

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				oneOf(emailNotificationHelper).getOrderEmailProperties(orderNumber);
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Email actualEmail = producer.createEmail(orderNumber, null);
		assertSame("Unexpected email created by producer", expectedEmail, actualEmail);
	}

	@Test(expected = EmailException.class)
	public void verifyEmailExceptionNotCaught() throws Exception {
		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				allowing(emailNotificationHelper).getOrderEmailProperties(with(any(String.class)));
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(throwException(new EmailException("Oh no!")));
			}
		});

		producer.createEmail("foo", null);
	}

}
