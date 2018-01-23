/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.resource.emails.integration.EmailWriterStrategy;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests the {@link com.elasticpath.rest.resource.emails.impl.EmailsResourceOperatorImpl#processCreateEmail}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CreateEmailTest {

	private static final String EMAIL_URI = "/email";
	private static final String RESOURCE_SERVER_NAME = "blah";
	private static final String SCOPE = "scope";
	@Mock
	private EmailWriterStrategy emailWriterStrategy;
	private static final EmailEntity EMAIL_ENTITY = EmailEntity.builder().withEmail("a@b.com").build();
	private static final ResourceState<EmailEntity> EMAIL = ResourceState.Builder.create(EMAIL_ENTITY).build();


	private EmailsResourceOperatorImpl emailsResourceOperator;

	@Before
	public void setUp() {
		emailsResourceOperator = new EmailsResourceOperatorImpl(RESOURCE_SERVER_NAME, null, null, emailWriterStrategy, null
		);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateEmail() {
		when(emailWriterStrategy.createEmail(EMAIL_ENTITY)).thenReturn(ExecutionResultFactory.<Void>createUpdateOK());
		ResourceOperation operation = TestResourceOperationFactory.createCreate(EMAIL_URI, EMAIL);

		OperationResult result = emailsResourceOperator.processCreateEmail(SCOPE, operation);

		assertTrue("This should be a successful operation.", result.isSuccessful());
		assertEquals("The resource status should be 204 Update Ok.", result.getResourceStatus(), ResourceStatus.CREATE_OK);
	}


	@Test(expected = BrokenChainException.class)
	@SuppressWarnings("unchecked")
	public void testCreateEmailWhenFailure() {
		ResourceOperation operation = TestResourceOperationFactory.createCreate(EMAIL_URI, EMAIL);
		when(emailWriterStrategy.createEmail(EMAIL_ENTITY)).thenReturn(ExecutionResultFactory.<Void>createNotFound());


		emailsResourceOperator.processCreateEmail(SCOPE, operation);
	}

	@Test(expected = BrokenChainException.class)
	@SuppressWarnings("unchecked")
	public void testCreateEmailWhenNullBody() {
		ResourceOperation operation = TestResourceOperationFactory.createCreate(EMAIL_URI, null);

		emailsResourceOperator.processCreateEmail(SCOPE, operation);
	}
}
