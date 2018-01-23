/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.repo.camelimport.processors;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;

/**
 * Attaches a log file to the email.
 */
public final class FileAttachmentProcessor implements Processor {

	@Override
	public void process(final Exchange exchange) throws Exception {

		String camelFileAbsolutePath = (String) exchange.getIn().getHeader("CamelFileAbsolutePath");
		File camelFile = new File(camelFileAbsolutePath);
		String logFileLocation = FileUtils.readFileToString(camelFile, "UTF-8");

		File log = new File(logFileLocation);
		DataHandler dataHandler = new DataHandler(new FileDataSource(log));
		exchange.getIn().addAttachment(log.getName(), dataHandler);
	}
}