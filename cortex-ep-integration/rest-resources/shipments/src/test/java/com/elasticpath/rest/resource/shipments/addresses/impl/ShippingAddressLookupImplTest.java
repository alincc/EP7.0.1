/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
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
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.shipments.addresses.integration.ShippingAddressLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Unit tests for {@link ShippingAddressLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingAddressLookupImplTest {

	public static final String URI = "uri";
	public static final String SCOPE = "scope";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceState<ShipmentEntity> mockShipmentRepresentation;
	@Mock
	private ShipmentEntity mockShipmentEntity;
	@Mock
	private ResourceState<AddressEntity> mockAddressRepresentation;
	@Mock
	private AddressEntity mockShippingAddress;
	@Mock
	private ShippingAddressLookupStrategy lookupStrategy;
	@Mock
	private TransformRfoToResourceState<AddressEntity, AddressEntity, ShipmentEntity> transformer;
	@InjectMocks
	private ShippingAddressLookupImpl lookup;

	@Before
	public void setUp() {
		when(mockShipmentRepresentation.getEntity()).thenReturn(mockShipmentEntity);
	}

	@Test
	public void testGetShippingAddressSuccessful() {
		when(lookupStrategy.getShippingAddress(anyString(), anyString(), anyString()))
				.thenReturn(ExecutionResultFactory.createReadOK(mockShippingAddress));
		when(transformer.transform(mockShippingAddress, mockShipmentRepresentation)).thenReturn(mockAddressRepresentation);

		ExecutionResult<ResourceState<AddressEntity>> result = lookup.getShippingAddress(mockShipmentRepresentation);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(mockAddressRepresentation);
	}

	@Test
	public void testGetShippingAddressNotFound() {
		when(lookupStrategy.getShippingAddress(anyString(), anyString(), anyString())).thenReturn(
				ExecutionResultFactory.<AddressEntity> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookup.getShippingAddress(mockShipmentRepresentation);
	}
}
