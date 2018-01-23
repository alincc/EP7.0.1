/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.subscriptions.permissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.shiro.subject.PrincipalCollection;
import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.subscriptions.integration.SubscriptionLookupStrategy;

/**
 * Test class for {@link SubscriptionIdParameterStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionIdParameterStrategyTest {

	private static final String SUBSCRIPTION_ID = "SUBSCRIPTION_ID";
	private static final String ENCODED_SUBSCRIPTION_ID = Base32Util.encode(SUBSCRIPTION_ID);
	private static final String USER_ID = "7F4E992F-9CFC-E648-BA11-DF1D5B23968F";
	private static final String SCOPE = "SCOPE";
	private static final PrincipalCollection PRINCIPALS = TestSubjectFactory.createCollectionWithScopeAndUserId(SCOPE, USER_ID);

	@Mock
	private SubscriptionLookupStrategy subscriptionLookup;

	@InjectMocks
	private SubscriptionIdParameterStrategy subscriptionIdParameterStrategy;

	/**
	 * Tests getting a parameter value.
	 */
	@Test
	public void testGetParameterValue() {
		Collection<String> ids = Arrays.asList(SUBSCRIPTION_ID);

		when(subscriptionLookup.getSubscriptionIds(SCOPE, USER_ID)).thenReturn(ExecutionResultFactory.createReadOK(ids));

		String subscriptionIdString = subscriptionIdParameterStrategy.getParameterValue(PRINCIPALS);
		assertEquals("This should be the expected subscription ID.", ENCODED_SUBSCRIPTION_ID, subscriptionIdString);
	}

	/**
	 * Test handling Failure Result.
	 */
	@Test
	public void testExecutionResultFailure() {
		when(subscriptionLookup.getSubscriptionIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound("Not Found"));

		String subscriptionId = subscriptionIdParameterStrategy.getParameterValue(PRINCIPALS);

		assertThat("Subscription ID should be empty", subscriptionId, Matchers.isEmptyOrNullString());
	}

}
