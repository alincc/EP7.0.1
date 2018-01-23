package com.elasticpath.repo.camelimport.processors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.Exchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.repo.camelimport.CamelImportConstants;
import com.elasticpath.repo.camelimport.services.ImporterService;
/**
 * Unit tests for ExecuteImportExportProcessor.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExecuteImportExportProcessorTest {

	private static final String DEST_DIR = "C:/destDir/";

	private static final String CHANGESET_GUID = "01234-012";

	@Mock
	private ImporterService importerService;
	
	@Mock
	private Exchange exchange;
	
	private final ExecuteImportExportProcessor processor = new ExecuteImportExportProcessor();
	
	@Before
	public void setUp() {
		processor.setImporterService(importerService);
	}

	@Test
	public void testHappyPath() throws Exception {
		when(exchange.getProperty(CamelImportConstants.IMPORT_DATA_DIR_PATH)).thenReturn(DEST_DIR);
		when(exchange.getProperty(CamelImportConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID);
		processor.process(exchange);
		verify(importerService, times(1)).executeImport(DEST_DIR, CHANGESET_GUID);
	}

	@Test
	public void testNoChangeSet() throws Exception {
		when(exchange.getProperty(CamelImportConstants.IMPORT_DATA_DIR_PATH)).thenReturn(DEST_DIR);
		when(exchange.getProperty(CamelImportConstants.CHANGESET_GUID)).thenReturn(null);
		processor.process(exchange);
		verify(importerService, times(1)).executeImport(DEST_DIR);
	}

	public void setImporterService(final ImporterService importerService) {
		this.importerService = importerService;
	}
}
