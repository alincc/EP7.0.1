package com.elasticpath.repo.camelimport.processors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.Exchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
 * Tests for {@link LockChangeSetStateProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class UpdateChangeSetStateProcessorTest {

	/** Change Set value string. **/
	private static final String CHANGESET_GUID_VALUE = "CHANGESET_GUID_VALUE";

	@Mock
	private LockChangeSetStateProcessor processor;

	@Mock
	private ChangeSetManagementService changeSetManagementService;

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private ChangeSetLoadTuner changeSetLoadTuner;

	@Mock
	private ChangeSetService changeSetService;

	@Mock
	private ChangeSet changeSet;

	@Mock
	private Exchange exchange;

	/**
	 * Sets up the tests.
	 */
	@Before
	public void setUp() {
		when(beanFactory.getBean(ContextIdNames.CHANGESET_SERVICE)).thenReturn(changeSetService);
		when(beanFactory.getBean(ContextIdNames.CHANGESET_LOAD_TUNER)).thenReturn(changeSetLoadTuner);
		when(changeSetManagementService.get(CHANGESET_GUID_VALUE, changeSetLoadTuner)).thenReturn(changeSet);
		when(changeSet.getStateCode()).thenReturn(ChangeSetStateCode.OPEN);

		processor = new LockChangeSetStateProcessor();
		processor.setBeanFactory(beanFactory);
		processor.setChangeSetManagementService(changeSetManagementService);
	}

	/**
	 * Testing a happy path through the processor.
	 * 
	 * @throws Exception thrown when an exception occurs
	 */
	@Test
	public void testHappyPath() throws Exception {
		when(changeSetService.isChangeSetEnabled()).thenReturn(Boolean.TRUE);
		when(exchange.getProperty(CamelImportConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID_VALUE);
		when(changeSet.getStateCode()).thenReturn(ChangeSetStateCode.OPEN);

		processor.process(exchange);

		verify(changeSetManagementService, times(1)).updateState(CHANGESET_GUID_VALUE, ChangeSetStateCode.LOCKED, changeSetLoadTuner);
	}

	/**
	 * Testing an exception is thrown when change set is finalized.
	 * 
	 * @throws Exception thrown when an exception occurs
	 */
	@Test(expected = EpSystemException.class)
	public void testLockedChangesetCausesError() throws Exception {
		when(changeSetService.isChangeSetEnabled()).thenReturn(Boolean.TRUE);
		when(exchange.getProperty(CamelImportConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID_VALUE);
		when(changeSet.getStateCode()).thenReturn(ChangeSetStateCode.FINALIZED);
		processor.process(exchange);
	}

	/**
	 * Testing an exception is thrown when change set guid is missing.
	 * 
	 * @throws Exception thrown when an exception occurs
	 */
	@Test(expected = ConfigurationException.class)
	public void testMissingChangeSetGuidFailure() throws Exception {
		when(changeSetService.isChangeSetEnabled()).thenReturn(Boolean.TRUE);
		when(exchange.getProperty(CamelImportConstants.CHANGESET_GUID)).thenReturn(null);

		processor.process(exchange);

		verify(changeSetManagementService, times(0)).updateState(CHANGESET_GUID_VALUE, ChangeSetStateCode.LOCKED, changeSetLoadTuner);
	}

	/**
	 * Testing an exception is thrown when change sets are not enabled.
	 * 
	 * @throws Exception thrown when an exception occurs
	 */
	@Test(expected = ConfigurationException.class)
	public void testChangeSetDisabledFailure() throws Exception {
		when(changeSetService.isChangeSetEnabled()).thenReturn(Boolean.FALSE);
		when(exchange.getProperty(CamelImportConstants.CHANGESET_GUID)).thenReturn(CHANGESET_GUID_VALUE);

		processor.process(exchange);

		verify(changeSetManagementService, times(0)).updateState(CHANGESET_GUID_VALUE, ChangeSetStateCode.LOCKED, changeSetLoadTuner);
	}
}
