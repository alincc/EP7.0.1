/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.impl;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository.FindCartOrder.BY_SHIPMENT_DETAILS_ID;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingServiceLevelRepository;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Test that {@link ShippingServiceLevelRepositoryImpl} behaves as expected.
 */
public class ShippingServiceLevelRepositoryImplTest {

	private static final String STORE_CODE = "store";

	private static final String SHIPMENT_DETAILS_ID = "shipment Id";

	private static final String SHIPPING_SERVICE_LEVEL_GUID = "ssl guid";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final CartOrderRepository mockCartOrderRepository = context.mock(CartOrderRepository.class);

	private final ShoppingCart mockShoppingCart = context.mock(ShoppingCart.class);

	private final ShippingServiceLevel shippingServiceLevel = context.mock(ShippingServiceLevel.class);
	private final Address shippingAddress = context.mock(Address.class);

	private final List<ShippingServiceLevel> shippingServiceLevelList = Collections.singletonList(shippingServiceLevel);
	private final List<String> shippingServiceLevelGuidsList = Collections.singletonList(SHIPPING_SERVICE_LEVEL_GUID);

	private final ShippingServiceLevelRepository repository = new ShippingServiceLevelRepositoryImpl(mockCartOrderRepository);

	/**
	 * Test the behaviour of find shipping service levels for shipment.
	 */
	@Test
	public void testFindShippingServiceLevelsForShipment() {
		allowingShippingServiceListToBeRetrieved();

		ExecutionResult<Collection<String>> result = repository.findShippingServiceLevelGuidsForShipment(STORE_CODE, SHIPMENT_DETAILS_ID);
		
		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(shippingServiceLevelGuidsList);
	}

	/**
	 * Test the behavior of find shipping service levels for shipment when cart order not found.
	 */
	@Test
	public void testFindShippingServiceLevelsForShipmentWhenCartOrderNotFound() {
		allowingCartOrderNotFound();
		
		ExecutionResult<Collection<String>> result = repository.findShippingServiceLevelGuidsForShipment(STORE_CODE, SHIPMENT_DETAILS_ID);
		
		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of find by guid.
	 */
	@Test
	public void testFindByGuid() {
		allowingShippingServiceListToBeRetrieved();

		ExecutionResult<ShippingServiceLevel> result = repository.findByGuid(STORE_CODE, SHIPMENT_DETAILS_ID, SHIPPING_SERVICE_LEVEL_GUID);
		
		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(shippingServiceLevel);
	}

	/**
	 * Test the behaviour of find by guid when not found.
	 */
	@Test
	public void testFindByGuidWhenNotFound() {
		context.checking(new Expectations() {
			{
				oneOf(shippingServiceLevel).getGuid();
				will(returnValue("OTHER_GUID"));
			}
		});
		allowingShippingServiceListToBeRetrieved();

		ExecutionResult<ShippingServiceLevel> result = repository.findByGuid(STORE_CODE, SHIPMENT_DETAILS_ID, SHIPPING_SERVICE_LEVEL_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of get selected shipping service level for shipment.
	 */
	@Test
	public void testGetSelectedShippingServiceLevelForShipment() {
		final CartOrder cartOrder = allowingShippingServiceListToBeRetrieved();
		context.checking(new Expectations() {
			{
				allowing(mockShoppingCart).getSelectedShippingServiceLevel();
				will(returnValue(shippingServiceLevel));
				
				allowing(cartOrder).getShippingServiceLevelGuid();
				will(returnValue(SHIPPING_SERVICE_LEVEL_GUID));

				allowing(shippingServiceLevel).getGuid();
				will(returnValue(SHIPPING_SERVICE_LEVEL_GUID));

			}
		});

		ExecutionResult<String> result = repository.getSelectedShipmentOptionIdForShipmentDetails(STORE_CODE, SHIPMENT_DETAILS_ID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(SHIPPING_SERVICE_LEVEL_GUID);
	}

	/**
	 * Test the behaviour of get selected shipping service level for shipment when cart order not found.
	 */
	@Test
	public void testGetSelectedShippingServiceLevelForShipmentWhenCartOrderNotFound() {
		allowingCartOrderNotFound();

		ExecutionResult<String> result = repository.getSelectedShipmentOptionIdForShipmentDetails(STORE_CODE, SHIPMENT_DETAILS_ID);
		
		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of get selected shipping service level for shipment when cart order has no service level guid.
	 */
	@Test
	public void testGetSelectedShippingServiceLevelForShipmentWhenCartOrderHasNoServiceLevelGuid() {
		final CartOrder cartOrder = allowingShippingServiceListToBeRetrieved();
		context.checking(new Expectations() {
			{
				allowing(cartOrder).getShippingServiceLevelGuid();
				will(returnValue(null));
			}
		});

		ExecutionResult<String> result = repository.getSelectedShipmentOptionIdForShipmentDetails(STORE_CODE, SHIPMENT_DETAILS_ID);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of get selected shipping service level for shipment when shipping service level not found.
	 */
	@Test
	public void testGetSelectedShippingServiceLevelForShipmentWhenShippingServiceLevelNotFound() {
		context.checking(new Expectations() {
			{
				allowing(mockCartOrderRepository).findShippingServiceLevels(STORE_CODE, shippingAddress);
				will(returnValue(Collections.emptyList()));
			}
		});

		final CartOrder cartOrder = allowingShippingServiceListToBeRetrieved();
		context.checking(new Expectations() {
			{
				allowing(cartOrder).getShippingServiceLevelGuid();
				will(returnValue(SHIPPING_SERVICE_LEVEL_GUID));

			}
		});

		ExecutionResult<String> result = repository.getSelectedShipmentOptionIdForShipmentDetails(STORE_CODE, SHIPMENT_DETAILS_ID);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.SERVER_ERROR);
	}
	
	/**
	 * Test the behaviour of get selected shipping service level for shipment when shipping service level not found.
	 */
	@Test
	public void testGetSelectedShippingServiceLevelForShipmentWhenSelectedLevelsAreOutOfSync() {
		context.checking(new Expectations() {
			{
				allowing(shippingServiceLevel).getGuid();
				will(returnValue("mismatch-shipping-service-level-guid."));
			}
		});

		final CartOrder cartOrder = allowingShippingServiceListToBeRetrieved();
		context.checking(new Expectations() {
			{
				allowing(cartOrder).getShippingServiceLevelGuid();
				will(returnValue(SHIPPING_SERVICE_LEVEL_GUID));
			}
		});

		ExecutionResult<String> result = repository.getSelectedShipmentOptionIdForShipmentDetails(STORE_CODE, SHIPMENT_DETAILS_ID);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	private CartOrder allowingShippingServiceListToBeRetrieved() {
		final CartOrder cartOrder = context.mock(CartOrder.class);

		context.checking(new Expectations() {
			{
				atLeast(1).of(mockCartOrderRepository).getCartOrder(STORE_CODE, SHIPMENT_DETAILS_ID, BY_SHIPMENT_DETAILS_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(cartOrder)));

				oneOf(mockCartOrderRepository).getShippingAddress(cartOrder);
				will(returnValue(ExecutionResultFactory.createReadOK(shippingAddress)));

				allowing(mockCartOrderRepository).findShippingServiceLevels(STORE_CODE, shippingAddress);
				will(returnValue(shippingServiceLevelList));

				allowing(shippingServiceLevel).getGuid();
				will(returnValue(SHIPPING_SERVICE_LEVEL_GUID));

			}
		});
		return cartOrder;
	}

	private void allowingCartOrderNotFound() {
		context.checking(new Expectations() {
			{
				oneOf(mockCartOrderRepository).getCartOrder(STORE_CODE, SHIPMENT_DETAILS_ID, BY_SHIPMENT_DETAILS_ID);
				will(returnValue(ExecutionResultFactory.createNotFound("cart order not found")));
			}
		});
	}

}
