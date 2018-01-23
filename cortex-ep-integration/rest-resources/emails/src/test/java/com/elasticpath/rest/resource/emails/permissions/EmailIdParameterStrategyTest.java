/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.permissions;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.shiro.subject.PrincipalCollection;
import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.emails.EmailLookup;
import com.elasticpath.rest.id.util.Base32Util;

/**
 * Test class for {@link EmailIdParameterStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class EmailIdParameterStrategyTest {

	private static final String TEST_USER_ID = "testUserId";
	private static final String SCOPE = "SCOPE";
	private static final PrincipalCollection PRINCIPALS = TestSubjectFactory.createCollectionWithScopeAndUserId(SCOPE, TEST_USER_ID);
	private static final String DECODED_EMAIL_ID = "decoded_email_id";
	private static final String EMAIL_ID = Base32Util.encode(DECODED_EMAIL_ID);

	@Mock
	private EmailLookup emailLookup;

	@InjectMocks
	private EmailIdParameterStrategy emailIdParameterStrategy;

	/**
	 * Test get email id parameter.
	 */
	@Test
	public void testGetEmailIdParameterValue() {
		Collection<String> expectedEmailIds = Collections.singletonList(EMAIL_ID);

		shouldFindEmailIds(SCOPE, ExecutionResultFactory.createReadOK(expectedEmailIds));

		String emailIds = emailIdParameterStrategy.getParameterValue(PRINCIPALS);

		assertThat("The email IDs string should contain the expected ID.", emailIds, Matchers.containsString(EMAIL_ID));
	}

	/**
	 * Test get email ID parameter value when email ID not found.
	 */
	@Test
	public void testGetEmailIdParameterValueWhenEmailIdNotFound() {
		shouldFindEmailIds(SCOPE, ExecutionResultFactory.<Collection<String>>createNotFound());

		String emailIds = emailIdParameterStrategy.getParameterValue(PRINCIPALS);

		assertThat("The email IDs string should be empty.", emailIds, Matchers.isEmptyString());
	}

	private void shouldFindEmailIds(final String scope, final ExecutionResult<Collection<String>> result) {
		when(emailLookup.findEmailIds(scope, TEST_USER_ID)).thenReturn(result);
	}
}
