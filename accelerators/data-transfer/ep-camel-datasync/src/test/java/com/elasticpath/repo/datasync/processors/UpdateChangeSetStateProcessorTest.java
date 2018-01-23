package com.elasticpath.repo.datasync.processors;

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
 * Unit tests for UpdateChangeSetStateProcessor.
 */
@RunWith(MockitoJUnitRunner.class)
public class UpdateChangeSetStateProcessorTest {

	private static final String CHANGESET_GUID = "TEST_CHANGESET_GUID";

	private UpdateChangeSetStateProcessor processor;

	@Mock
	private Exchange exchange;

	@Mock
	private ChangeSetManagementService changeSetManagementService;

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private ChangeSetService changeSetService;

	@Mock
	private ChangeSetLoadTuner changeSetLoadTuner;

	@Mock
	private Message inMsg;

	@Mock
	private ChangeSet changeSet;

	/**
	 * Set up test.
	 */
	@Before
	public final void setUp() {
		processor = new UpdateChangeSetStateProcessor();
		processor.setBeanFactory(beanFactory);
		processor.setChangeSetManagementService(changeSetManagementService);
		processor.setUpdateChangeSetState(ChangeSetStateCode.FINALIZED);

		when(beanFactory.getBean(ContextIdNames.CHANGESET_SERVICE)).thenReturn(changeSetService);
		when(beanFactory.getBean(ContextIdNames.CHANGESET_LOAD_TUNER)).thenReturn(changeSetLoadTuner);
		when(exchange.getIn()).thenReturn(inMsg);
		when(changeSetManagementService.get(CHANGESET_GUID, changeSetLoadTuner)).thenReturn(changeSet);
	}

	/**
	 * Test happy path through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public void testHappyPath() throws Exception {
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID);
		when(changeSetService.isChangeSetEnabled()).thenReturn(Boolean.TRUE);
		when(changeSet.getStateCode()).thenReturn(ChangeSetStateCode.READY_TO_PUBLISH);

		processor.process(exchange);

		verify(changeSetManagementService, times(1)).updateState(CHANGESET_GUID, ChangeSetStateCode.FINALIZED, changeSetLoadTuner);
	}

	/**
	 * Test missing changeset guid failure through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public void testMissingChangeSetFailure() throws Exception {
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn("");

		try {
			processor.process(exchange);
		} catch (UpdateChangeSetStatusException ucsse) {
			// Expected exception
		}

		verify(exchange, times(1)).setProperty(EpExchangeConstants.CHANGESET_STATUS_UPDATE_FAILURE, Boolean.TRUE);
	}

	/**
	 * Test missing changeset guid exception through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test(expected = UpdateChangeSetStatusException.class)
	public void testMissingChangeSetException() throws Exception {
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(null);
		when(changeSet.getStateCode()).thenReturn(ChangeSetStateCode.READY_TO_PUBLISH);

		processor.process(exchange);
	}

	/**
	 * Test changeset disabled exception through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test(expected = UpdateChangeSetStatusException.class)
	public void testIsChangeSetEnabledException() throws Exception {
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID);
		when(changeSetService.isChangeSetEnabled()).thenReturn(Boolean.FALSE);

		processor.process(exchange);
	}

	/**
	 * Test a ChangeSetManagementService failure through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public void testChangeSetManagementServiceFailure() throws Exception {
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID);
		when(changeSetService.isChangeSetEnabled()).thenReturn(Boolean.TRUE);
		when(changeSetManagementService.updateState(CHANGESET_GUID, ChangeSetStateCode.FINALIZED, changeSetLoadTuner)).thenThrow(
				new EpServiceException("Test exception"));
		when(changeSet.getStateCode()).thenReturn(ChangeSetStateCode.READY_TO_PUBLISH);

		try {
			processor.process(exchange);
		} catch (UpdateChangeSetStatusException ucsse) {
			// Expected exception
		}

		verify(exchange, times(1)).setProperty(EpExchangeConstants.CHANGESET_STATUS_UPDATE_FAILURE, Boolean.TRUE);
	}

	/**
	 * Test a ChangeSetManagementService exception through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test(expected = UpdateChangeSetStatusException.class)
	public void testChangeSetManagementServiceException() throws Exception {
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID);
		when(changeSetService.isChangeSetEnabled()).thenReturn(Boolean.TRUE);
		when(changeSetManagementService.updateState(CHANGESET_GUID, ChangeSetStateCode.FINALIZED, changeSetLoadTuner)).thenThrow(
				new EpServiceException("Test exception"));
		when(changeSet.getStateCode()).thenReturn(ChangeSetStateCode.READY_TO_PUBLISH);

		processor.process(exchange);
	}

	/**
	 * Testing an exception is thrown when change set is finalized.
	 * 
	 * @throws Exception thrown when an exception occurs
	 */
	@Test(expected = EpSystemException.class)
	public void testLockedChangesetCausesError() throws Exception {
		when(changeSetService.isChangeSetEnabled()).thenReturn(Boolean.TRUE);
		when(exchange.getProperty(EpExchangeConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID);
		when(changeSet.getStateCode()).thenReturn(ChangeSetStateCode.FINALIZED);

		processor.process(exchange);
	}
}
