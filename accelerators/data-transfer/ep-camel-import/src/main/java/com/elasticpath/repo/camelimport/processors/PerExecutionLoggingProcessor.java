/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.repo.camelimport.processors;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.spi.UnitOfWork;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.apache.log4j.spi.Filter;

import com.elasticpath.commons.util.impl.DateUtils;
import com.elasticpath.repo.camelimport.CamelImportConstants;

/**
 * Logs the execution of a route's processing steps to a new file.
 */
@SuppressWarnings("PMD.MoreThanOneLogger")
public class PerExecutionLoggingProcessor implements Processor, Synchronization {

	private static final Logger LOG = Logger.getLogger(PerExecutionLoggingProcessor.class);

	private Filter logFilter;

	@Override
	public void process(final Exchange exchange) throws Exception {

		// Create log file name
		generateLogFileName(exchange);

		// Add Synchronization to remove the logging appender when the route is complete
		UnitOfWork unitOfWork = exchange.getUnitOfWork();
		unitOfWork.addSynchronization(this);

		// Add a logging appender for this route execution
		addLogAppender(exchange);
	}

	/**
	 * Adds an appender to the root logger.
	 * 
	 * @param exchange The exchange
	 * @throws IOException The file exception
	 */
	protected void addLogAppender(final Exchange exchange) throws IOException {

		Appender appender = createAppender(exchange);

		Logger rootLogger = Logger.getRootLogger();
		rootLogger.addAppender(appender);
		exchange.getIn().setHeader(CamelImportConstants.APPENDER, appender);
	}

	/**
	 * Creates an appender that filters all non Import-Export tool related messages.
	 * 
	 * @param exchange The exchange
	 * @return The appender
	 * @throws IOException The file exception
	 */
	protected Appender createAppender(final Exchange exchange) throws IOException {

		TTCCLayout layout = new TTCCLayout();
		layout.setDateFormat(AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT, TimeZone.getDefault());

		String logFilePath = (String) exchange.getIn().getHeader(CamelImportConstants.LOG_FILE_PATH);

		FileAppender appender = new FileAppender(layout, logFilePath);
		appender.setImmediateFlush(true);
		appender.addFilter(logFilter);
		return appender;
	}

	/**
	 * Generates the filename for the log with a timestamp.
	 * 
	 * @param exchange The exchange
	 */
	protected void generateLogFileName(final Exchange exchange) {
		String dataDirPath = (String) exchange.getProperty(CamelImportConstants.IMPORT_DATA_DIR_PATH);
		String logDirPath = dataDirPath + File.separator + "log"; // Custom code
		String dateString = DateUtils.toFormattedString(new Date());
		String logFileName = CamelImportConstants.FILENAME_PREFIX + '-' + dateString + CamelImportConstants.FILE_EXTENSION;
		String logFilePath = logDirPath + File.separator + logFileName;
		exchange.getIn().setHeader(CamelImportConstants.LOG_FILE_PATH, logFilePath);
	}

	@Override
	public void onComplete(final Exchange exchange) {
		processRouteFinished(exchange, CamelImportConstants.SUCCESS_SUFFIX);
	}

	@Override
	public void onFailure(final Exchange exchange) {
		processRouteFinished(exchange, CamelImportConstants.FAILURE_SUFFIX);
	}

	/**
	 * Common logic for route completion.
	 * 
	 * @param exchange The exchange
	 * @param label The label for the log file
	 */
	protected void processRouteFinished(final Exchange exchange, final String label) {
		removeAppender(exchange);
		try {
			renameLogFile(exchange, label);
			appendLogLocationToFile(exchange);
		} catch (IOException e) {
			LOG.error("Error writing log to file", e);
		}
	}

	/**
	 * Remove the appender we added earlier, and close it.
	 * 
	 * @param exchange The exchange
	 */
	protected void removeAppender(final Exchange exchange) {

		FileAppender appender = (FileAppender) exchange.getIn().getHeader(CamelImportConstants.APPENDER);
		if (appender != null) {
			try {
				Logger rootLogger = Logger.getRootLogger();
				if (rootLogger != null) {
					rootLogger.removeAppender(appender);
				}
				exchange.getIn().removeHeader(CamelImportConstants.APPENDER);
			} finally {
				appender.close();
			}
		}
	}

	/**
	 * Rename log file to signify success or failure.
	 * 
	 * @param exchange The exchange
	 * @param label The label for the log file
	 * @throws IOException when failed to rename log file.
	 */
	protected void renameLogFile(final Exchange exchange, final String label) throws IOException {

		String logFilePath = (String) exchange.getIn().getHeader(CamelImportConstants.LOG_FILE_PATH);

		if (logFilePath != null) {
			String newLogFilePath = logFilePath.substring(0, logFilePath.length() - CamelImportConstants.FILE_EXTENSION.length());
			newLogFilePath = newLogFilePath + '-' + label + CamelImportConstants.FILE_EXTENSION;

			File sourceFile = new File(logFilePath);
			File targetFile = new File(newLogFilePath);
			if (!sourceFile.renameTo(targetFile)) {
				throw new IOException("Could not rename log file to include label: " + label);
			}

			exchange.getIn().setHeader(CamelImportConstants.LOG_FILE_PATH, newLogFilePath);
		}
	}

	/**
	 * Adds the log file location to the camel file. Makes it easier to find the log to attach to the error email.
	 * 
	 * @param exchange The exchange
	 * @throws IOException The file exception
	 */
	protected void appendLogLocationToFile(final Exchange exchange) throws IOException {
		String logFilePath = (String) exchange.getIn().getHeader(CamelImportConstants.LOG_FILE_PATH);
		String camelFileAbsolutePath = (String) exchange.getIn().getHeader("CamelFileAbsolutePath");
		File camelFile = new File(camelFileAbsolutePath);
		FileUtils.writeStringToFile(camelFile, logFilePath, "UTF-8");
	}

	public void setLogFilter(final Filter logFilter) {
		this.logFilter = logFilter;
	}
}