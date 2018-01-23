package com.elasticpath.repo.camelimport.routes;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;

/**
 * Send an email notification when *.new file is found under .error/ folder.
 */
public class NotifyCamelImportErrorRoute extends SpringRouteBuilder {

	private Endpoint sourceEndpoint;

	private String smtpHostName;

	private Endpoint deadLetterEndpoint;

	private String mailFrom;

	private String mailTo;

	private String mailSubject;

	private String mailBody;

	private int maximumRedeliveries;

	private long maximumRedeliveryDelay;

	private Processor fileAttachmentProcessor;

	/**
	 * Send a notification of the exception with the catalog feed. try 4 times after 1 minute
	 * 
	 * @throws Exception exception.
	 */
	public void configure() throws Exception {
		errorHandler(deadLetterChannel(deadLetterEndpoint).maximumRedeliveries(maximumRedeliveries)
				.maximumRedeliveryDelay(maximumRedeliveryDelay).useExponentialBackOff());

		log.info("NotifyCamelImportErrorRoute.sourceEndpoint=" + sourceEndpoint.getEndpointUri());
		System.out.println("NotifyCamelImportErrorRoute.sourceEndpoint=" + sourceEndpoint.getEndpointUri());
		
		from(sourceEndpoint)
		//from("file:D:/elasticpath/extensions/importexport/ext-importexport-cli/src/main/filtered-resources/target/import/data")
			.process(new Processor() {
				@Override
				public void process(Exchange exchange) throws Exception {
					System.out.println("Inside NotifyCamelImportErrorRouter.");
				}
			})
			.setHeader("To", simple(mailTo)).setHeader("From", simple(mailFrom))
			.setHeader("Subject", simple(mailSubject)).setBody(simple(String.format(mailBody, "${file:name}")))
			.process(fileAttachmentProcessor)
		.to("smtp://" + smtpHostName);
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

	public void setMailFrom(final String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public void setMailTo(final String mailTo) {
		this.mailTo = mailTo;
	}

	public void setMailSubject(final String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public void setMaximumRedeliveries(final int maximumRedeliveries) {
		this.maximumRedeliveries = maximumRedeliveries;
	}

	public void setMaximumRedeliveryDelay(final long maximumRedeliveryDelay) {
		this.maximumRedeliveryDelay = maximumRedeliveryDelay;
	}

	public void setMailBody(final String mailBody) {
		this.mailBody = mailBody;
	}

	public void setFileAttachmentProcessor(final Processor fileAttachmentProcessor) {
		this.fileAttachmentProcessor = fileAttachmentProcessor;
	}

}