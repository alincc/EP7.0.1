package com.elasticpath.repo.datasync.routes;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;

/**
 * Notifies the CMUser of Success or Failure Status of their published change set via E-mail.
 */
public class ChangeSetPublishSendEmailRoute extends SpringRouteBuilder {

	private Endpoint sourceEndpoint;

	private String smtpHostName;

	private Endpoint deadLetterEndpoint;

	private int maximumRedeliveries;

	private long maximumRedeliveryDelay;

	private Processor changeSetPublishEmailProcessor;

	@Override
	public void configure() throws Exception {
		errorHandler(deadLetterChannel(deadLetterEndpoint)
				.maximumRedeliveries(maximumRedeliveries)
				.maximumRedeliveryDelay(maximumRedeliveryDelay)
				.useExponentialBackOff());

		from(sourceEndpoint)
			.process(changeSetPublishEmailProcessor)
		.choice()
			.when(simple("${property.DST_SEND_EMAIL_STATUS} == true"))
				.to("smtp://" + smtpHostName)
		.end();
	}

	public void setSourceEndpoint(final Endpoint sourceEndpoint) {
		this.sourceEndpoint = sourceEndpoint;
	}

	public void setSmtpHostName(final String smtpHostName) {
		this.smtpHostName = smtpHostName;
	}

	public void setDeadLetterEndpoint(final Endpoint deadLetterEndpoint) {
		this.deadLetterEndpoint = deadLetterEndpoint;
	}

	public void setMaximumRedeliveries(final int maximumRedeliveries) {
		this.maximumRedeliveries = maximumRedeliveries;
	}

	public void setMaximumRedeliveryDelay(final long maximumRedeliveryDelay) {
		this.maximumRedeliveryDelay = maximumRedeliveryDelay;
	}

	public void setChangeSetPublishEmailProcessor(final Processor changeSetPublishEmailProcessor) {
		this.changeSetPublishEmailProcessor = changeSetPublishEmailProcessor;
	}
}
