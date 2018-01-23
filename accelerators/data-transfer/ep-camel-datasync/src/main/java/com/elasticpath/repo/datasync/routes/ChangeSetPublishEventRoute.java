package com.elasticpath.repo.datasync.routes;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.DataFormat;

import com.elasticpath.repo.datasync.tools.exception.RecoverableMessageException;

/**
 * Route for Publishing Change Sets.
 */
public class ChangeSetPublishEventRoute extends AbstractErrorHandlingSpringRouteBuilder {

	private Endpoint emailChangeSetResultEndpoint;

	private DataFormat eventMessageDataFormat;

	private Processor retrieveChangeSetGuidProcessor;

	private Processor publishChangeSetProcessor;

	private Processor updateChangeSetStateProcessor;

	private Processor openChangeSetStateProcessor;

	@Override
	public ProcessorDefinition<?> configureRoute(final ProcessorDefinition<?> processorDefinition) {

		return processorDefinition
				.unmarshal(eventMessageDataFormat)
				.process(retrieveChangeSetGuidProcessor)
				.process(publishChangeSetProcessor)
				.choice().when(simple("${property.DST_PUBLISH_SUCCESS} == true"))
					.process(updateChangeSetStateProcessor)
				.end()
				.to(emailChangeSetResultEndpoint)
				.log("Finished Publishing!");
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected ProcessorDefinition<?> getRouteSpecificOnExceptionHandling(final ProcessorDefinition<?> routeDefinition) {
		
		return routeDefinition
			.onException(Exception.class).onWhen(property(Exchange.EXCEPTION_CAUGHT).not().isInstanceOf(RecoverableMessageException.class))
				.to(emailChangeSetResultEndpoint)
				.handled(true)
				.useOriginalMessage()
				.log(LoggingLevel.ERROR, DLQ_ERROR_TEXT + DLQ_ERROR_MESSAGE)
				.doTry()
					.process(openChangeSetStateProcessor)
					.doCatch(Exception.class)
				.end()
				.to(getDeadLetterQueueEndpoint())
			.end()
			.onCompletion().onFailureOnly()
				.process(openChangeSetStateProcessor)
			.end();
	}

	public void setEmailChangeSetResultEndpoint(final Endpoint emailChangeSetResultEndpoint) {
		this.emailChangeSetResultEndpoint = emailChangeSetResultEndpoint;
	}

	public DataFormat getEventMessageDataFormat() {
		return eventMessageDataFormat;
	}

	public void setEventMessageDataFormat(final DataFormat eventMessageDataFormat) {
		this.eventMessageDataFormat = eventMessageDataFormat;
	}

	public void setRetrieveChangeSetGuidProcessor(final Processor retrieveChangeSetGuidProcessor) {
		this.retrieveChangeSetGuidProcessor = retrieveChangeSetGuidProcessor;
	}

	public void setPublishChangeSetProcessor(final Processor publishChangeSetProcessor) {
		this.publishChangeSetProcessor = publishChangeSetProcessor;
	}

	public void setUpdateChangeSetStateProcessor(final Processor updateChangeSetStateProcessor) {
		this.updateChangeSetStateProcessor = updateChangeSetStateProcessor;
	}

	public void setOpenChangeSetStateProcessor(final Processor openChangeSetStateProcessor) {
		this.openChangeSetStateProcessor = openChangeSetStateProcessor;
	}
}
