/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.producer.impl;

import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.OrderEmailPropertyHelper;
import com.elasticpath.email.util.EmailComposer;
import com.elasticpath.service.order.OrderService;

/**
 * Test class for {@link OrderShipmentReleaseFailedEmailProducer}.
 */
public class OrderShipmentReleaseFailedEmailProducerTest {

	private static final String ORDER_GUID = "ORDER-001";
	private static final String ORDER_GUID_KEY = "orderGuid";
	private static final String SHIPMENT_TYPE_KEY = "shipmentType";
	private static final String ERROR_MESSAGE_KEY = "errorMessage";
	private static final String SHIPMENT_TYPE_NAME = "PHYSICAL";
	private static final String ERROR_MESSAGE = "Oh no!";

	private OrderShipmentReleaseFailedEmailProducer producer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private OrderEmailPropertyHelper orderEmailPropertyHelper;

	@Mock
	private EmailComposer emailComposer;

	@Mock
	private OrderService orderService;

	@Before
	public void setUp() {
		producer = new OrderShipmentReleaseFailedEmailProducer();
		producer.setEmailComposer(emailComposer);
		producer.setOrderEmailPropertyHelper(orderEmailPropertyHelper);
		producer.setOrderService(orderService);
	}

	@Test
	public void verifyEmailProducerFromEventMessage() throws Exception {
		final Email expectedEmail = new SimpleEmail();
		final String shipmentNumber = "SHIP-001";
		final ShipmentType shipmentType = ShipmentType.PHYSICAL;

		final OrderShipment shipment = context.mock(OrderShipment.class);

		final Map<String, Object> data = ImmutableMap.<String, Object>of(
				ERROR_MESSAGE_KEY, ERROR_MESSAGE,
				ORDER_GUID_KEY, ORDER_GUID, // this is not used by the producer, but real messages will contain this field anyway.
				SHIPMENT_TYPE_KEY, SHIPMENT_TYPE_NAME);

		context.checking(new Expectations() {
			{
				allowing(shipment).getShipmentNumber();
				will(returnValue(shipmentNumber));

				allowing(orderService).findOrderShipment(shipmentNumber, shipmentType);
				will(returnValue(shipment));

				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				oneOf(orderEmailPropertyHelper).getFailedShipmentPaymentEmailProperties(shipment, ERROR_MESSAGE);
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		assertSame("Unexpected email created", expectedEmail, producer.createEmail(shipmentNumber, data));
	}

	@Test
	public void verifyExceptionThrownWhenNoErrorMessageGiven() throws Exception {
		final Map<String, Object> data = new HashMap<>();
		data.put(ERROR_MESSAGE_KEY, null);
		data.put(SHIPMENT_TYPE_KEY, SHIPMENT_TYPE_NAME);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(ERROR_MESSAGE_KEY);

		producer.createEmail(ORDER_GUID, data);
	}

	@Test
	public void verifyExceptionThrownWhenNoShipmentTypeGiven() throws Exception {
		final Map<String, Object> data = new HashMap<>();
		data.put(ERROR_MESSAGE_KEY, ERROR_MESSAGE);
		data.put(SHIPMENT_TYPE_KEY, null);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(SHIPMENT_TYPE_KEY);

		producer.createEmail(ORDER_GUID, data);
	}

}