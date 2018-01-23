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
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.test.AssertOperationResult;

/**
 * Processes the resource operation on profiles.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ShipmentLineItemTotalsResourceOperatorImplTest {

	@Mock
	private TotalLookup<ShipmentLineItemEntity> totalLookup;

	@InjectMocks
	ShipmentLineItemTotalsResourceOperatorImpl resourceOperator;

	@Mock
	private ResourceState<TotalEntity> lookupResult;

	@Mock
	private ResourceState<ShipmentLineItemEntity> mockOther;

	@Mock
	private ResourceOperation mockOperation;

	@Test
	public void testProcessRead() {
		when(totalLookup.getTotal(anyShipmentLineItemEntity())).thenReturn(ExecutionResultFactory.createReadOK(lookupResult));

		OperationResult result = resourceOperator.processRead(mockOther, mockOperation);

		AssertOperationResult.assertOperationResult(result)
				.resourceStatus(ResourceStatus.READ_OK);

	}

	private static ResourceState<ShipmentLineItemEntity> anyShipmentLineItemEntity() {
		return Mockito.any();
	}
}
