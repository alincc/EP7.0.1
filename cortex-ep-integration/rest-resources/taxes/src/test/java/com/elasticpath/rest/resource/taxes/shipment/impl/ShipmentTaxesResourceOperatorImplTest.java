/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.impl;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.taxes.TaxesLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.test.AssertOperationResult;

/**
 * Tests for {@link ShipmentTaxesResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentTaxesResourceOperatorImplTest {

	@Mock private TaxesLookup<ShipmentEntity> shipmentTaxesLookup;
	@Mock private ResourceState<ShipmentEntity> shipmentRepresentation;
	@Mock private ResourceState<TaxesEntity> taxesResource;

	@InjectMocks private ShipmentTaxesResourceOperatorImpl resourceOperator;

	@Test
	public void testProcessReadSuccess() {
		mockLookup(ExecutionResultFactory.createReadOK(taxesResource));

		OperationResult operationResult = performRead();

		AssertOperationResult.assertOperationResult(operationResult)
				.resourceStatus(ResourceStatus.READ_OK)
				.resourceState(taxesResource);
	}

	@Test
	public void testProcessReadWithLookupNotFound() {
		mockLookup(ExecutionResultFactory.<ResourceState<TaxesEntity>>createNotFound());

		OperationResult operationResult = performRead();

		AssertOperationResult.assertOperationResult(operationResult)
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	private void mockLookup(final ExecutionResult<ResourceState<TaxesEntity>> taxesRepresentationResult) {
		when(shipmentTaxesLookup.getTaxes(shipmentRepresentation)).thenReturn(taxesRepresentationResult);
	}

	private OperationResult performRead() {
		return resourceOperator.processRead(shipmentRepresentation, TestResourceOperationFactory.createRead("/irrelevant"));
	}

}
