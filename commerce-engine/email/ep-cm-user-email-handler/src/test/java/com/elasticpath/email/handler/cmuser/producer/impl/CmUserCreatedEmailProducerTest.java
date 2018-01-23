/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.cmuser.producer.impl;

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
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.cmuser.helper.CmUserEmailPropertyHelper;
import com.elasticpath.email.util.EmailComposer;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Test class for {@link CmUserCreatedEmailProducer}.
 */
public class CmUserCreatedEmailProducerTest {

	private CmUserCreatedEmailProducer emailProducer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final CmUserEmailPropertyHelper cmUserEmailPropertyHelper = context.mock(CmUserEmailPropertyHelper.class);

	private final EmailComposer emailComposer = context.mock(EmailComposer.class);

	private final CmUserService cmUserService = context.mock(CmUserService.class);

	private static final String CMUSER_GUID = "cmUserGuid1";

	@Before
	public void setUp() {
		emailProducer = new CmUserCreatedEmailProducer();

		emailProducer.setEmailComposer(emailComposer);
		emailProducer.setCmUserEmailPropertyHelper(cmUserEmailPropertyHelper);
		emailProducer.setCmUserService(cmUserService);
	}

	@Test
	public void verifyPasswordReminderEmailCreatedFromCmUserGuid() throws Exception {
		final Email expectedEmail = new SimpleEmail();

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				allowing(cmUserService).findByGuid(CMUSER_GUID);
				will(returnValue(context.mock(CmUser.class)));

				oneOf(cmUserEmailPropertyHelper).getCreateEmailProperties(with(any(CmUser.class)), with(any(String.class)), with(any(Locale.class)));
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Email actualEmail = emailProducer.createEmail(CMUSER_GUID, buildValidAdditionalData());
		assertSame("Unexpected email created by producer", expectedEmail, actualEmail);
	}

	@Test(expected = EmailException.class)
	public void verifyEmailExceptionNotCaught() throws Exception {

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				allowing(cmUserService).findByGuid(with(any(String.class)));
				will(returnValue(context.mock(CmUser.class)));

				oneOf(cmUserEmailPropertyHelper).getCreateEmailProperties(with(any(CmUser.class)), with(any(String.class)), with(any(Locale.class)));
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(throwException(new EmailException("Oh no!")));
			}
		});

		emailProducer.createEmail(CMUSER_GUID, buildValidAdditionalData());
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionThrownWhenNoCmUserIsFound() throws Exception {

		context.checking(new Expectations() {
			{
				allowing(cmUserService).findByGuid(CMUSER_GUID);
				will(returnValue(null));
			}
		});

		emailProducer.createEmail(CMUSER_GUID, buildValidAdditionalData());
	}

	private Map<String, Object> buildValidAdditionalData() {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put("password", "password1");
		additionalData.put("locale", Locale.CANADA.toLanguageTag());

		return additionalData;
	}

}
