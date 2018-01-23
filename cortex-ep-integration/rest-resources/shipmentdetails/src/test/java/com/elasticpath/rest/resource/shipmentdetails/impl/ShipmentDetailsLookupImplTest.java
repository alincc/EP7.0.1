/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetail;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.integration.ShipmentDetailsLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.integration.dto.ShipmentDetailsDto;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link ShipmentDetailsLookupImpl}.
 */
public final class ShipmentDetailsLookupImplTest {
	private static final String OPERATION_SHOULD_HAVE_BEEN_SUCCESSFUL = "Operation should have been successful.";
	private static final String USER_ID = "user id";
	private static final String SHIPMENT_DETAIL_ID = "shipment id";
	private static final String DELIVERY_CORRELATION_ID = "delivery id";
	private static final String DELIVERY_ID = Base32Util.encode(DELIVERY_CORRELATION_ID);
	private static final String ORDER_CORRELATION_ID = "order id";
	private static final String ORDER_ID = Base32Util.encode(ORDER_CORRELATION_ID);
	private static final String SCOPE = "scope";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ResourceState<DeliveryEntity> delivery = ResourceState.Builder
			.create(DeliveryEntity.builder()
					.withOrderId(ORDER_ID)
					.withDeliveryId(DELIVERY_ID)
					.build())
			.withScope(SCOPE)
			.build();

	private final ShipmentDetailsLookupStrategy mockShipmentDetailsLookupStrategy =
			context.mock(ShipmentDetailsLookupStrategy.class);
	private final ShipmentDetailsLookup shipmentDetailsLookup = new ShipmentDetailsLookupImpl(mockShipmentDetailsLookupStrategy);

	/**
	 * Test find shipment details ids by user id.
	 */
	@Test
	public void testFindShipmentDetailsIdsByProfileId() {
		context.checking(new Expectations() {
			{
				oneOf(mockShipmentDetailsLookupStrategy).getShipmentDetailsIds(SCOPE, USER_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(Collections.singleton(SHIPMENT_DETAIL_ID))));
			}
		});

		ExecutionResult<Collection<String>> result = shipmentDetailsLookup.findShipmentDetailsIds(SCOPE, USER_ID);

		assertTrue(OPERATION_SHOULD_HAVE_BEEN_SUCCESSFUL, result.isSuccessful());
		assertTrue("Collection of Shipment Detail IDs does not match expected value.",
				CollectionUtil.containsOnly(Collections.singleton(SHIPMENT_DETAIL_ID), result.getData()));
	}

	/**
	 * Test find shipment details ids by user id when error on lookup.
	 */
	@Test
	public void testFindShipmentDetailsIdsByProfileIdWhenErrorOnLookup() {
		context.checking(new Expectations() {
			{
				oneOf(mockShipmentDetailsLookupStrategy).getShipmentDetailsIds(SCOPE, USER_ID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentDetailsLookup.findShipmentDetailsIds(SCOPE, USER_ID);
	}

	/**
	 * Test get shipment detail.
	 */
	@Test
	public void testGetShipmentDetail() {
		final ShipmentDetailsDto shipmentDetailsDto = ResourceTypeFactory.createResourceEntity(ShipmentDetailsDto.class)
				.setDeliveryCorrelationId(DELIVERY_CORRELATION_ID)
				.setOrderCorrelationId(ORDER_CORRELATION_ID);

		context.checking(new Expectations() {
			{
				oneOf(mockShipmentDetailsLookupStrategy).getShipmentDetail(SCOPE, SHIPMENT_DETAIL_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(shipmentDetailsDto)));
			}
		});

		ExecutionResult<ShipmentDetail> result = shipmentDetailsLookup.getShipmentDetail(SCOPE, SHIPMENT_DETAIL_ID);

		assertTrue(OPERATION_SHOULD_HAVE_BEEN_SUCCESSFUL, result.isSuccessful());
		ShipmentDetail shipmentDetail = result.getData();
		assertEquals("Order Id does not match expected value.", ORDER_ID, shipmentDetail.getOrderId());
		assertEquals("Delivery Id does not match expected value.", DELIVERY_ID, shipmentDetail.getDeliveryId());
	}

	/**
	 * Test get shipment detail when lookup error.
	 */
	@Test
	public void testGetShipmentDetailWhenLookupError() {
		context.checking(new Expectations() {
			{
				oneOf(mockShipmentDetailsLookupStrategy).getShipmentDetail(SCOPE, SHIPMENT_DETAIL_ID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentDetailsLookup.getShipmentDetail(SCOPE, SHIPMENT_DETAIL_ID);
	}

	/**
	 * Test find shipment details id from order and delivery.
	 */
	@Test
	public void testFindShipmentDetailsIdFromOrderAndDelivery() {

		context.checking(new Expectations() {
			{
				oneOf(mockShipmentDetailsLookupStrategy).getShipmentDetailsIdForOrderAndDelivery(ORDER_CORRELATION_ID,
						DELIVERY_CORRELATION_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(SHIPMENT_DETAIL_ID)));
			}
		});

		ExecutionResult<String> result = shipmentDetailsLookup.findShipmentDetailsIdForDelivery(delivery);

		assertTrue(OPERATION_SHOULD_HAVE_BEEN_SUCCESSFUL, result.isSuccessful());
		assertEquals("Shipment Detail Id does not match expected value.", SHIPMENT_DETAIL_ID, result.getData());
	}

	/**
	 * Test find shipment details id from order and delivery on lookup error.
	 */
	@Test
	public void testFindShipmentDetailsIdFromOrderAndDeliveryOnLookupError() {

		context.checking(new Expectations() {
			{
				oneOf(mockShipmentDetailsLookupStrategy).getShipmentDetailsIdForOrderAndDelivery(ORDER_CORRELATION_ID,
						DELIVERY_CORRELATION_ID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentDetailsLookup.findShipmentDetailsIdForDelivery(delivery);
	}

	@Test
	public void ensureShipmentDetailsIdsCanBeRetrievedForOrder() {
		context.checking(new Expectations() {
			{
				oneOf(mockShipmentDetailsLookupStrategy).getShipmentDetailsIdsForOrder(SCOPE, ORDER_CORRELATION_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(Collections.singleton(SHIPMENT_DETAIL_ID))));
			}
		});

		ExecutionResult<Collection<String>> result = shipmentDetailsLookup.findShipmentDetailsIdsForOrder(SCOPE, ORDER_ID);

		assertExecutionResult(result).data(Collections.singleton(SHIPMENT_DETAIL_ID));
	}

	@Test
	public void ensureNotFoundReturnedIfShipmentDetailsIdsNotFoundForOrder() {
		context.checking(new Expectations() {
			{
				oneOf(mockShipmentDetailsLookupStrategy).getShipmentDetailsIdsForOrder(SCOPE, ORDER_CORRELATION_ID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentDetailsLookup.findShipmentDetailsIdsForOrder(SCOPE, ORDER_ID);
	}

	@Test
	public void ensureServerErrorReturnedIfShipmentDetailsIdsForOrderLookupFails() {
		context.checking(new Expectations() {
			{
				oneOf(mockShipmentDetailsLookupStrategy).getShipmentDetailsIdsForOrder(SCOPE, ORDER_CORRELATION_ID);
				will(returnValue(ExecutionResultFactory.createServerError("")));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		shipmentDetailsLookup.findShipmentDetailsIdsForOrder(SCOPE, ORDER_ID);
	}
}
