package com.elasticpath.extensions.messagehandler.camelimport.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/**
 * Builds a manifest file from the catalog feed data.
 */
@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
public class ConsumeCatalogServiceProcessor implements Processor {

	private static final Logger LOG = Logger.getLogger(ConsumeCatalogServiceProcessor.class);

	@Override
	public void process(final Exchange exchange) throws Exception {
		LOG.info("Reading/Consuming external data/file with HTTP end point (Shaw.ca)");
		// TODO: Write business logic. For now dummy code has been written for poc purpose.
	}
}
