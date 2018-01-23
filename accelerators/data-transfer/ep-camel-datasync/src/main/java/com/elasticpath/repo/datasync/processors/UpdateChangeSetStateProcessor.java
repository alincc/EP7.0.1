package com.elasticpath.repo.datasync.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.repo.datasync.commons.constants.EpExchangeConstants;
import com.elasticpath.repo.datasync.tools.exception.UpdateChangeSetStatusException;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetService;

/**
 * Finds the given ChangeSet and sets its state to finalized.
 */
public class UpdateChangeSetStateProcessor implements Processor {

	private ChangeSetManagementService changeSetManagementService;

	private BeanFactory beanFactory;

	private ChangeSetStateCode updateChangeSetState;

	@Override
	public void process(final Exchange exchange) throws Exception {
		exchange.setProperty(EpExchangeConstants.CHANGESET_STATUS_UPDATE_FAILURE, Boolean.TRUE);
		String changeSetGuid = (String) exchange.getProperty(EpExchangeConstants.CHANGESET_GUID);

		if (StringUtils.isBlank(changeSetGuid)) {
			throw new UpdateChangeSetStatusException(String.format("Change set guid does not exist on exchange.", changeSetGuid));
		}
		ChangeSetService changeSetService = beanFactory.getBean(ContextIdNames.CHANGESET_SERVICE);
		if (!changeSetService.isChangeSetEnabled()) {
			throw new UpdateChangeSetStatusException("Change sets are not enabled.");
		}

		ChangeSetLoadTuner changeSetLoadTuner = beanFactory.getBean(ContextIdNames.CHANGESET_LOAD_TUNER);
		changeSetLoadTuner.setLoadingMemberObjects(false);
		changeSetLoadTuner.setLoadingMemberObjectsMetadata(false);

		// This route expects READY_TO_PUBLISH changesets so if we encounter one that is another status,
		// then it means someone else is concurrently modifying the changeset. In that case
		// we will fail-fast.
		ChangeSet changeSet = changeSetManagementService.get(changeSetGuid, changeSetLoadTuner);
		if (!ChangeSetStateCode.READY_TO_PUBLISH.equals(changeSet.getStateCode())) {
			throw new EpSystemException("Attempted to transition the changeset with GUID [" + changeSetGuid + "] from READY_TO_PUBLISH to "
					+ updateChangeSetState + ", but encountered the changeset in state [" + changeSet.getStateCode() + "].");
		}

		try {
			changeSetManagementService.updateState(changeSetGuid, updateChangeSetState, changeSetLoadTuner);
		} catch (EpServiceException e) {
			throw new UpdateChangeSetStatusException("Updating the changeset status failed", e);
		}
		exchange.removeProperty(EpExchangeConstants.CHANGESET_STATUS_UPDATE_FAILURE);
	}

	public void setChangeSetManagementService(final ChangeSetManagementService changeSetManagementService) {
		this.changeSetManagementService = changeSetManagementService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setUpdateChangeSetState(final ChangeSetStateCode updateChangeSetState) {
		this.updateChangeSetState = updateChangeSetState;
	}
}
