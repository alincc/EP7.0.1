/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.integration.DestinationInfoWriterStrategy;
import com.elasticpath.rest.resource.shipmentdetails.integration.ShipmentDetailsLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.integration.dto.ShipmentDetailsDto;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Test class for {@link DestinationInfoWriterStrategyImpl}.
 */
public class DestinationInfoWriterStrategyImplTest {

	private static final String SHIPMENT_ID = "shipment_id";

	private static final String ADDRESS_GUID = "address_guid";

	private static final String ORDER_GUID = "order_guid";

	private static final String STORE_CODE = "store_code";


	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final CartOrderRepository mockCartOrderRepository = context.mock(CartOrderRepository.class);

	private final ShipmentDetailsLookupStrategy mockShipmentDetailsLookupStrategy = context.mock(ShipmentDetailsLookupStrategy.class);

	private final CartOrder cartOrder = context.mock(CartOrder.class);

	/**
	 * Test update delivery address for shipment.
	 */
	@Test
	public void testUpdateDeliveryAddressForShipmentForNewAddress() {
		DestinationInfoWriterStrategy strategy = createAddressInfoWriterStrategy();

		final ShipmentDetailsDto shipmentDto = createShipmentDetailsDto();

		context.checking(new Expectations() {


			{
				allowing(mockShipmentDetailsLookupStrategy).getShipmentDetail(STORE_CODE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(shipmentDto)));

				oneOf(mockCartOrderRepository).updateShippingAddressOnCartOrder(ADDRESS_GUID, ORDER_GUID, STORE_CODE);
				will(returnValue(ExecutionResultFactory.createReadOK(false)));
				
				allowing(mockCartOrderRepository).findByGuid(STORE_CODE, ORDER_GUID);
				will(returnValue(ExecutionResultFactory.createReadOK(cartOrder)));
				
				allowing(cartOrder).getShippingAddressGuid();
				will(returnValue(null));
			}
		});

		ExecutionResult<Void> result = strategy.updateShippingAddressForShipment(STORE_CODE, SHIPMENT_ID, ADDRESS_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.resourceStatus(ResourceStatus.CREATE_OK);
	}
	
	/**
	 * Test update delivery address for shipment.
	 */
	@Test
	public void testUpdateDeliveryAddressForShipmentForExistingAddress() {
		DestinationInfoWriterStrategy strategy = createAddressInfoWriterStrategy();

		final ShipmentDetailsDto shipmentDto = createShipmentDetailsDto();

		context.checking(new Expectations() {


			{
				allowing(mockShipmentDetailsLookupStrategy).getShipmentDetail(STORE_CODE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(shipmentDto)));

				oneOf(mockCartOrderRepository).updateShippingAddressOnCartOrder(ADDRESS_GUID, ORDER_GUID, STORE_CODE);
				will(returnValue(ExecutionResultFactory.createReadOK(false)));
				
				allowing(mockCartOrderRepository).findByGuid(STORE_CODE, ORDER_GUID);
				will(returnValue(ExecutionResultFactory.createReadOK(cartOrder)));
				
				allowing(cartOrder).getShippingAddressGuid();
				will(returnValue(ADDRESS_GUID));
			}
		});

		ExecutionResult<Void> result = strategy.updateShippingAddressForShipment(STORE_CODE, SHIPMENT_ID, ADDRESS_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.resourceStatus(ResourceStatus.READ_OK);
	}
	
	/**
	 * Test update delivery address for shipment.
	 */
	@Test
	public void testUpdateDeliveryAddressWhenCartOrderIsNotFound() {
		DestinationInfoWriterStrategy strategy = createAddressInfoWriterStrategy();

		final ShipmentDetailsDto shipmentDto = createShipmentDetailsDto();

		context.checking(new Expectations() {

			{
				allowing(mockShipmentDetailsLookupStrategy).getShipmentDetail(STORE_CODE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(shipmentDto)));
				
				allowing(mockCartOrderRepository).findByGuid(STORE_CODE, ORDER_GUID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.updateShippingAddressForShipment(STORE_CODE, SHIPMENT_ID, ADDRESS_GUID);
	}

	/**
	 * Test update when shipment is not found.
	 */
	@Test
	public void testUpdateWhenShipmentIsNotFound() {
		DestinationInfoWriterStrategy strategy = createAddressInfoWriterStrategy();

		context.checking(new Expectations() {
			{
				allowing(mockShipmentDetailsLookupStrategy).getShipmentDetail(STORE_CODE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createServerError(StringUtils.EMPTY)));

			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		strategy.updateShippingAddressForShipment(STORE_CODE, SHIPMENT_ID, ADDRESS_GUID);
	}
	
	/**
	 * Test update when shipment is not found.
	 */
	@Test
	public void testUpdateWhenShipmentAddressFailsWhenUpdatingAddressOnCartOrder() {
		DestinationInfoWriterStrategy strategy = createAddressInfoWriterStrategy();
		final ShipmentDetailsDto shipmentDto = createShipmentDetailsDto();
		context.checking(new Expectations() {


			{
				allowing(mockShipmentDetailsLookupStrategy).getShipmentDetail(STORE_CODE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(shipmentDto)));
				
				allowing(mockCartOrderRepository).findByGuid(STORE_CODE, ORDER_GUID);
				will(returnValue(ExecutionResultFactory.createReadOK(cartOrder)));
				
				allowing(cartOrder).getShippingAddressGuid();
				will(returnValue(null));
				
				oneOf(mockCartOrderRepository).updateShippingAddressOnCartOrder(ADDRESS_GUID, ORDER_GUID, STORE_CODE);
				will(returnValue(ExecutionResultFactory.createServerError("")));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		strategy.updateShippingAddressForShipment(STORE_CODE, SHIPMENT_ID, ADDRESS_GUID);
	}

	private ShipmentDetailsDto createShipmentDetailsDto() {
		return ResourceTypeFactory.createResourceEntity(ShipmentDetailsDto.class).setOrderCorrelationId(ORDER_GUID);
	}

	private DestinationInfoWriterStrategy createAddressInfoWriterStrategy() {
		return new DestinationInfoWriterStrategyImpl(mockCartOrderRepository, mockShipmentDetailsLookupStrategy);
	}
}
