package com.elasticpath.repo.datasync.routes;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Endpoint;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.DataFormat;

import com.elasticpath.repo.datasync.tools.exception.UnrecoverableMessageException;

/**
 * Routes message to different endpoints depending on the eventType.
 */
public class EventTypeSplitterRoute extends AbstractErrorHandlingSpringRouteBuilder {

	private Map<String, Endpoint> destinationEndPoints;

	private DataFormat eventMessageDataFormat;

	@Override
	public ProcessorDefinition<?> configureRoute(final ProcessorDefinition<?> processorDefinition) {
		ChoiceDefinition choiceDefinition = 
				processorDefinition
				.unmarshal(eventMessageDataFormat)
				.choice();

		for (Entry<String, Endpoint> entry : destinationEndPoints.entrySet()) {
			final String entryEventType = entry.getKey();
			final Endpoint entryEndpoint = entry.getValue();
			choiceDefinition = choiceDefinition
					.when(simple(String.format("${body.getEventType.getName} == '%s'", entryEventType)))
						.marshal(eventMessageDataFormat)
						.to(entryEndpoint);
		}

		choiceDefinition = choiceDefinition.otherwise().throwException(
				new UnrecoverableMessageException("No route defined for event type or eventType field missing"));

		return choiceDefinition.end();
	}

	public void setDestinationEndPoints(final Map<String, Endpoint> destinationEndPoints) {
		this.destinationEndPoints = destinationEndPoints;
	}

	public DataFormat getEventMessageDataFormat() {
		return eventMessageDataFormat;
	}

	public void setEventMessageDataFormat(final DataFormat eventMessageDataFormat) {
		this.eventMessageDataFormat = eventMessageDataFormat;
	}

}
