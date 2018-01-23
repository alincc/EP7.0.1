/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.emails.constants.EmailTestConstants;
import com.elasticpath.rest.schema.uri.EmailsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests {@link EmailsUriBuilderImpl}.
 */
public class EmailsUriBuilderImplTest {

	private static final String EMAIL_ID = "email_id";
	private static final String SCOPE = "scope";

	/**
	 * Tests building an Email URI.
	 */
	@Test
	public void testEmailsUriBuilder() {
		EmailsUriBuilder builder = new EmailsUriBuilderImpl(EmailTestConstants.EMAIL_PATH);
		String emailUri = builder.setEmailId(EMAIL_ID)
				.setScope(SCOPE)
				.build();

		String expectedEmailUri = URIUtil.format(EmailTestConstants.EMAIL_PATH, SCOPE, EMAIL_ID);
		assertEquals("The email uri should match.", expectedEmailUri, emailUri);
	}
}
