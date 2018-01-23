/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.shipments.shippingoption.integration.ShippingOptionLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Tests for {@link ShippingOptionsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionsLookupImplTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShippingOptionLookupStrategy shippingOptionLookupStrategy;
	@Mock
	private TransformRfoToResourceState<ShippingOptionEntity, ShippingOptionEntity, ShipmentEntity> shippingOptionTransformer;
	@Mock
	private ResourceState<ShippingOptionEntity> shippingOption;
	@Mock
	private ShippingOptionEntity shippingOptionEntity;
	@Mock
	private ResourceState<ShipmentEntity> shipmentRepresentation;
	@Mock
	private ShipmentEntity shipmentEntity;
	@InjectMocks
	private ShippingOptionsLookupImpl shippingCostLookupImpl;

	@Before
	public void init() {
		String scope = "testScope";
		when(shipmentRepresentation.getEntity()).thenReturn(shipmentEntity);
		when(shipmentRepresentation.getScope()).thenReturn(scope);
		when(shippingOptionTransformer.transform(shippingOptionEntity, shipmentRepresentation))
				.thenReturn(shippingOption);
	}
	
	@Test
	public void testGetShippingCostSuccess() {
		mockShippingOptionLookupStrategy(ExecutionResultFactory.createReadOK(shippingOptionEntity));

		ExecutionResult<ResourceState<ShippingOptionEntity>> lookupResult = shippingCostLookupImpl.getShippingOption(shipmentRepresentation);

		assertExecutionResult(lookupResult)
			.isSuccessful()
			.data(shippingOption);
	}

	@Test
	public void testGetShippingCostWithCostNotFound() {
		mockShippingOptionLookupStrategy(ExecutionResultFactory.<ShippingOptionEntity>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shippingCostLookupImpl.getShippingOption(shipmentRepresentation);
	}

	private void mockShippingOptionLookupStrategy(final ExecutionResult<ShippingOptionEntity> shippingOptionResult) {
		when(shippingOptionLookupStrategy.getShippingOption(anyString(), anyString(), anyString())).thenReturn(shippingOptionResult);
	}
}
