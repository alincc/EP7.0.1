/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

/**
 * The Class ShippingOptionWriterStrategyImplTest.
 */
public class ShippingOptionWriterStrategyImplTest {

	private static final String STORE_CODE = "storeCode";
	private static final String TEST_DELIVERY_METHOD_CODE = "test_delivery_method_code";
	private static final String TEST_SHIPMENT_DETAILS_ID = "test_shipment_details_id";

	private final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final CartOrderRepository mockCartOrderRepository = context.mock(CartOrderRepository.class);
	private final ShippingOptionWriterStrategyImpl shippingOptionWriterStrategy = new ShippingOptionWriterStrategyImpl(mockCartOrderRepository);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * Test select shipping option for shipment.
	 */
	@Test
	public void testSelectShippingOptionForShipment() {

		final CartOrder mockCartOrder = createCartOrder(null);

		context.checking(new Expectations() {
			{
				oneOf(mockCartOrderRepository).findByShipmentDetailsId(STORE_CODE, TEST_SHIPMENT_DETAILS_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(mockCartOrder)));

				oneOf(mockCartOrderRepository).saveCartOrder(mockCartOrder);
				will(returnValue(ExecutionResultFactory.createReadOK(mockCartOrder)));
			}
		});
		ExecutionResult<Boolean> result = shippingOptionWriterStrategy.selectShippingOptionForShipment(
				STORE_CODE, TEST_SHIPMENT_DETAILS_ID, TEST_DELIVERY_METHOD_CODE);

		assertTrue("Result should be successful", result.isSuccessful());
		assertFalse("Boolean result returned should be false", result.getData());
	}

	/**
	 * Test select shipping option for shipment when get order id for shipment detail fails.
	 */
	@Test
	public void testSelectShippingOptionForShipmentWhenGetOrderIdForShipmentDetailFails() {
		context.checking(new Expectations() {
			{
				oneOf(mockCartOrderRepository).findByShipmentDetailsId(STORE_CODE, TEST_SHIPMENT_DETAILS_ID);
				will(returnValue(ExecutionResultFactory.createNotFound("Order not found")));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shippingOptionWriterStrategy.selectShippingOptionForShipment(
				STORE_CODE, TEST_SHIPMENT_DETAILS_ID, TEST_DELIVERY_METHOD_CODE);
	}

	/**
	 * Test select shipping option for shipment when there was no previous selection.
	 */
	@Test
	public void testSelectShippingOptionForShipmentWithPreviousSelection() {

		final CartOrder mockCartOrder = createCartOrder("Previous Guid");

		context.checking(new Expectations() {
			{
				oneOf(mockCartOrderRepository).findByShipmentDetailsId(STORE_CODE, TEST_SHIPMENT_DETAILS_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(mockCartOrder)));

				oneOf(mockCartOrderRepository).saveCartOrder(mockCartOrder);
				will(returnValue(ExecutionResultFactory.createReadOK(mockCartOrder)));
			}
		});
		ExecutionResult<Boolean> result = shippingOptionWriterStrategy.selectShippingOptionForShipment(
				STORE_CODE, TEST_SHIPMENT_DETAILS_ID, TEST_DELIVERY_METHOD_CODE);

		assertTrue("Result should be successful", result.isSuccessful());
		assertTrue("Boolean result returned should be true", result.getData());
	}

	/**
	 * Test select shipping option for shipment when save cart order throws exception.
	 */
	@Test
	public void testSelectShippingOptionForShipmentWhenSaveCartOrderFails() {
		final CartOrder mockCartOrder = createCartOrder(null);

		context.checking(new Expectations() {
			{
				oneOf(mockCartOrderRepository).findByShipmentDetailsId(STORE_CODE, TEST_SHIPMENT_DETAILS_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(mockCartOrder)));

				oneOf(mockCartOrderRepository).saveCartOrder(mockCartOrder);
				will(returnValue(ExecutionResultFactory.createServerError("Failure saving cart order")));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		shippingOptionWriterStrategy.selectShippingOptionForShipment(
				STORE_CODE, TEST_SHIPMENT_DETAILS_ID, TEST_DELIVERY_METHOD_CODE);
	}

	/**
	 * Creates the cart order.
	 *
	 * @param shippingServiceLevelGuid the shipping service level guid
	 * @return the cart order
	 */
	private CartOrder createCartOrder(final String shippingServiceLevelGuid) {
		final CartOrder mockCartOrder = context.mock(CartOrder.class);

		context.checking(new Expectations() {
			{
				oneOf(mockCartOrder).getShippingServiceLevelGuid();
				will(returnValue(shippingServiceLevelGuid));

				oneOf(mockCartOrder).setShippingServiceLevelGuid(TEST_DELIVERY_METHOD_CODE);
			}
		});

		return mockCartOrder;
	}
}
