/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.impl;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.shipments.shippingoption.ShippingOptionsLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.test.AssertOperationResult;

/**
 * Tests for {@link ShippingOptionsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionsResourceOperatorImplTest {

	private static final String SHIPPING_OPTION_URI = "/testShippingCostUri";

	@Mock
	private ShippingOptionsLookup shippingOptionsLookup;
	@Mock
	private ResourceState<ShippingOptionEntity> shippingOptionEntity;
	@Mock
	private ResourceState<ShipmentEntity> shipment;
	@Mock
	private ShipmentEntity shipmentEntity;
	@InjectMocks
	private ShippingOptionsResourceOperatorImpl resourceOperator;

	@Before
	public void setUp() {
		when(shipment.getEntity()).thenReturn(shipmentEntity);
	}

	@Test
	public void testProcessReadSuccess() {

		mockShippingCostLookup(ExecutionResultFactory.createReadOK(shippingOptionEntity));
		
		OperationResult readResult = resourceOperator.processRead(shipment, createReadOperation());
		
		AssertOperationResult.assertOperationResult(readResult).resourceStatus(ResourceStatus.READ_OK);
	}
	
	@Test
	public void testProcessReadWithLookupNotFound() {

		mockShippingCostLookup(ExecutionResultFactory.<ResourceState<ShippingOptionEntity>>createNotFound());
		
		OperationResult readResult = resourceOperator.processRead(shipment, createReadOperation());
		
		AssertOperationResult.assertOperationResult(readResult).resourceStatus(ResourceStatus.NOT_FOUND);
	}
	
	@Test
	public void testProcessReadWithLookupServerError() {

		mockShippingCostLookup(ExecutionResultFactory.<ResourceState<ShippingOptionEntity>>createServerError("Test server error."));
		
		OperationResult readResult = resourceOperator.processRead(shipment, createReadOperation());
		
		AssertOperationResult.assertOperationResult(readResult).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	private void mockShippingCostLookup(final ExecutionResult<ResourceState<ShippingOptionEntity>> shippingOptionResult) {
		when(shippingOptionsLookup.getShippingOption(shipment)).thenReturn(shippingOptionResult);
	}
	
	private ResourceOperation createReadOperation() {
		return TestResourceOperationFactory.createRead(SHIPPING_OPTION_URI);
	}

}
