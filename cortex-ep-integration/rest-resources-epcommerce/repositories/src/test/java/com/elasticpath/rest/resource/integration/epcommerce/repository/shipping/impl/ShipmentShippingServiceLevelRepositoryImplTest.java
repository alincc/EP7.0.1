/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Tests for {@link ShipmentShippingServiceLevelRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentShippingServiceLevelRepositoryImplTest {

	private static final String SERVICE_LEVEL_GUID = "testShippingServiceLevelGuid";

	@Mock private ShippingServiceLevelService shippingServiceLevelService;
	@Mock private ShippingServiceLevel shippingServiceLevel;

	@InjectMocks
	private ShipmentShippingServiceLevelRepositoryImpl repositoryImpl;

	@Test
	public void testFindByGuidSuccess() {
		when(shippingServiceLevelService.findByGuid(SERVICE_LEVEL_GUID)).thenReturn(shippingServiceLevel);
		
		ExecutionResult<ShippingServiceLevel> serviceLevelResult = repositoryImpl.findByGuid(SERVICE_LEVEL_GUID);
		
		assertTrue("ShippingServiceLevel lookup should be successful.", serviceLevelResult.isSuccessful());
		assertEquals("Result data should be output of service.", shippingServiceLevel, serviceLevelResult.getData());
	}

	@Test
	public void testFindByGuidFailure() {
		ExecutionResult<ShippingServiceLevel> serviceLevelResult = repositoryImpl.findByGuid(SERVICE_LEVEL_GUID);
		
		assertTrue("ShippingServiceLevel lookup should be a failure.", serviceLevelResult.isFailure());
	}

}
