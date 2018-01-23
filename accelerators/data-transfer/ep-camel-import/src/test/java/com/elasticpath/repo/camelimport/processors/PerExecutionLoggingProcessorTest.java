/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.repo.camelimport.processors;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.spi.UnitOfWork;
import org.apache.log4j.Appender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.repo.camelimport.CamelImportConstants;

/**
 * Tests for PerExecutionLoggingProcessor.
 */
@RunWith(MockitoJUnitRunner.class)
public class PerExecutionLoggingProcessorTest {

	private static final String DOT_NEW_FILE = "/the/file/path/new.x.new.y.new";

	@Mock
	private Exchange exchange;

	@Mock
	private Message inMsg;

	@Mock
	private UnitOfWork unitOfWork;

	@Mock
	private Appender appender;

	@Mock
	private GenericFile<File> exchangeInBody;

	private final PerExecutionLoggingProcessor processor = createStubbedProcessor();

	/**
	 * Create the stubbed processor.
	 * 
	 * @return the stubbed processor
	 */
	protected PerExecutionLoggingProcessor createStubbedProcessor() {
		return new PerExecutionLoggingProcessor() {
			@Override
			protected Appender createAppender(final Exchange exchange) throws IOException {
				return appender;
			}
		};
	}

	/**
	 * Sets up.
	 */
	@Before
	public final void setUp() {
		when(exchange.getUnitOfWork()).thenReturn(unitOfWork);
		when(exchange.getIn()).thenReturn(inMsg);
		when(inMsg.getBody(GenericFile.class)).thenReturn(exchangeInBody);
		when(inMsg.getHeader(CamelImportConstants.IMPORT_DATA_DIR_PATH)).thenReturn(DOT_NEW_FILE);
		when(exchangeInBody.getFile()).thenReturn(new File(DOT_NEW_FILE));
	}

	/**
	 * Test to ensure headers are set.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public final void testHeadersSet() throws Exception {
		processor.process(exchange);
		verify(inMsg, times(1)).setHeader(eq(CamelImportConstants.LOG_FILE_PATH), any(String.class));
		verify(inMsg, times(1)).setHeader(eq(CamelImportConstants.APPENDER), any(Appender.class));
	}
}
