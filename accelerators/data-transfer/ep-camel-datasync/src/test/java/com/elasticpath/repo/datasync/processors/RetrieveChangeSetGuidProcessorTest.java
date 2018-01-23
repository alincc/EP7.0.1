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

import com.elasticpath.messaging.impl.EventMessageImpl;
import com.elasticpath.repo.datasync.commons.constants.EpExchangeConstants;
import com.elasticpath.repo.datasync.tools.exception.UnrecoverableMessageException;

/**
 * Unit tests for RetrieveChangeSetGuidProcessor.
 */
@RunWith(MockitoJUnitRunner.class)
public class RetrieveChangeSetGuidProcessorTest {

	private static final String CHANGESET_GUID = "TEST_CHANGESET_GUID";

	private RetrieveChangeSetGuidProcessor processor;

	@Mock
	private Exchange exchange;

	@Mock
	private EventMessageImpl changeSetMsg;

	@Mock
	private Message inMsg;

	/**
	 * Set up test.
	 */
	@Before
	public final void setUp() {
		processor = new RetrieveChangeSetGuidProcessor();
		when(inMsg.getBody(EventMessageImpl.class)).thenReturn(changeSetMsg);
		when(exchange.getIn()).thenReturn(inMsg);
	}

	/**
	 * Test happy path through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public void testHappyPath() throws Exception {
		when(changeSetMsg.getGuid()).thenReturn(CHANGESET_GUID);

		processor.process(exchange);

		verify(exchange, times(1)).setProperty(EpExchangeConstants.CHANGESET_GUID, CHANGESET_GUID);
	}

	/**
	 * Test missing changeset GUID failure through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test
	public void testMissingChangeSetGuidFailure() throws Exception {
		when(changeSetMsg.getGuid()).thenReturn(null);

		try {
			processor.process(exchange);
		} catch (Exception e) {
			// Expected exception
		}

		verify(exchange, times(0)).setProperty(EpExchangeConstants.CHANGESET_GUID, CHANGESET_GUID);
	}

	/**
	 * Test missing changeset GUID exception through the processor.
	 * 
	 * @throws Exception exception
	 */
	@Test(expected = UnrecoverableMessageException.class)
	public void testMissingChangeSetGuidException() throws Exception {
		when(changeSetMsg.getGuid()).thenReturn(null);

		processor.process(exchange);
	}
}
