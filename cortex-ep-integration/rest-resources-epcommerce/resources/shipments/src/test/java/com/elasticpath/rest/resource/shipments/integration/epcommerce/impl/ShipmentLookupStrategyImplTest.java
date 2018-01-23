/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShipmentShippingServiceLevelRepository;

/**
 * Test cases for {@link ShipmentLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLookupStrategyImplTest {

	private static final String STORE_CODE = "TEST";
	private static final String ORDER_GUID = "test-order";
	private static final String SHIPMENT_GUID = "test-shipment";
	private static final String SHIPPING_SERVICE_LEVEL_GUID = "testShippingServiceLevelGuid";
	private static final String RESULT_SUCCESS_ASSERT_MESSAGE = "The operation should have completed successfully";
	private static final String RESULT_STATUS_OK_MESSAGE = "Result status should be OK";
	private static final String SHIPMENT_IDS_COLLECTION_NULL = "The shipment IDs collection should not be null.";
	private static final String STATUS_CODE = "TEST_STATUS";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShipmentRepository shipmentRepository;
	@Mock
	private PhysicalOrderShipment orderShipment;
	@Mock
	private OrderShipmentStatus orderShipmentStatus;
	@Mock
	private ShipmentShippingServiceLevelRepository shipmentShippingServiceLevelRepository;
	@Mock
	private ShipmentEntity shipmentEntity;
	@Mock
	private ShippingServiceLevel shippingServiceLevel;
	@InjectMocks
	private ShipmentLookupStrategyImpl shipmentLookupStrategyImpl;

	@Before
	public void setUp() {
		when(shipmentEntity.getPurchaseId()).thenReturn(ORDER_GUID);
		when(shipmentEntity.getShipmentId()).thenReturn(SHIPMENT_GUID);
		when(orderShipment.getShipmentStatus()).thenReturn(orderShipmentStatus);
		when(orderShipmentStatus.getName()).thenReturn(STATUS_CODE);
	}

	@Test
	public void testFindWhenShipmentNotFound() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(
				ExecutionResultFactory.<PhysicalOrderShipment>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));
		callFind(shipmentEntity);
	}

	@Test
	public void testFindForSuccess() {
		prepareShipmentRepositorySuccess();
		prepareShippingServiceLevelRepositorySuccess();

		ExecutionResult<ShipmentEntity> executionResult = callFind(shipmentEntity);

		assertReadOk(executionResult);
		final ShipmentEntity resultShipmentEntity = executionResult.getData();
		assertNotNull("The shipment dto should not be null.", resultShipmentEntity);
	}

	private void prepareShipmentRepositorySuccess() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(ExecutionResultFactory.createReadOK(orderShipment));
	}

	@Test
	public void testFindShipmentIDsWhenOrderNotFound() {
		when(shipmentRepository.findAll(STORE_CODE, ORDER_GUID)).thenReturn(
				ExecutionResultFactory.<Collection<PhysicalOrderShipment>> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		callFindShipmentIDs();
	}

	@Test
	public void testFindShipmentIDsWhenOneOrMoreShipmentsFound() {
		Collection<PhysicalOrderShipment> shipments = ImmutableList.of(createOrderShipmentMock("123"), createOrderShipmentMock("456"));
		when(shipmentRepository.findAll(STORE_CODE, ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(shipments));

		ExecutionResult<Collection<String>> executionResult = callFindShipmentIDs();

		assertReadOk(executionResult);
		final Collection<String> shipmentIDs = executionResult.getData();
		assertNotNull(SHIPMENT_IDS_COLLECTION_NULL, shipmentIDs);
		assertFalse("Should be non empty collection.", shipmentIDs.isEmpty());
	}

	private ExecutionResult<ShipmentEntity> callFind(final ShipmentEntity shipmentEntity) {
		return shipmentLookupStrategyImpl.find(shipmentEntity);
	}

	private ExecutionResult<Collection<String>> callFindShipmentIDs() {
		return shipmentLookupStrategyImpl.findShipmentIds(STORE_CODE, ORDER_GUID);
	}

	private PhysicalOrderShipment createOrderShipmentMock(final String shipmentNumber) {
		PhysicalOrderShipment orderShipment = mock(PhysicalOrderShipment.class);
		when(orderShipment.getShipmentNumber()).thenReturn(shipmentNumber);
		return orderShipment;
	}

	private void assertReadOk(final ExecutionResult<?> executionResult) {
		assertEquals(RESULT_STATUS_OK_MESSAGE, ResourceStatus.READ_OK, executionResult.getResourceStatus());
		assertTrue(RESULT_SUCCESS_ASSERT_MESSAGE, executionResult.isSuccessful());
	}

	private void prepareShippingServiceLevelRepositorySuccess() {
		when(orderShipment.getShippingServiceLevelGuid()).thenReturn(SHIPPING_SERVICE_LEVEL_GUID);
		when(shipmentShippingServiceLevelRepository.findByGuid(SHIPPING_SERVICE_LEVEL_GUID)).thenReturn(
				ExecutionResultFactory.createReadOK(shippingServiceLevel));
	}
}
