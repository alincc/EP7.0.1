/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.subscriptions;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.ResourceServerKernel;
import com.elasticpath.rest.resource.subscriptions.integration.SubscriptionLookupStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;

/**
 * Tests shipments bean wiring.
 */
@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class SubscriptionsResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "resourceOperationContext")
	private ResourceOperationContext resourceOperationContext;

	@ReplaceWithMock(beanName = "resourceServerKernel")
	private ResourceServerKernel resourceServerKernel;

	@ReplaceWithMock(beanName = "subscriptionLookupStrategy")
	private SubscriptionLookupStrategy subscriptionLookupStrategy;

}
