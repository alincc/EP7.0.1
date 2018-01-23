/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.DestinationInfoLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.integration.DestinationInfoLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.integration.ShipmentDetailsLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.integration.dto.ShipmentDetailsDto;
import com.elasticpath.rest.id.util.Base32Util;


/**
 * Test class for {@link DestinationInfoLookupImpl}.
 */
public final class DestinationInfoLookupImplTest {

	private static final String DECODED_ADDRESS_ID = "decoded_address_id";
	private static final String DECODED_ORDER_ID = "decoded_order_id";
	private static final String SHIPMENT_ID = "decoded_shipment_id";
	private static final String SCOPE = "scope";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final DestinationInfoLookupStrategy mockDestinationInfoLookupStrategy = context.mock(DestinationInfoLookupStrategy.class);
	private final ShipmentDetailsLookupStrategy mockShipmentDetailsLookupStrategy = context.mock(ShipmentDetailsLookupStrategy.class);
	private final DestinationInfoLookup destinationLookup = new DestinationInfoLookupImpl(mockDestinationInfoLookupStrategy,
			mockShipmentDetailsLookupStrategy);


	/**
	 * Test find selected address id for shipment.
	 */
	@Test
	public void testFindSelectedAddressIdForShipment() {

		final ShipmentDetailsDto shipmentDetails = ResourceTypeFactory.createResourceEntity(ShipmentDetailsDto.class);
		shipmentDetails.setOrderCorrelationId(DECODED_ORDER_ID);

		context.checking(new Expectations() {
			{
				allowing(mockShipmentDetailsLookupStrategy).getShipmentDetail(SCOPE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(shipmentDetails)));

				allowing(mockDestinationInfoLookupStrategy).findSelectedAddressIdForShipment(SCOPE, DECODED_ORDER_ID,
					SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(DECODED_ADDRESS_ID)));
			}
		});

		ExecutionResult<String> result = destinationLookup.findSelectedAddressIdForShipment(SCOPE, SHIPMENT_ID);
		String expectedId = Base32Util.encode(DECODED_ADDRESS_ID);

		assertTrue(result.isSuccessful());
		assertEquals(expectedId, result.getData());
	}

	/**
	 * Test find selected address id for shipment with no address found.
	 */
	@Test
	public void testFindSelectedAddressIdForShipmentWithNoAddressFound() {

		final ShipmentDetailsDto shipmentDetails = ResourceTypeFactory.createResourceEntity(ShipmentDetailsDto.class);
		shipmentDetails.setOrderCorrelationId(DECODED_ORDER_ID);

		context.checking(new Expectations() {
			{
				allowing(mockShipmentDetailsLookupStrategy).getShipmentDetail(SCOPE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(shipmentDetails)));

				allowing(mockDestinationInfoLookupStrategy).findSelectedAddressIdForShipment(SCOPE, DECODED_ORDER_ID,
					SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createNotFound(StringUtils.EMPTY)));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		destinationLookup.findSelectedAddressIdForShipment(SCOPE, SHIPMENT_ID);
	}
}
