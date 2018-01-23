/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.impl;

import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.shipments.addresses.ShippingAddressLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Unit test for {@link ShippingAddressResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingAddressResourceOperatorImplTest {

	@Mock
	private ResourceOperation resourceOperation;
	@Mock
	private ShippingAddressLookup mockShippingAddressLookup;
	@Mock
	private ResourceState<ShipmentEntity> shipmentRepresentation;
	@Mock
	private ResourceState<AddressEntity> mockAddressResource;
	@InjectMocks
	private ShippingAddressResourceOperatorImpl resourceOperator;

	@Test
	public void testProcessReadShippingAddressWhenLookupReturnsReadOkay() {
		when(mockShippingAddressLookup.getShippingAddress(shipmentRepresentation))
				.thenReturn(ExecutionResultFactory.createReadOK(mockAddressResource));

		OperationResult result = resourceOperator.processShippingAddressRead(shipmentRepresentation, resourceOperation);

		assertOperationResult(result)
				.resourceState(mockAddressResource)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadShippingAddressWhenLookupReturnsNotFound() {
		when(mockShippingAddressLookup.getShippingAddress(shipmentRepresentation))
				.thenReturn(ExecutionResultFactory.<ResourceState<AddressEntity>>createNotFound());

		OperationResult result = resourceOperator.processShippingAddressRead(shipmentRepresentation, resourceOperation);

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessReadShippingAddressWhenLookupReturnsServerError() {
		when(mockShippingAddressLookup.getShippingAddress(shipmentRepresentation)).thenReturn(
				ExecutionResultFactory.<ResourceState<AddressEntity>> createServerError("Server Error"));

		OperationResult result = resourceOperator.processShippingAddressRead(shipmentRepresentation, resourceOperation);

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}
}
