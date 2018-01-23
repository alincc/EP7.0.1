package com.elasticpath.repo.datasync.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.repo.datasync.commons.constants.EpExchangeConstants;
import com.elasticpath.repo.datasync.messages.ChangeSetSummaryMessage;
import com.elasticpath.repo.datasync.messages.impl.ChangeSetSummaryMessageImpl;
import com.elasticpath.repo.datasync.tools.exception.DataSyncPublishException;
import com.elasticpath.repo.datasync.tools.exception.UnrecoverableMessageException;
import com.elasticpath.repo.datasync.tools.launcher.DatasyncExecuter;
import com.elasticpath.repo.datasync.tools.launcher.DatasyncSummary;

/**
 * Takes in a changeset Guid and uses the DatasyncLauncher to publish the change set.
 */
public class PublishChangeSetProcessor implements Processor {

	private static final Logger LOG = Logger.getLogger(PublishChangeSetProcessor.class);
	
	private DatasyncExecuter datasyncExecuter;

	@Override
	public void process(final Exchange exchange) throws Exception {

		exchange.setProperty(EpExchangeConstants.DST_PUBLISH_SUCCESS, Boolean.TRUE);

		String changeSetGuid = (String) exchange.getProperty(EpExchangeConstants.CHANGESET_GUID);

		if (StringUtils.isBlank(changeSetGuid)) {
			throw new UnrecoverableMessageException("The change set GUID does not exist");
		}

		// Execute the data sync tool
		DatasyncSummary resultSummary = getDatasyncExecuter().execute(changeSetGuid);

		// Process the results of the DST 
		String processSummary = createProcessSummary(resultSummary);
		boolean success = !resultSummary.hasErrors(); 
		ChangeSetSummaryMessage changeSetSummaryMessage = createChangeSetSummaryMessage(processSummary, success);

		exchange.setProperty(EpExchangeConstants.CHANGESET_RESULT_MESSAGE, changeSetSummaryMessage);

		if (!success) {
			exchange.setProperty(EpExchangeConstants.DST_PUBLISH_SUCCESS, Boolean.FALSE);
			throw new DataSyncPublishException("An error has occurred publishing change set: " + changeSetGuid);
		}
	}

	/**
	 * Instantiates ChangeSetSummaryMessageImpl.
	 * 
	 * @param processSummary the DST summary
	 * @param success if the DST was successful or not
	 * @return a new ChangeSetSummaryMessageImpl
	 */
	protected ChangeSetSummaryMessageImpl createChangeSetSummaryMessage(final String processSummary, final boolean success) {
		return new ChangeSetSummaryMessageImpl(success, processSummary);
	}

	/**
	 * Instantiates a DatasyncLauncher.
	 * @return a new DatasyncLauncher
	 */
	protected DatasyncExecuter getDatasyncExecuter() {
		return datasyncExecuter;
	}
	
	public void setDatasyncExecuter(final DatasyncExecuter datasyncExecuter) {
		this.datasyncExecuter = datasyncExecuter;
	}
	
	/**
	 * Creates a summary report from the DST result summary.
	 * @param summary the DST summary
	 * @return list of the errors or success results
	 */
	protected String createProcessSummary(final DatasyncSummary summary) {
		String processSummary = "";
		if (summary.hasErrors()) {
			LOG.error("DataSync Errors for changeset with GUID " + summary.getChangesetGuid()
					+ summary.getErrorMessage());
			processSummary = summary.getErrorMessage();			
		} else {
			// Do not send the success messages in an email. only log them.
			LOG.info("DataSync Succeeded for changeset with GUID " + summary.getChangesetGuid()
					+ summary.getSummaryMessage());
		}
		return processSummary;
	}
}