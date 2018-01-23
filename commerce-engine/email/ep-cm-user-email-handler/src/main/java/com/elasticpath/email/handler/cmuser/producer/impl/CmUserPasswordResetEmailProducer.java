/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.cmuser.producer.impl;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.email.domain.EmailProperties;

/**
 * Producer for a CM User password reset confirmation email.
 */
public class CmUserPasswordResetEmailProducer extends AbstractCmUserEmailProducer {

	@Override
	public Email createEmail(final String guid, final Map<String, Object> emailData) throws EmailException {

		final CmUser cmUser = getCmUser(guid);
		final String password = getPassword(emailData);
		final Locale locale = getLocale(emailData);
		final EmailProperties properties = getCmUserEmailPropertyHelper().getResetEmailProperties(cmUser, password, locale);

		return getEmailComposer().composeMessage(properties);
	}

}
