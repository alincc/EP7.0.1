package com.elasticpath.repo.camelimport.services.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.enums.OperationEnum;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.importexport.common.ImportExportContextIdNames;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SimpleSummaryLayout;
import com.elasticpath.importexport.common.util.MessageResolver;
import com.elasticpath.importexport.importer.controller.ImportController;
import com.elasticpath.repo.camelimport.services.ImporterService;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;

/**
 * Service methods to execute an import using import export.
 */
@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
public class IEImporterServiceImpl implements ImporterService {

	private static final Logger LOG = Logger.getLogger(IEImporterServiceImpl.class);

	private BeanFactory beanFactory;

	private String importConfigFileName;

	@Override
	public void executeImport(final String dataDir) throws FileNotFoundException, ConfigurationException {
		ImportController controller = beanFactory.getBean(ImportExportContextIdNames.IMPORT_CONTROLLER);
		InputStream configStream = null;
		try {
			configStream = this.getClass().getClassLoader().getResourceAsStream(importConfigFileName);
			controller.loadConfiguration(configStream);
			controller.getImportConfiguration().getRetrievalConfiguration().setSource(dataDir);

			Summary summary = controller.executeImport();
			printResults(summary);
			
			if (controller.failuresExist()) {
				throw new RuntimeException("Failures exist in import.");
			}
		} finally {
			if (configStream != null) {
				try {
					configStream.close();
				} catch (IOException e) {
					LOG.error("IOException closing the configuration file stream", e);
				}
			}
		}

	}

	@Override
	public void executeImport(final String dataDir, final String changesetGuid) throws FileNotFoundException, ConfigurationException {
		if (changesetGuid != null) {
			configureChangesetForImport(changesetGuid);
		}
		executeImport(dataDir);
	}

	private void configureChangesetForImport(final String changeSetGuid) throws ConfigurationException {
		ChangeSetLoadTuner changeSetLoadTuner = beanFactory.getBean(ContextIdNames.CHANGESET_LOAD_TUNER);
		changeSetLoadTuner.setLoadingMemberObjects(false);
		changeSetLoadTuner.setLoadingMemberObjectsMetadata(false);
		ChangeSetManagementService changeSetManagementService = beanFactory.getBean(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);
		ChangeSet changeSet = changeSetManagementService.get(changeSetGuid, changeSetLoadTuner);
		if (changeSet == null) {
			throw new ConfigurationException(String.format("Change set %s does not exist.", changeSetGuid));
		} else if (!changeSetManagementService.isChangeAllowed(changeSetGuid)) {
			throw new ConfigurationException(String.format("Change set %s does not allow changes. It is probably locked or finalized.",
					changeSetGuid));
		}

		ThreadLocalMap<String, Object> metadataMap = beanFactory.getBean("persistenceListenerMetadataMap");
		metadataMap.put("changeSetGuid", changeSetGuid);
		metadataMap.put("changeSetOperation", OperationEnum.OPERATIONAL);
		metadataMap.put("importOperation", OperationEnum.OPERATIONAL);
	}
	
	private void printResults(final Summary summary) {
		MessageResolver messageResolver = beanFactory.getBean("messageResolver");
		final SimpleSummaryLayout layout = new SimpleSummaryLayout();
		layout.setMessageResolver(messageResolver);
		LOG.info(layout.format(summary)); 
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setImportConfigFileName(final String importConfigFileName) {
		this.importConfigFileName = importConfigFileName;
	}

}
