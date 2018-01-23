/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.emails.integration.EmailLookupStrategy;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests {@link EmailLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class EmailLookupImplTest {

	private static final String RESOURCE_SERVER_NAME = "blah";
	private static final String TEST_CUSTOMER_GUID = "testCustomerGuid";
	private static final String EXPECTED_RESOURCE_STATUS = "The result should have the expected resource status.";
	private static final String RESULT_SHOULD_BE_SUCCESSFUL = "Result should be successful";
	private static final String SCOPE = "SCOPE";
	private static final String DECODED_EMAIL_ID = "decoded_email_id";
	private static final String EMAIL_ID = Base32Util.encode(DECODED_EMAIL_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private EmailLookupStrategy emailLookupStrategy;

	private EmailLookupImpl emailLookup;

	@Before
	public void setUp() {
		emailLookup = new EmailLookupImpl(RESOURCE_SERVER_NAME, emailLookupStrategy);
	}


	@Test
	public void testSuccessfullyFindEmail() {
		EmailEntity emailEntity = createEmailEntity();
		ResourceState<EmailEntity> emailRepresentation = createEmailRepresentation(emailEntity);
		shouldFindEmail(SCOPE, DECODED_EMAIL_ID, ExecutionResultFactory.createReadOK(emailEntity));

		ExecutionResult<ResourceState<EmailEntity>> result = emailLookup.getEmail(SCOPE, EMAIL_ID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.READ_OK, result.getResourceStatus());
		assertEquals("The result should return the expected representation", emailRepresentation, result.getData());
	}


	@Test
	public void testFindEmailWhenEmailNotFound() {
		shouldFindEmail(SCOPE, DECODED_EMAIL_ID, ExecutionResultFactory.<EmailEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		emailLookup.getEmail(SCOPE, EMAIL_ID);
	}


	@Test
	public void testSuccessfullyFindEmailIds() {
		Collection<String> emailIds = Collections.singleton(DECODED_EMAIL_ID);

		shouldGetEmailIds(SCOPE, ExecutionResultFactory.createReadOK(emailIds));

		ExecutionResult<Collection<String>> result = emailLookup.findEmailIds(SCOPE, TEST_CUSTOMER_GUID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.READ_OK, result.getResourceStatus());
		assertThat("The result should return the expected result", result.getData(), Matchers.hasItem(EMAIL_ID));
	}

	/**
	 * Test find email IDs when no IDs are found.
	 */
	@Test
	public void testFindEmailIdsWhenNoIdsFound() {
		shouldGetEmailIds(SCOPE, ExecutionResultFactory.<Collection<String>>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		emailLookup.findEmailIds(SCOPE, TEST_CUSTOMER_GUID);
	}

	private void shouldFindEmail(final String scope, final String emailId, final ExecutionResult<EmailEntity> result) {
		when(emailLookupStrategy.findEmail(scope, emailId)).thenReturn(result);
	}

	private void shouldGetEmailIds(final String scope, final ExecutionResult<Collection<String>> result) {
		when(emailLookupStrategy.findEmailIds(scope, TEST_CUSTOMER_GUID)).thenReturn(result);
	}

	private EmailEntity createEmailEntity() {
		return EmailEntity.builder()
				.withEmail(DECODED_EMAIL_ID)
				.build();
	}

	private ResourceState<EmailEntity> createEmailRepresentation(final EmailEntity emailEntity) {
		String selfUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, EMAIL_ID);
		Self self = SelfFactory.createSelf(selfUri);

		String emailsListUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE);
		ResourceLink emailListLink = ElementListFactory.createListWithoutElement(emailsListUri, CollectionsMediaTypes.LINKS.id());
		return ResourceState.Builder
				.create(emailEntity)
				.withScope(SCOPE)
				.withSelf(self)
				.addingLinks(emailListLink)
				.build();
	}
}
