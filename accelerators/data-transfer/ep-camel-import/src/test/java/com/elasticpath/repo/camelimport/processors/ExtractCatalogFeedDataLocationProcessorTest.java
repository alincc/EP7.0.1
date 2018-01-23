package com.elasticpath.repo.camelimport.processors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.file.GenericFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.repo.camelimport.CamelImportConstants;

/**
 * Tests for ExtractCatalogFeedDataLocationProcessor.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExtractCatalogFeedDataLocationProcessorTest {

	@Mock
	private Exchange exchange;

	@Mock
	private Message inMsg;

	@Mock
	private GenericFile<File> exchangeInBody;

	private final ExtractCatalogFeedDataLocationProcessor processor = createStubbedProcessor();

	protected ExtractCatalogFeedDataLocationProcessor createStubbedProcessor() {
		return new ExtractCatalogFeedDataLocationProcessor() {
			@Override
			protected void validateDirectoryExists(final File catalogFeedDataDir) throws FileNotFoundException {
				// no validation
			}
		};
	}

	/**
	 * Sets up.
	 */
	@Before
	public final void setUp() {
		when(exchange.getIn()).thenReturn(inMsg);
		when(inMsg.getBody(GenericFile.class)).thenReturn(exchangeInBody);
	}

	/**
	 * Test to ensure properties are set.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public final void testPropertiesSet() throws Exception {
		final String dotNewFilePath = "/the/file/path/new.x.new.y.new";
		final String catalogFeedDataDirPath = "/the/file/path/new.x.new.y";

		when(exchangeInBody.getFile()).thenReturn(new File(dotNewFilePath));

		processor.process(exchange);

		verify(exchange, times(1)).setProperty(CamelImportConstants.IMPORT_DATA_DIR_PATH,
				new File(catalogFeedDataDirPath).getPath());
	}
}
