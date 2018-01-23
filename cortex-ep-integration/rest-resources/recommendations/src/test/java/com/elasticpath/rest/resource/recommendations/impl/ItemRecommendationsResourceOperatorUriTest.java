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
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Test class for {@link ItemRecommendationsResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ItemRecommendationsResourceOperatorImpl.class })
public final class ItemRecommendationsResourceOperatorUriTest extends AbstractUriTest {

	private static final String RESOURCE_NAME = "recommendations";
	private static final String GROUP = "upsells";
	private static final String OTHER_URI = "other/mobee/12345=";
	private static final int PAGE_NUMBER = 2;

	@Mock
	private ItemRecommendationsResourceOperatorImpl resourceOperator;

	@Test
	public void testReadItemRecommendations() {
		String uri = new RecommendationsUriBuilderImpl(RESOURCE_NAME).setSourceUri(OTHER_URI).build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		mediaType(ItemsMediaTypes.ITEM);
		readOther(operation);

		when(resourceOperator.processReadItemRecommendations(anyItemEntity(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadItemRecommendations(anyItemEntity(), anyResourceOperation());
	}

	@Test
	public void testReadRecommendedItemsForItem() {
		String uri = new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setSourceUri(OTHER_URI)
				.setRecommendationGroup(GROUP)
				.build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		mediaType(ItemsMediaTypes.ITEM);
		readOther(operation);

		when(resourceOperator.processReadRecommendedItemsForItem(anyItemEntity(), anyString(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadRecommendedItemsForItem(anyItemEntity(), anyString(), anyResourceOperation());
	}

	@Test
	public void testReadSpecificPageOfRecommendedItemsForItem() {
		String uri = new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setSourceUri(OTHER_URI)
				.setRecommendationGroup(GROUP)
				.setPageNumber(PAGE_NUMBER)
				.build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		mediaType(ItemsMediaTypes.ITEM);
		readOther(operation);

		when(resourceOperator.processPagedReadRecommendedItemsForItem(anyItemEntity(), anyString(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processPagedReadRecommendedItemsForItem(
				anyItemEntity(), anyString(), eq(String.valueOf(PAGE_NUMBER)), anyResourceOperation());
	}

	private ResourceState<ItemEntity> anyItemEntity() {
		return any();
	}
}


