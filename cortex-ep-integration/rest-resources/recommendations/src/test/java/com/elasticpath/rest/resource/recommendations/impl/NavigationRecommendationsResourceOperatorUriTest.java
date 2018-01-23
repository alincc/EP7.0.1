/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.navigations.NavigationsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Test class for {@link NavigationRecommendationsResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ NavigationRecommendationsResourceOperatorImpl.class })
public final class NavigationRecommendationsResourceOperatorUriTest extends AbstractUriTest {

	public static final String RESOURCE_NAME = "recommendations";
	public static final String GROUP = "upsells";
	public static final String OTHER_URI = "other/mobee/12345=";
	public static final int PAGE_NUMBER = 2;

	@Mock
	private NavigationRecommendationsResourceOperatorImpl resourceOperator;


	@Test
	public void testReadNavigationRecommendations() {
		String uri = new RecommendationsUriBuilderImpl(RESOURCE_NAME).setSourceUri(OTHER_URI).build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		when(resourceOperator.processReadNavigationRecommendations(anyNavigationEntity(), anyResourceOperation())).thenReturn(operationResult);

		mediaType(NavigationsMediaTypes.NAVIGATION);
		readOther(operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadNavigationRecommendations(anyNavigationEntity(), anyResourceOperation());
	}

	@Test
	public void testReadRecommendedItemsForNavigation() {
		String uri = new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setSourceUri(OTHER_URI)
				.setRecommendationGroup(GROUP)
				.build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		mediaType(NavigationsMediaTypes.NAVIGATION);
		readOther(operation);

		when(resourceOperator.processReadRecommendedItemsForNavigation(anyNavigationEntity(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadRecommendedItemsForNavigation(anyNavigationEntity(), eq(GROUP), anyResourceOperation());
	}

	@Test
	public void testReadSpecificPageOfRecommendedItemsForNavigation() {
		String uri = new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setSourceUri(OTHER_URI)
				.setRecommendationGroup(GROUP)
				.setPageNumber(PAGE_NUMBER)
				.build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		mediaType(NavigationsMediaTypes.NAVIGATION);
		readOther(operation);

		when(resourceOperator.processPagedReadRecommendedItemsForNavigation(anyNavigationEntity(), anyString(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processPagedReadRecommendedItemsForNavigation(
				anyNavigationEntity(), eq(GROUP), eq(String.valueOf(PAGE_NUMBER)), anyResourceOperation());
	}

	private ResourceState<NavigationEntity> anyNavigationEntity() {
		return any();
	}
}
