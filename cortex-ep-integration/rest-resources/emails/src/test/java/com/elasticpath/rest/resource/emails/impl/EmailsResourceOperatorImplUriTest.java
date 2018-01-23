/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests URI-related annotations on {@link EmailsResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ EmailsResourceOperatorImpl.class })
public class EmailsResourceOperatorImplUriTest extends AbstractResourceOperatorUriTest {
	private static final String EMAILS_RESOURCE = "emails";
	private static final String EMAIL_ID = "3xz22pjosxx6x4ria6xfcmlb6c=";
	private static final String SCOPE = "testscope";

	@Spy
	private final EmailsResourceOperatorImpl resourceOperator = new EmailsResourceOperatorImpl(null, null, null, null, null);

	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Test path annotation for processing a read on the email form.
	 */
	@Test
	public void testPathAnnotationForProcessReadForm() {
		String uri = URIUtil.format(EMAILS_RESOURCE, SCOPE, Form.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadForm(SCOPE, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadForm(SCOPE, operation);

	}

	/**
	 * Test path annotation for processing a read on an email.
	 */
	@Test
	public void testPathAnnotationForProcessReadEmail() {
		String uri = URIUtil.format(EMAILS_RESOURCE, SCOPE, EMAIL_ID);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadEmail(SCOPE, EMAIL_ID, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadEmail(SCOPE, EMAIL_ID, operation);
	}

	/**
	 * Test path annotation for processing the creation of an email.
	 */
	@Test
	public void testPathAnnotationForCreatingAnEmail() {
		String uri = URIUtil.format(EMAILS_RESOURCE, SCOPE);
		ResourceOperation operation = TestResourceOperationFactory.createCreate(uri, null);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processCreateEmail(SCOPE, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processCreateEmail(SCOPE, operation);
	}

	/**
	 * Test path annotation for reading a list of emails.
	 */
	@Test
	public void testPathAnnotationForReadingListOfEmails() {
		String uri = URIUtil.format(EMAILS_RESOURCE, SCOPE);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadEmails(SCOPE, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadEmails(SCOPE, operation);
	}
}
