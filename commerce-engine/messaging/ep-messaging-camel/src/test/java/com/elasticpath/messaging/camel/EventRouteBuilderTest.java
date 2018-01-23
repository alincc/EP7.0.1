/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.messaging.camel;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.log4j.Logger;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.camel.test.support.TransactionPolicyRegistryManager;
import com.elasticpath.messaging.factory.impl.EventMessageFactoryImpl;

/**
 * Test for {@link EventRouteBuilder}.
 */
public class EventRouteBuilderTest extends CamelTestSupport {

	private static final Logger LOG = Logger.getLogger(EventRouteBuilderTest.class);

	private static final String INCOMING_ENDPOINT_URI = "direct:orderEvents";

	private static final String JMS_ENDPOINT_URI = "jms:orderEvents";

	private MockEndpoint mockOutgoingEndpoint;

	private NotifyBuilder notifyBuilder;

	private final EventRouteBuilder eventRouteBuilder = new EventRouteBuilder();

	@Rule
	public final JUnitRuleMockery mockery = new JUnitRuleMockery();

	private final EventType eventType = mockery.mock(EventType.class);

	@Override
	protected JndiRegistry createRegistry() throws Exception {
		final JndiRegistry registry = super.createRegistry();
		new TransactionPolicyRegistryManager(registry).registerDefaultTransactionPolicy();
		return registry;
	}

	/**
	 * Sets up test case.
	 * 
	 * @throws Exception on error
	 */
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		final Endpoint incomingEndpoint = getMandatoryEndpoint(INCOMING_ENDPOINT_URI);
		mockOutgoingEndpoint = getMockEndpoint("mock:" + JMS_ENDPOINT_URI);

		eventRouteBuilder.setIncomingEndpoint(incomingEndpoint);
		eventRouteBuilder.setOutgoingEndpoint(mockOutgoingEndpoint);
		eventRouteBuilder.setEventMessageDataFormat(new JacksonDataFormat(new ObjectMapper(), EventMessage.class));

		notifyBuilder = new NotifyBuilder(context)
				.wereSentTo(mockOutgoingEndpoint.getEndpointUri())
				.whenDone(1)
				.create();

		mockery.checking(new Expectations() {
			{
				allowing(eventType).getName();
				will(returnValue("eventType"));
			}
		});

		context.addRoutes(eventRouteBuilder);
	}

	/**
	 * Ensure order event enqueues a JSON representation of an EventMessage.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testDefaultOrderEventEnqueue() throws Exception {
		final EventMessage eventMessage = createTestOrderEventMessage();
		template.sendBody(INCOMING_ENDPOINT_URI, eventMessage);
		assertTrue("Failed to deliver message", notifyBuilder.matches(2, TimeUnit.SECONDS));

		mockOutgoingEndpoint.expectedMessagesMatches(new EventMessageJsonGuidValidator(eventMessage));
		mockOutgoingEndpoint.expectedMessagesMatches(new EventMessageJsonEventTypeValidator(eventMessage));
		mockOutgoingEndpoint.expectedMessagesMatches(new EventMessageJsonDataValidator(eventMessage));
		mockOutgoingEndpoint.assertIsSatisfied();
	}

	private EventMessage createTestOrderEventMessage() {
		return new EventMessageFactoryImpl().createEventMessage(eventType, "GUID");
	}

	/**
	 * Shares common functionality used to validate a JSON string is an accurate representation of an {@link EventMessage}.
	 */
	private abstract class AbstractEventMessageJsonValidator implements Predicate {
		protected final EventMessage eventMessage;

		protected AbstractEventMessageJsonValidator(final EventMessage eventMessage) {
			this.eventMessage = eventMessage;
		}

		@SuppressWarnings("unchecked")
		protected Map<String, Object> unmarshalToMap(final Exchange exchange) throws IOException {
			final String jsonString = exchange.getIn().getBody(String.class);
			return (Map<String, Object>) new ObjectMapper().readValue(jsonString, Map.class);
		}

		protected <T> boolean determineEquality(final T obj1, final T obj2) {
			LOG.info("Determining equality of [" + obj1 + "] and [" + obj2 + "].");
			return obj1.equals(obj2);
		}
	}

	/**
	 * Validates the GUID component of a JSON string representation of an {@link EventMessage}.
	 */
	private class EventMessageJsonGuidValidator extends AbstractEventMessageJsonValidator {
		EventMessageJsonGuidValidator(final EventMessage eventMessage) {
			super(eventMessage);
		}

		@Override
		public boolean matches(final Exchange exchange) {
			final Map<String, Object> eventMessageMap;

			try {
				eventMessageMap = unmarshalToMap(exchange);
			} catch (final Exception e) {
				return false;
			}

			return determineEquality(eventMessage.getGuid(), eventMessageMap.get("guid"));
		}
	}

	/**
	 * Validates the EventType component of a JSON string representation of an {@link EventMessage}.
	 */
	private class EventMessageJsonEventTypeValidator extends AbstractEventMessageJsonValidator {
		EventMessageJsonEventTypeValidator(final EventMessage eventMessage) {
			super(eventMessage);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean matches(final Exchange exchange) {
			final Map<String, Object> eventMessageMap;

			try {
				eventMessageMap = unmarshalToMap(exchange);
			} catch (final Exception e) {
				return false;
			}

			final Map<String, Object> eventType = (Map<String, Object>) eventMessageMap.get("eventType");
			final String eventTypeName = (String) eventType.get("name");

			return determineEquality(eventMessage.getEventType().getName(), eventTypeName);
		}
	}

	/**
	 * Validates the Data component of a JSON string representation of an {@link EventMessage}.
	 */
	private class EventMessageJsonDataValidator extends AbstractEventMessageJsonValidator {
		EventMessageJsonDataValidator(final EventMessage eventMessage) {
			super(eventMessage);
		}

		@Override
		public boolean matches(final Exchange exchange) {
			final Map<String, Object> eventMessageMap;

			try {
				eventMessageMap = unmarshalToMap(exchange);
			} catch (final Exception e) {
				return false;
			}

			return determineEquality(eventMessage.getData(), eventMessageMap.get("data"));
		}
	}

}
