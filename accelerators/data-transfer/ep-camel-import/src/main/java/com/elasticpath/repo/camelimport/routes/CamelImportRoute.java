/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.repo.camelimport.routes;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;

import com.elasticpath.importexport.common.util.Message;

/**
 * Route to consume catalog feed from a shared file location.
 */
public class CamelImportRoute extends SpringRouteBuilder {

	private Endpoint sourceEndpoint;

	private Processor extractCatalogFeedDataLocationProcessor;

	private Processor createManifestProcessor;

	private Processor createChangeSetProcessor;

	private Processor executeImportExportProcessor;

	private Processor lockChangeSetStateProcessor;

	private Processor perExecutionLoggingProcessor;

	private boolean useChangesets;

	private ObjectRenderer messageRenderer;

	@SuppressWarnings("rawtypes")
	@Override
	public void configure() throws Exception {

		// Configure IE-specific error logging should be done before the route executes an import
		configureLogRendering();
		
		/*
    	 * #### Using OnException block ####
    	 * The OnException block is written as a separate block from the routes.
    	 */
        onException(Exception.class).process(new Processor() {

            public void process(Exchange exchange) throws Exception {
            	// Write custom code.
                handleImportError();
            }
        }).log("Received body ${body}").handled(true);

        log.debug("CamelImportRoute.sourceEndpoint=" + sourceEndpoint.getEndpointUri());

		//ProcessorDefinition definition = from(sourceEndpoint);
		//ProcessorDefinition definition = from("file:D:/elasticpath/extensions/importexport/ext-importexport-cli/src/main/filtered-resources/target/import/data");
		ProcessorDefinition definition = from("file:D:/elasticpath/extensions/importexport/ext-importexport-cli/src/main/filtered-resources/target/import/data");
		definition.process(extractCatalogFeedDataLocationProcessor);
		definition.process(perExecutionLoggingProcessor);
		definition.process(createManifestProcessor);

		if (useChangesets) {
			log.info("Changeset is enabled.");
			System.out.println("Changeset is enabled..");
			definition.process(createChangeSetProcessor);
			definition.process(executeImportExportProcessor);
			definition.process(lockChangeSetStateProcessor);
		} else {
			log.info("Changeset is disabled.");
			System.out.println("Changeset is disabled..");
			definition.process(executeImportExportProcessor);
		}
		definition.end();
	}

	/**
	 * Handle exceptions incase any failures happens during import-job execution.
	 *
	 * @throws InterruptedException
	 */
	private void handleImportError() throws InterruptedException {
		// TODO: Write business logic. For now dummy code has been written for poc purpose.
		Thread.sleep(1 * 60 * 1000);
        log.info("Handling exception, while execution of import-job");
	}
	
	private void configureLogRendering() {
		LoggerRepository loggerRepository = LogManager.getLoggerRepository();
		if (loggerRepository instanceof RendererSupport) {
			RendererSupport rendererSupport = (RendererSupport) loggerRepository;
			rendererSupport.setRenderer(Message.class, messageRenderer);
		}
	}

	public void setSourceEndpoint(final Endpoint sourceEndpoint) {
		this.sourceEndpoint = sourceEndpoint;
	}

	public void setExtractCatalogFeedDataLocationProcessor(final Processor extractCatalogFeedDataLocationProcessor) {
		this.extractCatalogFeedDataLocationProcessor = extractCatalogFeedDataLocationProcessor;
	}

	public void setCreateManifestProcessor(final Processor createManifestProcessor) {
		this.createManifestProcessor = createManifestProcessor;
	}

	public void setCreateChangeSetProcessor(final Processor createChangeSetProcessor) {
		this.createChangeSetProcessor = createChangeSetProcessor;
	}

	public void setExecuteImportExportProcessor(final Processor executeImportExportProcessor) {
		this.executeImportExportProcessor = executeImportExportProcessor;
	}

	public void setLockChangeSetStateProcessor(final Processor lockChangeSetStateProcessor) {
		this.lockChangeSetStateProcessor = lockChangeSetStateProcessor;
	}

	public void setUseChangesets(final boolean useChangesets) {
		this.useChangesets = useChangesets;
	}

	public void setPerExecutionLoggingProcessor(final Processor perExecutionLoggingProcessor) {
		this.perExecutionLoggingProcessor = perExecutionLoggingProcessor;
	}

	public void setMessageRenderer(final ObjectRenderer messageRenderer) {
		this.messageRenderer = messageRenderer;
	}
}