package com.elasticpath.repo.datasync.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.repo.datasync.commons.constants.EpExchangeConstants;
import com.elasticpath.repo.datasync.messages.ChangeSetSummaryMessage;
import com.elasticpath.repo.datasync.tools.exception.ChangeSetNotFoundException;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Takes in a DstSummary Message and Generates the E-mail Fields for the ChangeSet Email.
 */
public class ChangeSetPublishEmailProcessor implements Processor {

	/** Publish failure. */
	public static final String EMAIL_PUBLISH_FAILURE = "Change Set Publishing FAILED for: ";

	/** Publish success and finalize failure. */
	public static final String EMAIL_PUBLISH_SUCCESS_FINALIZE_FAILURE = "Change Set Publishing SUCCEEDED and Finalization Update FAILED for: ";

	/** Publish and finalize success. */
	public static final String EMAIL_ALL_SUCCESS = "Change Set Publishing SUCCEEDED for: ";
	
	private static final String NEW_LINE = "\n";
	
	private static final String GUID_TITLE_START = "GUID [";
	
	private static final String GUID_TITLE_END = "]";

	private static final String SUBJECT_HEADER = "subject";

	private static final String CM_USER_NAME = "SYSTEM";

	private ChangeSetManagementService changeSetManagementService;

	private BeanFactory beanFactory;

	private CmUserService cmUserService;

	private String mailFrom;

	@Override
	public void process(final Exchange exchange) throws Exception {
		String changeSetGuid = getChangeSetGuid(exchange);
		ChangeSet changeSet = findChangeSet(changeSetGuid);
		CmUser cmUser = findChangeSetCreator(changeSet);
		String changeSetName = changeSet.getName();

		if (shouldEmailUser(exchange, cmUser)) {

			exchange.setProperty(EpExchangeConstants.DST_SEND_EMAIL_STATUS, Boolean.TRUE);

			ChangeSetSummaryMessage resultMessage = (ChangeSetSummaryMessage) exchange.getProperty(EpExchangeConstants.CHANGESET_RESULT_MESSAGE);
			String dstSummary = resultMessage.getDSTSummary();
			Boolean finalizeFailure = (Boolean) exchange.getProperty(EpExchangeConstants.CHANGESET_STATUS_UPDATE_FAILURE);
			exchange.getIn().setHeader("to", StringUtils.trimToEmpty(cmUser.getEmail()));
			exchange.getIn().setHeader("from", StringUtils.trimToEmpty(mailFrom));

			if (resultMessage.isSuccess() && finalizeFailure == null) {
				exchange.getIn().setHeader(SUBJECT_HEADER, EMAIL_ALL_SUCCESS + changeSetName);
				exchange.getIn().setBody(getEmailBody(EMAIL_ALL_SUCCESS, changeSetName, changeSetGuid, dstSummary));

			} else if (resultMessage.isSuccess() && finalizeFailure != null && finalizeFailure) {
				exchange.getIn().setHeader(SUBJECT_HEADER, EMAIL_PUBLISH_SUCCESS_FINALIZE_FAILURE + changeSetName);
				exchange.getIn().setBody(getEmailBody(EMAIL_PUBLISH_SUCCESS_FINALIZE_FAILURE, changeSetName, changeSetGuid, dstSummary));

			} else {
				exchange.getIn().setHeader(SUBJECT_HEADER, EMAIL_PUBLISH_FAILURE + changeSetName);
				exchange.getIn().setBody(getEmailBody(EMAIL_PUBLISH_FAILURE, changeSetName, changeSetGuid, dstSummary));
			}
		}
	}

	private String getEmailBody(final String statusMessage, final String changeSetName, final String changeSetGuid, final String dstSummary) {
		StringBuilder emailBody = new StringBuilder(statusMessage).append(changeSetName);
		emailBody.append(NEW_LINE).append(GUID_TITLE_START).append(changeSetGuid).append(GUID_TITLE_END);
		emailBody.append(NEW_LINE).append(NEW_LINE).append(dstSummary);
		return emailBody.toString();
	}

	/**
	 * Returns true if an e-mail should be sent and sets the email property to be used for the to address.
	 * 
	 * @param exchange the exchange
	 * @param cmUser the cmUser
	 * @return true if email will be sent to the user
	 */
	protected boolean shouldEmailUser(final Exchange exchange, final CmUser cmUser) {
		if (StringUtils.equals(CM_USER_NAME, cmUser.getUserName())) {
			return false;
		}
		return true;
	}

	private ChangeSet findChangeSet(final String changeSetGuid) {
		ChangeSetLoadTuner changeSetLoadTuner = beanFactory.getBean(ContextIdNames.CHANGESET_LOAD_TUNER);
		changeSetLoadTuner.setLoadingMemberObjects(false);
		changeSetLoadTuner.setLoadingMemberObjectsMetadata(false);

		ChangeSet changeSet = changeSetManagementService.get(changeSetGuid, changeSetLoadTuner);
		if (changeSet == null) {
			throw new ChangeSetNotFoundException("Change set not found", changeSetGuid);
		}
		return changeSet;
	}

	private CmUser findChangeSetCreator(final ChangeSet changeSet) {
		String creatorGuid = changeSet.getCreatedByUserGuid();

		return cmUserService.findByGuid(creatorGuid);
	}

	/**
	 * Returns the guid on the Change set on the exchange if there is one.
	 * 
	 * @param exchange the exchange
	 * @return string the guid of the Change set on the exchange
	 */
	protected String getChangeSetGuid(final Exchange exchange) {
		String changeSetGuid = (String) exchange.getProperty(EpExchangeConstants.CHANGESET_GUID);

		if (StringUtils.isBlank(changeSetGuid)) {
			throw new ChangeSetNotFoundException("Change set not found", changeSetGuid);
		}

		return changeSetGuid;
	}

	public void setChangeSetManagementService(final ChangeSetManagementService changeSetManagementService) {
		this.changeSetManagementService = changeSetManagementService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setCmUserService(final CmUserService cmUserService) {
		this.cmUserService = cmUserService;
	}

	public void setMailFrom(final String mailFrom) {
		this.mailFrom = mailFrom;
	}
}
