/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.emails.constants.EmailTestConstants;
import com.elasticpath.rest.schema.uri.EmailFormUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link EmailFormUriBuilderImpl}.
 */
public class EmailFormUriBuilderImplTest {
	private static final String SCOPE = "scope";

	/**
	 * Tests building an Email form URI.
	 */
	@Test
	public void testEmailFormUriBuilder() {
		EmailFormUriBuilder builder = new EmailFormUriBuilderImpl(EmailTestConstants.EMAIL_PATH);
		String emailFormUri = builder
				.setScope(SCOPE)
				.build();

		String expectedEmailFormUri = URIUtil.format(EmailTestConstants.EMAIL_PATH, SCOPE, Form.URI_PART);
		assertEquals("The email form uri should match.", expectedEmailFormUri, emailFormUri);
	}
}
