/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.subscriptions.impl;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.subscriptions.SubscriptionEntity;
import com.elasticpath.rest.definition.subscriptions.SubscriptionsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.subscriptions.integration.SubscriptionLookupStrategy;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link com.elasticpath.rest.resource.subscriptions.impl.SubscriptionResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionResourceOperatorImplTest {

	private static final String SCOPE = "scope";
	private static final String SUBSCRIPTION_ID = "4ndg5pjosxx6x4ria6xfclmq3u=";
	private static final String ENCODED_SUBSCRIPTION_ID = Base32Util.encode(SUBSCRIPTION_ID);
	private static final String SUBSCRIPTIONS = "subscriptions";
	private static final String LIST_URI = URIUtil.format(SUBSCRIPTIONS, SCOPE);
	private static final String SINGLE_URI = URIUtil.format(LIST_URI, ENCODED_SUBSCRIPTION_ID);
	private static final String USER_ID = "userID";
	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private SubscriptionLookupStrategy subscriptionLookupStrategy;
	@InjectMocks
	private SubscriptionResourceOperatorImpl subscriptionResourceOperator;


	/**
	 * Tests {@link SubscriptionResourceOperatorImpl#processReadSubscriptionList(String, com.elasticpath.rest.ResourceOperation)} is invoked.
	 */
	@Test
	public void testProcessReadSubscriptionList() {
		ResourceOperation readOperation = TestResourceOperationFactory.createRead(LIST_URI);
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
		when(subscriptionLookupStrategy.getSubscriptionIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.singleton(SUBSCRIPTION_ID)));
		ResourceLink expectedLink = ElementListFactory.createElementOfList(SINGLE_URI, SubscriptionsMediaTypes.SUBSCRIPTION.id());
		Self expectedSelf = SelfFactory.createSelf(LIST_URI);

		OperationResult result = subscriptionResourceOperator.processReadSubscriptionList(SCOPE, readOperation);

		assertResourceState(result.getResourceState())
				.containsLink(expectedLink)
				.self(expectedSelf);
	}

	/**
	 * Tests {@link SubscriptionResourceOperatorImpl#processReadSubscription(String, String, com.elasticpath.rest.ResourceOperation)} is invoked.
	 */
	@Test
	public void testProcessReadSubscription() {
		ResourceOperation readOperation = TestResourceOperationFactory.createRead(SINGLE_URI);
		SubscriptionEntity subscriptionEntity = SubscriptionEntity.builder().build();
		when(subscriptionLookupStrategy.getSubscription(SCOPE, SUBSCRIPTION_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(subscriptionEntity));
		Self expectedSelf = SelfFactory.createSelf(SINGLE_URI);

		OperationResult result = subscriptionResourceOperator.processReadSubscription(SCOPE, ENCODED_SUBSCRIPTION_ID, readOperation);

		assertEquals(subscriptionEntity, result.getResourceState().getEntity());
		assertResourceState(result.getResourceState())
				.self(expectedSelf);
	}
}
