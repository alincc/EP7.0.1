/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.messagehandler.camelimport.routes;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.log4j.Logger;

/**
 * This Apache Camel Router is part of Extended EP Message Handler.
 * This Apache Camel Router is responsible for external data/file polling with HTTP end point (Shaw.ca)
 * to invoke the catalog service and read the response using JAXB. Invoke corresponding processors and converters.
 */
@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
public class ShawCamelRouteBuilder extends RouteBuilder {

    private static final Logger LOG = Logger.getLogger(ShawCamelRouteBuilder.class);

    //private Endpoint incomingEndpoint;
    private Processor consumeCatalogServiceProcessor;
	
	@Override
	public void configure() throws Exception {
        // TODO: Write business logic. For now dummy code has been written for poc purpose.

        //LOG.debug("incomingEndpoint :: " + incomingEndpoint);
        LOG.debug("This Apache Camel Router is responsible to read external data/file from HTTP end point.");

        // Read external data/file from incoming HTTP end point
        //ProcessorDefinition<?> definition = from(incomingEndpoint);
        /*
        ProcessorDefinition<?> definition = from("timer:service/topics?period=2000")
                .setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.POST))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("http4://localhost:9000/service/topics/100?throwExceptionOnFailure=false&authUsername=admin&authPassword=admin").streamCaching();
        */
        ProcessorDefinition<?> definition = from("file:D:/tmp/inputFolder/catalog?noop=true");

        definition.process(consumeCatalogServiceProcessor);
        definition.to("file:D:/elasticpath/extensions/importexport/ext-importexport-cli/src/main/filtered-resources/target/import/data");
        definition.end();

	}
	
	/*public Endpoint getIncomingEndpoint() {
		return incomingEndpoint;
	}

	public void setIncomingEndpoint(final Endpoint incomingEndpoint) {
		this.incomingEndpoint = incomingEndpoint;
	}*/

    public Processor getConsumeCatalogServiceProcessor() {
        return consumeCatalogServiceProcessor;
    }

    public void setConsumeCatalogServiceProcessor(final Processor consumeCatalogServiceProcessor) {
        this.consumeCatalogServiceProcessor = consumeCatalogServiceProcessor;
    }
}
