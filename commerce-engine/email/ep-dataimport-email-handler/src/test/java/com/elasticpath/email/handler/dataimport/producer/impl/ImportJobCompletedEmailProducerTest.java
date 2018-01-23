/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.dataimport.producer.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.dataimport.helper.ImportEmailPropertyHelper;
import com.elasticpath.email.util.EmailComposer;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.dataimport.ImportJobStatusHandler;

/**
 * Test class for {@link ImportJobCompletedEmailProducer}.
 */
public class ImportJobCompletedEmailProducerTest {

	private static final String LOCALE_KEY = "locale";

	private static final String CMUSER_GUID_KEY = "cmUserGuid";

	private static final String PROCESS_ID = "processId1";

	private static final String CMUSER_GUID = "cmUserGuid1";

	private static final Locale LOCALE = Locale.CANADA;

	private ImportJobCompletedEmailProducer emailProducer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final EmailComposer emailComposer = context.mock(EmailComposer.class);

	private final CmUserService cmUserService = context.mock(CmUserService.class);

	private final ImportJobStatusHandler importJobStatusHandler = context.mock(ImportJobStatusHandler.class);

	private final ImportEmailPropertyHelper importEmailPropertyHelper = context.mock(ImportEmailPropertyHelper.class);

	@Before
	public void setUp() {
		emailProducer = new ImportJobCompletedEmailProducer();

		emailProducer.setEmailComposer(emailComposer);
		emailProducer.setImportEmailPropertyHelper(importEmailPropertyHelper);
		emailProducer.setImportJobStatusHandler(importJobStatusHandler);
		emailProducer.setCmUserService(cmUserService);
	}

	@Test
	public void verifyImportJobCompletedEmailIsCreated() throws Exception {
		final Email expectedEmail = new SimpleEmail();

		final ImportJobStatus importJobStatus = givenImportJobStatusHandlerReturnsImportJobStatus(PROCESS_ID);
		final CmUser cmUser = givenCmUserServiceFindsCmUser(CMUSER_GUID);
		final EmailProperties emailProperties = givenImportEmailPropertyHelperBuildsEmailProperties(importJobStatus, cmUser, LOCALE);

		context.checking(new Expectations() {
			{
				oneOf(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Map<String, Object> additionalData = buildValidAdditionalData();
		final Email actualEmail = emailProducer.createEmail(PROCESS_ID, additionalData);
		assertSame("Unexpected email created by producer", expectedEmail, actualEmail);
	}

	@Test(expected = EmailException.class)
	public void verifyEmailExceptionNotCaught() throws Exception {
		final ImportJobStatus importJobStatus = givenImportJobStatusHandlerReturnsImportJobStatus(PROCESS_ID);
		final CmUser cmUser = givenCmUserServiceFindsCmUser(CMUSER_GUID);
		final EmailProperties emailProperties = givenImportEmailPropertyHelperBuildsEmailProperties(importJobStatus, cmUser, LOCALE);

		context.checking(new Expectations() {
			{
				oneOf(emailComposer).composeMessage(emailProperties);
				will(throwException(new EmailException("Oh no!")));
			}
		});

		final Map<String, Object> additionalData = buildValidAdditionalData();
		emailProducer.createEmail(PROCESS_ID, additionalData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNullProcessId() throws Exception {
		givenCmUserServiceFindsCmUser(CMUSER_GUID);

		emailProducer.createEmail(null, buildValidAdditionalData());
	}

	@Test
	public void verifyNoEmailCreatedWhenNullCmUser() throws Exception {
		final Map<String, Object> additionalData = buildValidAdditionalData();
		additionalData.remove(CMUSER_GUID_KEY);

		final Email actualEmail = emailProducer.createEmail(PROCESS_ID, additionalData);

		assertNull("No email should have been created when no CM User Guid was given.", actualEmail);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNullLocale() throws Exception {
		givenCmUserServiceFindsCmUser(CMUSER_GUID);

		final Map<String, Object> additionalData = buildValidAdditionalData();
		additionalData.remove(LOCALE_KEY);

		emailProducer.createEmail(PROCESS_ID, additionalData);
	}

	private ImportJobStatus givenImportJobStatusHandlerReturnsImportJobStatus(final String processId) {
		final ImportJobStatus importJobStatus = context.mock(ImportJobStatus.class);

		context.checking(new Expectations() {
			{
				allowing(importJobStatusHandler).getImportJobStatus(processId);
				will(returnValue(importJobStatus));

			}
		});

		return importJobStatus;
	}

	private CmUser givenCmUserServiceFindsCmUser(final String cmuserGuid) {
		final CmUser cmUser = context.mock(CmUser.class);

		context.checking(new Expectations() {
			{
				allowing(cmUserService).findByGuid(cmuserGuid);
				will(returnValue(cmUser));
			}
		});

		return cmUser;
	}

	private EmailProperties givenImportEmailPropertyHelperBuildsEmailProperties(final ImportJobStatus importJobStatus, final CmUser cmUser,
																				final Locale locale) {
		final EmailProperties emailProperties = context.mock(EmailProperties.class);

		context.checking(new Expectations() {
			{
				oneOf(importEmailPropertyHelper).getEmailProperties(importJobStatus, cmUser, locale);
				will(returnValue(emailProperties));
			}
		});

		return emailProperties;
	}

	private Map<String, Object> buildValidAdditionalData() {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put(CMUSER_GUID_KEY, CMUSER_GUID);
		additionalData.put(LOCALE_KEY, LOCALE.toString());
		return additionalData;
	}

}
