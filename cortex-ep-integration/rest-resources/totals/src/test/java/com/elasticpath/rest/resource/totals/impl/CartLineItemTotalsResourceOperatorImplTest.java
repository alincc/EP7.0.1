/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.test.AssertOperationResult;

/**
 * Processes the resource operation.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CartLineItemTotalsResourceOperatorImplTest {

	@Mock
	private TotalLookup<LineItemEntity> totalLookup;

	@InjectMocks
	CartLineItemTotalsResourceOperatorImpl resourceOperator;

	@Mock
	private ResourceState<TotalEntity> lookupResult;

	@Mock
	private ResourceState<LineItemEntity> mockOther;

	@Mock
	private ResourceOperation mockOperation;

	@Test
	public void testProcessRead() {
		when(totalLookup.getTotal(anyLineItemEntity())).thenReturn(ExecutionResultFactory.createReadOK(lookupResult));

		OperationResult result = resourceOperator.processRead(mockOther, mockOperation);

		AssertOperationResult.assertOperationResult(result)
				.resourceStatus(ResourceStatus.READ_OK);

	}

	private static ResourceState<LineItemEntity> anyLineItemEntity() {
		return Mockito.any();
	}
}
