package com.elasticpath.repo.camelimport.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.elasticpath.repo.camelimport.CamelImportConstants;
import com.elasticpath.repo.camelimport.services.ImporterService;

/**
 * Processor to execute import of catalog feed.
 */
public class ExecuteImportExportProcessor implements Processor {

	private ImporterService importerService;

	@Override
	public void process(final Exchange exchange) throws Exception {
		String dataDir = (String) exchange.getProperty(CamelImportConstants.IMPORT_DATA_DIR_PATH);
		String changeSetGuid = (String) exchange.getProperty(CamelImportConstants.CHANGESET_GUID);
		if (changeSetGuid == null) {
			importerService.executeImport(dataDir);
		} else {
			importerService.executeImport(dataDir, changeSetGuid);
		}
	}

	public void setImporterService(final ImporterService importerService) {
		this.importerService = importerService;
	}
}