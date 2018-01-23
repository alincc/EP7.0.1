package com.elasticpath.repo.camelimport.processors;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.repo.camelimport.CamelImportConstants;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetSearchCriteria;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Unit tests for GenerateDPToSasCreateInCDHubMsgProcessorImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateChangesetProcessorTest {

	/** initialization String values. **/
	private static final String DEST_DIR = "1234567890_PIERS";
	private static final String CHANGESET_GUID = "TEST_CHANGESET_GUID";
	private static final String CMUSER_GUID = "TEST_CMUSER_GUID";

	private String cmUserName;

	private CreateChangesetProcessor processor;

	@Mock
	private Exchange exchange;

	@Mock
	private ChangeSetManagementService changeSetManagementService;

	@Mock
	private CmUserService cmUserService;

	@Mock
	private ChangeSet changeSet;

	@Mock
	private CmUser cmuser;
	
	@Mock
	private BeanFactory beanFactory;
	
	@Mock
	private ChangeSetLoadTuner changeSetLoadTuner;
	
	@Mock
	private Message inMsg;
	
	/**
	 * Set up test.
	 */
	@Before
	public final void setUp() {
		cmUserName = CreateChangesetProcessor.getCmUserName();
		when(beanFactory.getBean(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE)).thenReturn(changeSetManagementService);
		when(beanFactory.getBean(ContextIdNames.CMUSER_SERVICE)).thenReturn(cmUserService);
		when(beanFactory.getBean(ContextIdNames.CHANGESET_LOAD_TUNER)).thenReturn(changeSetLoadTuner);
		when(beanFactory.getBean(ContextIdNames.CHANGE_SET)).thenReturn(changeSet);
		when(exchange.getIn()).thenReturn(inMsg);
		
		when(changeSetManagementService.add(changeSet)).thenReturn(changeSet);

		processor = new CreateChangesetProcessor();
		processor.setBeanFactory(beanFactory);
		
		when(changeSet.getGuid()).thenReturn(CHANGESET_GUID);		
		when(cmuser.getGuid()).thenReturn(CMUSER_GUID);
		when(cmuser.getUserName()).thenReturn(cmUserName);
	}

	/**
	 * Test Happy path with through the processor.
	 * 
	 * @throws Exception exception
	 */

	@Test
	public final void testHappyPath() throws Exception {
		when(exchange.getProperty(CamelImportConstants.IMPORT_DATA_DIR_PATH)).thenReturn(DEST_DIR);
		when(cmUserService.findByUserName(cmUserName)).thenReturn(cmuser);
		
		processor.process(exchange);

		verify(cmUserService, times(1)).findByUserName(cmUserName);
		verify(changeSet, times(1)).getGuid();
		verify(changeSetManagementService, times(1)).findByCriteria(any(ChangeSetSearchCriteria.class), any(ChangeSetLoadTuner.class));
	}

	/**
	 * Test Configuration exception is thrown when no dest dir is found on the exchange.
	 * 
	 * @throws Exception exception
	 */
	@Test(expected = ConfigurationException.class)
	public final void testNoDestDirOnExchangeThrowsUnrecoverableException() throws Exception {
		when(exchange.getProperty(CamelImportConstants.IMPORT_DATA_DIR_PATH)).thenReturn(null);
		when(cmUserService.findByUserName(cmUserName)).thenReturn(cmuser);

		processor.process(exchange);
		
		verify(cmUserService, times(0)).findByUserName(cmUserName);
		verify(changeSet, times(0)).getGuid();
		verify(changeSetManagementService, times(0)).findByCriteria(any(ChangeSetSearchCriteria.class), any(ChangeSetLoadTuner.class));
	}

	/**
	 * Test Configuration exception is thrown when no SYSTEM user exists.
	 * 
	 * @throws Exception exception
	 */
	@Test(expected = RuntimeException.class)
	public final void testNoSYSTEMUserThrowsUnrecoverableException() throws Exception {
		when(exchange.getProperty(CamelImportConstants.IMPORT_DATA_DIR_PATH)).thenReturn(DEST_DIR);
		when(cmUserService.findByUserName(cmUserName)).thenReturn(null);

		processor.process(exchange);

		verify(cmUserService, times(1)).findByUserName(cmUserName);
		verify(changeSet, times(0)).getGuid();
		verify(changeSetManagementService, times(0)).findByCriteria(any(ChangeSetSearchCriteria.class), 
				any(ChangeSetLoadTuner.class), anyInt(), anyInt());
	}
}
