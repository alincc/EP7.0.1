/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.producer.impl;

import static org.junit.Assert.assertSame;

import java.util.Collections;
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

import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.EmailNotificationHelper;
import com.elasticpath.email.util.EmailComposer;

/**
 * Unit test for {@link OrderShipmentShippedEmailProducer}.
 */
public class OrderShipmentShippedEmailProducerTest {

	private OrderShipmentShippedEmailProducer producer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final EmailNotificationHelper emailNotificationHelper = context.mock(EmailNotificationHelper.class);

	private final EmailComposer emailComposer = context.mock(EmailComposer.class);

	private static final String ORDER_GUID_KEY = "orderGuid";

	private static final String ORDER_GUID = "order123";

	private static final String SHIPMENT_NUMBER = "shipment1";

	@Before
	public void setUp() {
		producer = new OrderShipmentShippedEmailProducer();
		producer.setEmailComposer(emailComposer);
		producer.setEmailNotificationHelper(emailNotificationHelper);
	}

	@Test
	public void verifyOrderShippedEmailIsCreatedFromOrderAndShipmentNumbers() throws Exception {
		final Email expectedEmail = new SimpleEmail();

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				oneOf(emailNotificationHelper).getShipmentConfirmationEmailProperties(ORDER_GUID, SHIPMENT_NUMBER);
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Map<String, Object> data = new HashMap<>();
		data.put(ORDER_GUID_KEY, ORDER_GUID);

		final Email actualEmail = producer.createEmail(SHIPMENT_NUMBER, data);
		assertSame("Unexpected email created by producer", expectedEmail, actualEmail);
	}

	@Test(expected = EmailException.class)
	public void verifyEmailExceptionNotCaught() throws Exception {
		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				allowing(emailNotificationHelper).getShipmentConfirmationEmailProperties(ORDER_GUID, SHIPMENT_NUMBER);
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(throwException(new EmailException("Oh no!")));
			}
		});

		final Map<String, Object> data = new HashMap<>();
		data.put(ORDER_GUID_KEY, ORDER_GUID);
		producer.createEmail(SHIPMENT_NUMBER, data);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNoOrderGuidGiven() throws Exception {
		producer.createEmail(ORDER_GUID, Collections.singletonMap(ORDER_GUID_KEY, null));
	}

}
