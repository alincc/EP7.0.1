package com.elasticpath.repo.datasync.processors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.repo.datasync.commons.constants.EpExchangeConstants;
import com.elasticpath.repo.datasync.messages.ChangeSetSummaryMessage;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Unit tests for ChangeSetPublishEmailProcessor.
 */
@RunWith(MockitoJUnitRunner.class)
public class ChangeSetPublishEmailProcessorTest {

	private static final String NEW_LINES_CHAR = "\n\n";
	
	private static final String GUID_TITLE = "\nGUID ";
	
	private static final String CHANGESET_GUID = "TEST_CHANGESET_GUID";

	private static final String CHANGESET_NAME = "Test Change Set Name";

	private static final String CMUSER_GUID = "TEST_CMUSER_GUID";

	private static final String SYSTEM_CM_USER_NAME = "SYSTEM";

	private static final String CM_USER_NAME = "TEST_CMUSER_GUID";

	private static final String TEST_SUMMARY_RESULT = "Test summary result.";

	private static final String TEST_CUSTOMER_EMAIL = "customer.test@elasticpath.com";

	private static final String TEST_REPLY_EMAIL = "reply.test@elasticpath.com";

	private static final String HEADER_TO = "to";

	private static final String HEADER_FROM = "from";

	private static final String HEADER_SUBJECT = "subject";

	private ChangeSetPublishEmailProcessor processor;

	@Mock
	private Exchange exchange;

	@Mock
	private ChangeSet changeSet;

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private ChangeSetLoadTuner changeSetLoadTuner;

	@Mock
	private ChangeSetManagementService changeSetManagementService;

	@Mock
	private CmUser cmUser;

	@Mock
	private CmUserService cmUserService;

	@Mock
	private Message inMsg;

	@Mock
	private ChangeSetSummaryMessage changeSetSummaryMessage;

	/**
	 * Set up test.
	 */
	@Before
	public final void setUp() {
		processor = new ChangeSetPublishEmailProcessor();
		processor.setBeanFactory(beanFactory);
		processor.setChangeSetManagementService(changeSetManagementService);
		processor.setMailFrom(TEST_REPLY_EMAIL);
		processor.setCmUserService(cmUserService);

		when(changeSetManagementService.get(CHANGESET_GUID, changeSetLoadTuner)).thenReturn(changeSet);
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID);
		when(exchange.getIn()).thenReturn(inMsg);
		when(beanFactory.getBean(ContextIdNames.CHANGESET_LOAD_TUNER)).thenReturn(changeSetLoadTuner);
		when(changeSet.getCreatedByUserGuid()).thenReturn(CMUSER_GUID);
		when(changeSet.getName()).thenReturn(CHANGESET_NAME);
		when(cmUserService.findByGuid(CMUSER_GUID)).thenReturn(cmUser);
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_RESULT_MESSAGE)).thenReturn(changeSetSummaryMessage);
		when(changeSetSummaryMessage.getDSTSummary()).thenReturn(TEST_SUMMARY_RESULT);
		when(cmUser.getEmail()).thenReturn(TEST_CUSTOMER_EMAIL);
	}

	/**
	 * Test happy path through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public final void testHappyPath() throws Exception {
		when(cmUser.getUserName()).thenReturn(CM_USER_NAME);
		when(changeSetSummaryMessage.isSuccess()).thenReturn(Boolean.TRUE);
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_STATUS_UPDATE_FAILURE)).thenReturn(null);

		processor.process(exchange);

		verify(exchange, times(1)).setProperty(EpExchangeConstants.DST_SEND_EMAIL_STATUS, Boolean.TRUE);
		verify(inMsg, times(1)).setHeader(HEADER_TO, TEST_CUSTOMER_EMAIL);
		verify(inMsg, times(1)).setHeader(HEADER_FROM, TEST_REPLY_EMAIL);
		verify(inMsg, times(1)).setHeader(HEADER_SUBJECT, ChangeSetPublishEmailProcessor.EMAIL_ALL_SUCCESS + CHANGESET_NAME);
		verify(inMsg, times(1)).setBody(ChangeSetPublishEmailProcessor.EMAIL_ALL_SUCCESS + CHANGESET_NAME + GUID_TITLE + "["
										+ CHANGESET_GUID + "]" + NEW_LINES_CHAR + TEST_SUMMARY_RESULT);
	}

	/**
	 * Test publish success and finalize failure through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public final void testPublishSuccessFinalizeFailure() throws Exception {
		when(cmUser.getUserName()).thenReturn(CM_USER_NAME);
		when(changeSetSummaryMessage.isSuccess()).thenReturn(Boolean.TRUE);
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_STATUS_UPDATE_FAILURE)).thenReturn(Boolean.TRUE);

		processor.process(exchange);

		verify(exchange, times(1)).setProperty(EpExchangeConstants.DST_SEND_EMAIL_STATUS, Boolean.TRUE);
		verify(inMsg, times(1)).setHeader(HEADER_TO, TEST_CUSTOMER_EMAIL);
		verify(inMsg, times(1)).setHeader(HEADER_FROM, TEST_REPLY_EMAIL);
		verify(inMsg, times(1)).setHeader(HEADER_SUBJECT, ChangeSetPublishEmailProcessor.EMAIL_PUBLISH_SUCCESS_FINALIZE_FAILURE + CHANGESET_NAME);
		verify(inMsg, times(1)).setBody(ChangeSetPublishEmailProcessor.EMAIL_PUBLISH_SUCCESS_FINALIZE_FAILURE + CHANGESET_NAME + GUID_TITLE + "["
										+ CHANGESET_GUID + "]" + NEW_LINES_CHAR + TEST_SUMMARY_RESULT);
	}

	/**
	 * Test publish failure through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public final void testPublishFailure() throws Exception {
		when(cmUser.getUserName()).thenReturn(CM_USER_NAME);
		when(changeSetSummaryMessage.isSuccess()).thenReturn(Boolean.TRUE);
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_STATUS_UPDATE_FAILURE)).thenReturn(Boolean.FALSE);

		processor.process(exchange);

		verify(exchange, times(1)).setProperty(EpExchangeConstants.DST_SEND_EMAIL_STATUS, Boolean.TRUE);
		verify(inMsg, times(1)).setHeader(HEADER_TO, TEST_CUSTOMER_EMAIL);
		verify(inMsg, times(1)).setHeader(HEADER_FROM, TEST_REPLY_EMAIL);
		verify(inMsg, times(1)).setHeader(HEADER_SUBJECT, ChangeSetPublishEmailProcessor.EMAIL_PUBLISH_FAILURE + CHANGESET_NAME);
		verify(inMsg, times(1)).setBody(ChangeSetPublishEmailProcessor.EMAIL_PUBLISH_FAILURE + CHANGESET_NAME + GUID_TITLE + "["
										+ CHANGESET_GUID + "]" + NEW_LINES_CHAR + TEST_SUMMARY_RESULT);
	}

	/**
	 * Test no email happy path through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public final void testNoEmailHappyPath() throws Exception {
		when(cmUser.getUserName()).thenReturn(SYSTEM_CM_USER_NAME);

		processor.process(exchange);

		verify(exchange, times(0)).setProperty(EpExchangeConstants.DST_SEND_EMAIL_STATUS, Boolean.TRUE);
		verify(exchange, times(0)).getIn();
		verify(inMsg, times(0)).setHeader(any(String.class), any(String.class));
		verify(inMsg, times(0)).setBody(any(String.class));
	}

}
