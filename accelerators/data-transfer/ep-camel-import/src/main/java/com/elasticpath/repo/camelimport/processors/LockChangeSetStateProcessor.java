package com.elasticpath.repo.camelimport.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.repo.camelimport.CamelImportConstants;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetService;

/**
 * Finds the given ChangeSet and sets its state to finalized.
 */
public class LockChangeSetStateProcessor implements Processor {

	private ChangeSetManagementService changeSetManagementService;

	private BeanFactory beanFactory;

	@Override
	public void process(final Exchange exchange) throws Exception {
		String changeSetGuid = (String) exchange.getProperty(CamelImportConstants.CHANGESET_GUID);

		if (StringUtils.isBlank(changeSetGuid)) {
			throw new ConfigurationException(String.format("Change set guid does not exist on exchange.", changeSetGuid));
		}
		ChangeSetService changeSetService = beanFactory.getBean(ContextIdNames.CHANGESET_SERVICE);
		// COMMERCE/SYSTEM/CHANGESETS/enable - is false. Hence either change that or hard code to true; 
		if (changeSetService.isChangeSetEnabled()) {
			throw new ConfigurationException("Change sets are not enabled.");
		}

		ChangeSetLoadTuner changeSetLoadTuner = beanFactory.getBean(ContextIdNames.CHANGESET_LOAD_TUNER);
		changeSetLoadTuner.setLoadingMemberObjects(false);
		changeSetLoadTuner.setLoadingMemberObjectsMetadata(false);

		// This route only creates OPEN changesets so if we encounter one that is LOCKED,
		// then it means someone else is concurrently modifying the changeset. In that case
		// we will fail-fast.
		ChangeSet changeSet = changeSetManagementService.get(changeSetGuid, changeSetLoadTuner);
		if (!ChangeSetStateCode.OPEN.equals(changeSet.getStateCode())) {
			throw new EpSystemException("Attempted to transition the changeset with GUID [" + changeSetGuid
					+ "] from OPEN to LOCKED, but encountered the changeset in state [" + changeSet.getStateCode() + "].");
		}
		changeSetManagementService.updateState(changeSetGuid, ChangeSetStateCode.LOCKED, changeSetLoadTuner);
	}

	public void setChangeSetManagementService(final ChangeSetManagementService changeSetManagementService) {
		this.changeSetManagementService = changeSetManagementService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
