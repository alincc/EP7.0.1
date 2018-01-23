/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.integration.epcommerce.addresses.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Test cases for {@link ShippingAddressLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingAddressLookupStrategyImplTest {

	private static final String STORE_CODE = "TEST";

	private static final String ORDER_GUID = "test-order";

	private static final String SHIPMENT_GUID = "test-shipment";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShipmentRepository shipmentRepository;
	@InjectMocks
	private ShippingAddressLookupStrategyImpl shippingAddressLookupStrategyImpl;
	@Mock
	private AbstractDomainTransformer<Address, AddressEntity> shippingAddressTransformer;
	@Mock
	private AddressEntity shippingAddressEntity;
	@Mock
	private PhysicalOrderShipment orderShipment;
	@Mock
	private OrderAddress shipmentAddress;

	/**
	 * Test {@link ShippingAddressLookupStrategyImpl#getShippingAddress(String, String, String)} when order shipment not found.
	 */
	@Test
	public void testGetShippingAddressWhenShipmentNotFound() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(
				ExecutionResultFactory.<PhysicalOrderShipment> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shippingAddressLookupStrategyImpl.getShippingAddress(STORE_CODE, ORDER_GUID, SHIPMENT_GUID);

	}

	/**
	 * Test {@link ShippingAddressLookupStrategyImpl#getShippingAddress(String, String, String)} when shipment address not found.
	 */
	@Test
	public void testGetShippingAddressWhenShipmentAddressNotFound() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(ExecutionResultFactory.createReadOK(orderShipment));
		when(orderShipment.getShipmentAddress()).thenReturn(null);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shippingAddressLookupStrategyImpl.getShippingAddress(STORE_CODE, ORDER_GUID, SHIPMENT_GUID);
	}

	/**
	 * Test {@link ShippingAddressLookupStrategyImpl#getShippingAddress(String, String, String)} when everything works fine.
	 */
	@Test
	public void testGetShippingAddressForSuccess() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(ExecutionResultFactory.createReadOK(orderShipment));
		when(orderShipment.getShipmentAddress()).thenReturn(shipmentAddress);
		when(shippingAddressTransformer.transformToEntity(shipmentAddress)).thenReturn(shippingAddressEntity);

		ExecutionResult<AddressEntity> result = shippingAddressLookupStrategyImpl.getShippingAddress(STORE_CODE, ORDER_GUID, SHIPMENT_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.resourceStatus(ResourceStatus.READ_OK)
			.data(shippingAddressEntity);
	}
}
