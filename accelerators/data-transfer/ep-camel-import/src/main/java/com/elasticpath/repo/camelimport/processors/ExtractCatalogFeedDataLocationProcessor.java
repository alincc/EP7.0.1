package com.elasticpath.repo.camelimport.processors;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;

import com.elasticpath.repo.camelimport.CamelImportConstants;

/**
 * Extracts catalog feed data path into exchange properties.
 */
public class ExtractCatalogFeedDataLocationProcessor implements Processor {

	@Override
	public void process(final Exchange exchange) throws Exception {
		GenericFile<?> body = exchange.getIn().getBody(GenericFile.class);
		File dotNewFile = (File) body.getFile();

		String catalogFeedDataDirName = dotNewFile.getName().replaceAll("\\.new$", "");
		//File catalogFeedDataDir = new File(dotNewFile.getParentFile(), catalogFeedDataDirName);
		File catalogFeedDataDir = new File(dotNewFile.getParentFile(), "\\");

		validateDirectoryExists(catalogFeedDataDir);

		exchange.setProperty(CamelImportConstants.IMPORT_DATA_DIR_PATH, catalogFeedDataDir.getPath());
	}

	/**
	 * Validate directory exists. Throws {@code FileNotFoundException} if not.
	 *
	 * @param catalogFeedDataDir the catalog feed data directory
	 * @throws FileNotFoundException the file not found exception
	 */
	protected void validateDirectoryExists(final File catalogFeedDataDir) throws FileNotFoundException {
		if (!catalogFeedDataDir.exists()) {
			throw new FileNotFoundException(String.format("Directory does not exist", catalogFeedDataDir.getPath()));
		}
		if (!catalogFeedDataDir.isDirectory()) {
			throw new FileNotFoundException(String.format("Not a directory", catalogFeedDataDir.getPath()));
		}
	}

}
