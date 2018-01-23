/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.subscriptions.impl;

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
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests URI-related annotations on {@link SubscriptionResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SubscriptionResourceOperatorImpl.class })
public class SubscriptionResourceOperatorImplUriTest extends AbstractResourceOperatorUriTest {

	private static final String SCOPE = "scope";
	private static final String SUBSCRIPTION_ID = "4ndg5pjosxx6x4ria6xfclmq3u=";
	private static final String SUBSCRIPTIONS = "subscriptions";

	@Spy
	private final SubscriptionResourceOperatorImpl subscriptionResourceOperator = new SubscriptionResourceOperatorImpl(null, null);
	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Tests {@link SubscriptionResourceOperatorImpl#processReadSubscriptionList(String, ResourceOperation)} is invoked.
	 */
	@Test
	public void testProcessReadSubscriptionList() {
		String uri = URIUtil.format(SUBSCRIPTIONS, SCOPE);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(subscriptionResourceOperator)
				.processReadSubscriptionList(SCOPE, operation);

		dispatch(operation);

		verify(subscriptionResourceOperator).processReadSubscriptionList(SCOPE, operation);
	}

	/**
	 * Tests {@link SubscriptionResourceOperatorImpl#processReadSubscription(String, String, ResourceOperation)} is invoked.
	 */
	@Test
	public void testProcessReadSubscription() {
		String uri = URIUtil.format(SUBSCRIPTIONS, SCOPE, SUBSCRIPTION_ID);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(subscriptionResourceOperator)
				.processReadSubscription(SCOPE, SUBSCRIPTION_ID, operation);

		dispatch(operation);

		verify(subscriptionResourceOperator).processReadSubscription(SCOPE, SUBSCRIPTION_ID, operation);
	}

	private void dispatch(final ResourceOperation operation) {
		dispatchMethod(operation, subscriptionResourceOperator);
	}
}
