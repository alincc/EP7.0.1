package com.elasticpath.repo.datasync.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.Exchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.repo.datasync.commons.constants.EpExchangeConstants;
import com.elasticpath.repo.datasync.messages.impl.ChangeSetSummaryMessageImpl;
import com.elasticpath.repo.datasync.tools.exception.DataSyncPublishException;
import com.elasticpath.repo.datasync.tools.exception.UnrecoverableMessageException;
import com.elasticpath.repo.datasync.tools.launcher.DatasyncExecuter;
import com.elasticpath.repo.datasync.tools.launcher.DatasyncSummary;

/**
 * Unit tests for PublishChangeSetProcessor.
 */
@RunWith(MockitoJUnitRunner.class)
public class PublishChangeSetProcessorTest {

	private static final String NEW_LINE_CHAR = "\n";

	private static final String CHANGESET_GUID = "TEST_CHANGESET_GUID";

	private static final String SYNC_RESULT_ITEM_STRING = "syncResultItemString";

	private static final String SYNC_ERROR_RESULT_ITEM = "syncErrorResultItem";

	private PublishChangeSetProcessor processor;

	@Mock
	private DatasyncExecuter datasyncExecuter;

	@Mock
	private ChangeSetSummaryMessageImpl changeSetSummaryMessage;

	@Mock
	private Exchange exchange;

	private DatasyncSummary resultSummary;

	/**
	 * Set up test.
	 */
	@Before
	public final void setUp() {
		processor = new PublishChangeSetProcessor() {
			@Override
			protected ChangeSetSummaryMessageImpl createChangeSetSummaryMessage(final String processSummary, final boolean success) {
				return changeSetSummaryMessage;
			}

			@Override
			protected DatasyncExecuter getDatasyncExecuter() {
				return datasyncExecuter;
			}

		};
	}

	private void setSuccessMessageOnSummary(final String changesetGuid) throws Exception {
		resultSummary = new DatasyncSummary(changesetGuid);
		resultSummary.addSummary(SYNC_RESULT_ITEM_STRING);
		
		when(datasyncExecuter.execute(CHANGESET_GUID)).thenReturn(resultSummary);
	}

	private void setErrorMessageOnSummary(final String changesetGuid) throws Exception {
		resultSummary = new DatasyncSummary(changesetGuid);
		resultSummary.addError(SYNC_ERROR_RESULT_ITEM);
		
		when(datasyncExecuter.execute(CHANGESET_GUID)).thenReturn(resultSummary);
	}
	
	/**
	 * Test happy path through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public void testHappyPath() throws Exception {
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID);
		setSuccessMessageOnSummary(CHANGESET_GUID);

		processor.process(exchange);

		verify(exchange, times(1)).setProperty(EpExchangeConstants.DST_PUBLISH_SUCCESS, Boolean.TRUE);
		verify(exchange, times(1)).setProperty(EpExchangeConstants.CHANGESET_RESULT_MESSAGE, changeSetSummaryMessage);
		verify(exchange, times(0)).setProperty(EpExchangeConstants.DST_PUBLISH_SUCCESS, Boolean.FALSE);

		assertTrue(processor.createProcessSummary(resultSummary).isEmpty());
	}

	/**
	 * Test DST publish failure through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public void testDstPublishFailure() throws Exception {
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID);
		setErrorMessageOnSummary(CHANGESET_GUID);

		try {
			processor.process(exchange);
		} catch (DataSyncPublishException dspe) {
			// Expected exception
		}

		verify(exchange, times(1)).setProperty(EpExchangeConstants.DST_PUBLISH_SUCCESS, Boolean.TRUE);
		verify(exchange, times(1)).setProperty(EpExchangeConstants.CHANGESET_RESULT_MESSAGE, changeSetSummaryMessage);
		verify(exchange, times(1)).setProperty(EpExchangeConstants.DST_PUBLISH_SUCCESS, Boolean.FALSE);

		assertEquals(SYNC_ERROR_RESULT_ITEM + NEW_LINE_CHAR, processor.createProcessSummary(resultSummary));
	}

	/**
	 * Test DST publish exception through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test(expected = DataSyncPublishException.class)
	public void testDstPublishException() throws Exception {
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID);
		setErrorMessageOnSummary(CHANGESET_GUID);

		processor.process(exchange);
	}

	/**
	 * Test missing changeset GUID failure through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public void testMissingChangeSetGuidFailure() throws Exception {
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(null);

		try {
			processor.process(exchange);
		} catch (UnrecoverableMessageException ume) {
			// Expected exception
		}

		verify(exchange, times(1)).setProperty(EpExchangeConstants.DST_PUBLISH_SUCCESS, Boolean.TRUE);
		verify(exchange, times(0)).setProperty(EpExchangeConstants.CHANGESET_RESULT_MESSAGE, changeSetSummaryMessage);
		verify(exchange, times(0)).setProperty(EpExchangeConstants.DST_PUBLISH_SUCCESS, Boolean.FALSE);
	}

	/**
	 * Test missing changeset GUID exception through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test(expected = UnrecoverableMessageException.class)
	public void testMissingChangeSetGuidException() throws Exception {
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(null);

		processor.process(exchange);
	}
}
