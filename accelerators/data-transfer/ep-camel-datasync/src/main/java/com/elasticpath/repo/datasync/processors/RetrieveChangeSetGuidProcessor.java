package com.elasticpath.repo.datasync.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.messaging.impl.EventMessageImpl;
import com.elasticpath.repo.datasync.commons.constants.EpExchangeConstants;
import com.elasticpath.repo.datasync.tools.exception.UnrecoverableMessageException;

/**
 * Processor to retrieve the changeset uid from exchange.
 */
public class RetrieveChangeSetGuidProcessor implements Processor {

	@Override
	public void process(final Exchange exchange) throws Exception {

		EventMessageImpl changeSetMsg = exchange.getIn().getBody(EventMessageImpl.class);
		String changeSetGuid = changeSetMsg.getGuid();

		if (StringUtils.isBlank(changeSetGuid)) {
			throw new UnrecoverableMessageException("The change set GUID does not exist");
		}

		exchange.setProperty(EpExchangeConstants.CHANGESET_GUID, changeSetGuid);
	}

}
