/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.integration.DestinationInfoLookupStrategy;

/**
 * Test class for {@link DestinationInfoLookupStrategyImpl}.
 */
public class DestinationInfoLookupStrategyImplTest {

	private static final String SHIPMENT_ID = "shipment_id";
	private static final String ADDRESS_GUID = "address_guid";
	private static final String STORE_CODE = "storeCode";
	private static final String ORDER_GUID = "order_guid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final CartOrderRepository mockCartOrderRepository = context.mock(CartOrderRepository.class);
	private final DestinationInfoLookupStrategy destinationInfoLookupStrategy =
			new DestinationInfoLookupStrategyImpl(mockCartOrderRepository);

	/**
	 * Test find selected address for shipment.
	 */
	@Test
	public void testFindSelectedAddressForShipment() {

		final CartOrder cartOrder = new CartOrderImpl();
		final Address shippingAddress = new OrderAddressImpl();
		shippingAddress.setGuid(ADDRESS_GUID);

		context.checking(new Expectations() {
			{
				allowing(mockCartOrderRepository).findByGuid(STORE_CODE, ORDER_GUID);
				will(returnValue(ExecutionResultFactory.createReadOK(cartOrder)));

				allowing(mockCartOrderRepository).getShippingAddress(cartOrder);
				will(returnValue(ExecutionResultFactory.createReadOK(shippingAddress)));
			}
		});

		ExecutionResult<String> result = destinationInfoLookupStrategy.findSelectedAddressIdForShipment(STORE_CODE, ORDER_GUID, SHIPMENT_ID);

		assertTrue(result.isSuccessful());
		assertEquals(ADDRESS_GUID, result.getData());
	}

	/**
	 * Test find selected address for shipment with shipping address not found.
	 */
	@Test
	public void testFindSelectedAddressForShipmentWithShippingAddressNotFound() {

		final CartOrder cartOrder = new CartOrderImpl();

		context.checking(new Expectations() {
			{
				allowing(mockCartOrderRepository).findByGuid(STORE_CODE, ORDER_GUID);
				will(returnValue(ExecutionResultFactory.createReadOK(cartOrder)));

				allowing(mockCartOrderRepository).getShippingAddress(cartOrder);
				will(returnValue(ExecutionResultFactory.createNotFound("Address not found")));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		destinationInfoLookupStrategy.findSelectedAddressIdForShipment(STORE_CODE, ORDER_GUID, SHIPMENT_ID);
	}

	/**
	 * Test find selected address for shipment with cart order not found.
	 */
	@Test
	public void testFindSelectedAddressForShipmentWithCartOrderNotFound() {

		context.checking(new Expectations() {
			{
				allowing(mockCartOrderRepository).findByGuid(STORE_CODE, ORDER_GUID);
				will(returnValue(ExecutionResultFactory.createNotFound(StringUtils.EMPTY)));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		destinationInfoLookupStrategy.findSelectedAddressIdForShipment(STORE_CODE, ORDER_GUID, SHIPMENT_ID);
	}
}
