/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.impl;

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

/**
 * Test class for {@link StoreRecommendationsResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ StoreRecommendationsResourceOperatorImpl.class })
public final class StoreRecommendationsResourceOperatorUriTest extends AbstractResourceOperatorUriTest {

	public static final String RESOURCE_NAME = "recommendations";
	public static final String SCOPE = "scope";
	public static final String RECOMMENDATION_GROUP = "updownleftrightsells";
	public static final int PAGE_NUMBER = 2;

	@Spy
	private final StoreRecommendationsResourceOperatorImpl resourceOperator = new StoreRecommendationsResourceOperatorImpl(null);
	@Mock
	private OperationResult mockOperationResult;

	@Test
	public void testReadRootRecommendations() {
		String uri = new RecommendationsUriBuilderImpl(RESOURCE_NAME).setScope(SCOPE).build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadStoreRecommendations(SCOPE, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadStoreRecommendations(SCOPE, operation);
	}

	@Test
	public void testReadRecommendedItems() {
		String uri = new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setScope(SCOPE)
				.setRecommendationGroup(RECOMMENDATION_GROUP)
				.build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadRecommendedItemsForStore(SCOPE, RECOMMENDATION_GROUP, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadRecommendedItemsForStore(SCOPE, RECOMMENDATION_GROUP, operation);
	}

	@Test
	public void testReadSpecificPageOfRecommendedItems() {
		String uri = new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setScope(SCOPE)
				.setRecommendationGroup(RECOMMENDATION_GROUP)
				.setPageNumber(PAGE_NUMBER)
				.build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processPagedReadRecommendedItemsForStore(SCOPE, RECOMMENDATION_GROUP, String.valueOf(PAGE_NUMBER), operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processPagedReadRecommendedItemsForStore(SCOPE, RECOMMENDATION_GROUP, String.valueOf(PAGE_NUMBER), operation);
	}
}
