/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.service.order.OrderService;

/**
 * Test cases for {@link ShipmentRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentRepositoryImplTest {

	private static final String RESULT_FAILURE_ASSERT_MESSAGE = "The operation should have failed";

	private static final String RESULT_SUCCESS_ASSERT_MESSAGE = "The operation should have completed successfully";

	private static final String RESULT_STATUS_NOT_FOUND_MESSAGE = "Result status should be NOT FOUND";

	private static final String RESULT_STATUS_OK_MESSAGE = "Result status should be OK";

	private static final String SHIPMENTS_COLLECTION_NULL = "The shipment collection should not be null.";

	private static final String STORE_CODE = "TEST";

	private static final String ORDER_GUID = "test-order";

	private static final String SHIPMENT_GUID = "test-shipment";

	private static final String LINE_ITEM_GUID = "test-shipment-line-item";

	@Mock
	private OrderService orderService;

	@Mock
	private Order order;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderShipment orderShipment;

	@InjectMocks
	private ShipmentRepositoryImpl shipmentRepositoryImpl;


	@Test
	public void testFindWhenOrderShipmentNotFound() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(null);

		ExecutionResult<PhysicalOrderShipment> executionResult = callFind();

		assertNotFound(executionResult);
	}

	@Test
	public void testFindForSuccess() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);

		ExecutionResult<PhysicalOrderShipment> executionResult = callFind();

		assertReadOk(executionResult);
		assertNotNull("The order shipment should not null.", executionResult.getData());
	}

	@Test
	public void testFindOtherCustomersShipment() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn("someone-elses-order-guid");

		ExecutionResult<PhysicalOrderShipment> executionResult = callFind();

		assertNotFound(executionResult);
	}

	@Test
	public void testFindAllWhendOrderNotFound() {
		when(orderRepository.findByGuid(STORE_CODE, ORDER_GUID)).thenReturn(ExecutionResultFactory.<Order> createNotFound());

		ExecutionResult<Collection<PhysicalOrderShipment>> executionResult = callFindAll();

		assertNotFound(executionResult);
	}


	@Test
	public void testFindAllWhenNoShipmentFound() {
		when(orderRepository.findByGuid(STORE_CODE, ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(order));
		when(order.getPhysicalShipments()).thenReturn(null);

		ExecutionResult<Collection<PhysicalOrderShipment>> executionResult = callFindAll();

		assertReadOk(executionResult);
		Collection<PhysicalOrderShipment> shipments = executionResult.getData();
		assertNotNull(SHIPMENTS_COLLECTION_NULL, shipments);
		assertTrue("The collection of order shipments should be empty.", shipments.isEmpty());
	}


	@Test
	public void testFindAllWhenOneOrMoreShipmentsFound() {
		when(orderRepository.findByGuid(STORE_CODE, ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(order));
		PhysicalOrderShipment physicalOrderShipment = mock(PhysicalOrderShipment.class);
		List<PhysicalOrderShipment> shipments = ImmutableList.of(physicalOrderShipment);
		when(order.getPhysicalShipments()).thenReturn(shipments);

		ExecutionResult<Collection<PhysicalOrderShipment>> executionResult = callFindAll();

		assertReadOk(executionResult);
		Collection<PhysicalOrderShipment> shipmentsCollection = executionResult.getData();
		assertNotNull(SHIPMENTS_COLLECTION_NULL, shipmentsCollection);
		assertFalse("The collection of order shipments should not be empty.", shipmentsCollection.isEmpty());
	}

	@Test
	public void testGetOrderSkusForShipment() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku orderSku = mock(OrderSku.class);
		expectedSkus.add(orderSku);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);

		ExecutionResult<Collection<OrderSku>> executionResult = shipmentRepositoryImpl.getOrderSkusForShipment(
				STORE_CODE, ORDER_GUID, SHIPMENT_GUID);

		assertReadOk(executionResult);
		assertEquals("The order shipment should not null.", 1, executionResult.getData().size());
	}

	@Test
	public void testGetOrderSku() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku expectedOrderSku = mock(OrderSku.class);
		expectedSkus.add(expectedOrderSku);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);
		when(expectedOrderSku.getGuid()).thenReturn(LINE_ITEM_GUID);

		ExecutionResult<OrderSku> orderSkuResult
				= shipmentRepositoryImpl.getOrderSku(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, null);

		assertReadOk(orderSkuResult);
		assertEquals(expectedOrderSku, orderSkuResult.getData());
	}

	@Test
	public void testGetOrderSkuNoSkusFound() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);

		ExecutionResult<OrderSku> orderSkuResult
				= shipmentRepositoryImpl.getOrderSku(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, null);

		assertNotFound(orderSkuResult);
	}

	@Test
	public void testGetOrderSkuNotFound() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku expectedOrderSku = mock(OrderSku.class);
		expectedSkus.add(expectedOrderSku);
		when(expectedOrderSku.getGuid()).thenReturn("not the sku you're looking for");
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);

		ExecutionResult<OrderSku> orderSkuResult
				= shipmentRepositoryImpl.getOrderSku(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, null);

		assertNotFound(orderSkuResult);
	}

	@Test
	public void testGetOrderSkuBundleConstituentNotFound() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku expectedOrderSku = mock(OrderSku.class);
		expectedSkus.add(expectedOrderSku);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);
		when(expectedOrderSku.getGuid()).thenReturn(LINE_ITEM_GUID);

		ExecutionResult<OrderSku> orderSkuResult
				= shipmentRepositoryImpl.getOrderSku(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, "another-line-item");

		assertNotFound(orderSkuResult);
	}

	@Test
	public void testGetOrderSkuDependentNotFound() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku expectedOrderSku = mock(OrderSku.class);
		expectedSkus.add(expectedOrderSku);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);
		when(expectedOrderSku.getGuid()).thenReturn(LINE_ITEM_GUID);
		OrderSku parentOrderSku = mock(OrderSku.class);
		when(expectedOrderSku.getParent()).thenReturn(parentOrderSku);

		ExecutionResult<OrderSku> orderSkuResult
				= shipmentRepositoryImpl.getOrderSku(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, "another-line-item");

		assertNotFound(orderSkuResult);
	}

	@Test
	public void testGetOrderSkuDependent() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku expectedOrderSku = mock(OrderSku.class);
		expectedSkus.add(expectedOrderSku);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);
		when(expectedOrderSku.getGuid()).thenReturn(LINE_ITEM_GUID);
		OrderSku parentOrderSku = mock(OrderSku.class);
		when(expectedOrderSku.getParent()).thenReturn(parentOrderSku);
		String parentOrderSkuGuid = "another-line-item";
		when(parentOrderSku.getGuid()).thenReturn(parentOrderSkuGuid);

		ExecutionResult<OrderSku> orderSkuResult
				= shipmentRepositoryImpl.getOrderSku(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, parentOrderSkuGuid);

		assertReadOk(orderSkuResult);
	}

	private ExecutionResult<PhysicalOrderShipment> callFind() {
		return shipmentRepositoryImpl.find(ORDER_GUID, SHIPMENT_GUID);
	}

	private ExecutionResult<Collection<PhysicalOrderShipment>> callFindAll() {
		return shipmentRepositoryImpl.findAll(STORE_CODE, ORDER_GUID);
	}

	private void assertNotFound(final ExecutionResult<?> executionResult) {
		assertEquals(RESULT_STATUS_NOT_FOUND_MESSAGE, ResourceStatus.NOT_FOUND, executionResult.getResourceStatus());
		assertTrue(RESULT_FAILURE_ASSERT_MESSAGE, executionResult.isFailure());
	}

	private void assertReadOk(final ExecutionResult<?> executionResult) {
		assertEquals(RESULT_STATUS_OK_MESSAGE, ResourceStatus.READ_OK, executionResult.getResourceStatus());
		assertTrue(RESULT_SUCCESS_ASSERT_MESSAGE, executionResult.isSuccessful());
	}
}
