/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.deliveries.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.orders.integration.epcommerce.deliveries.transform.DeliveryTransformer;
import com.elasticpath.rest.resource.orders.integration.epcommerce.deliveries.wrapper.DeliveryWrapper;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link DeliveryLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeliveryLookupStrategyImplTest {

	private static final String OPERATION_SHOULD_BE_SUCCESSFUL = "Operation should be successful";
	private static final String SHIPMENT_SHIPMENT_TYPE_ID = "SHIPMENT";
	private static final String STORE_CODE = "store_code";
	private static final String CART_GUID = "cart_guid";
	private static final String ORDER_GUID = "order_guid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private DeliveryTransformer deliveryTransformer;

	@InjectMocks
	private DeliveryLookupStrategyImpl deliveryLookupStrategy;

	/**
	 * Test get delivery IDs.
	 */
	@Test
	public void testGetDeliveryIds() {
		CartOrder cartOrder = createMockCartOrder();
		Set<ShipmentType> shipmentTypes = createShipmentTypes(ShipmentType.ELECTRONIC, ShipmentType.PHYSICAL);
		ShoppingCart shoppingCart = createMockShoppingCart(shipmentTypes);

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		shouldGetShoppingCartWithResult(ExecutionResultFactory.createReadOK(shoppingCart));

		ExecutionResult<Collection<String>> result = deliveryLookupStrategy.getDeliveryIds(STORE_CODE, ORDER_GUID);

		List<String> expectedDeliveryIds = Arrays.asList(SHIPMENT_SHIPMENT_TYPE_ID);
		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertTrue("Collection of delivery ids do not match expected values.", CollectionUtil.containsOnly(expectedDeliveryIds, result.getData()));
	}

	/**
	 * Test get delivery Ids when cart order is not found not found.
	 */
	@Test
	public void testGetDeliveryIdsWhenCartOrderIsNotFoundNotFound() {
		shouldFindByGuidWithResult(ExecutionResultFactory.<CartOrder>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		deliveryLookupStrategy.getDeliveryIds(STORE_CODE, ORDER_GUID);
	}

	/**
	 * Test get delivery Ids when shopping cart is not found not found.
	 */
	@Test
	public void testGetDeliveryIdsWhenShoppingCartIsNotFoundNotFound() {
		CartOrder cartOrder = createMockCartOrder();

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		shouldGetShoppingCartWithResult(ExecutionResultFactory.<ShoppingCart>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		deliveryLookupStrategy.getDeliveryIds(STORE_CODE, ORDER_GUID);
	}

	/**
	 * Test find by code and order ID.
	 */
	@Test
	public void testFindByIdAndOrderId() {
		CartOrder cartOrder = createMockCartOrder();
		Set<ShipmentType> shipmentTypes = createShipmentTypes(ShipmentType.PHYSICAL);
		ShoppingCart shoppingCart = createMockShoppingCart(shipmentTypes);
		DeliveryWrapper deliveryWrapper = createDeliveryWrapper(SHIPMENT_SHIPMENT_TYPE_ID);
		DeliveryEntity deliveryEntity = DeliveryEntity.builder().build();

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		shouldGetShoppingCartWithResult(ExecutionResultFactory.createReadOK(shoppingCart));
		shouldTransformToEntity(deliveryWrapper, deliveryEntity);

		ExecutionResult<DeliveryEntity> result = deliveryLookupStrategy.findByIdAndOrderId(STORE_CODE, ORDER_GUID, SHIPMENT_SHIPMENT_TYPE_ID);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("Delivery DTO does not match expected value.", deliveryEntity, result.getData());
	}

	private DeliveryWrapper createDeliveryWrapper(final String deliveryCode) {
		DeliveryWrapper deliveryWrapper = ResourceTypeFactory.createResourceEntity(DeliveryWrapper.class);
		deliveryWrapper.setDeliveryCode(deliveryCode)
				.setShipmentType(deliveryCode);
		return deliveryWrapper;
	}

	/**
	 * Test find by code and order id when delivery id not found.
	 */
	@Test
	public void testFindByIdAndOrderIdWhenDeliveryIdNotFound() {
		Set<ShipmentType> shipmentTypes = createShipmentTypes(ShipmentType.ELECTRONIC);
		ShoppingCart shoppingCart = createMockShoppingCart(shipmentTypes);
		CartOrder cartOrder = createMockCartOrder();

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		shouldGetShoppingCartWithResult(ExecutionResultFactory.createReadOK(shoppingCart));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		deliveryLookupStrategy.findByIdAndOrderId(STORE_CODE, ORDER_GUID, SHIPMENT_SHIPMENT_TYPE_ID);
	}

	/**
	 * Test find by code and order id when error on get deliveries.
	 */
	@Test
	public void testFindByIdAndOrderIdWhenErrorOnGetDeliveries() {
		shouldFindByGuidWithResult(ExecutionResultFactory.<CartOrder>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		deliveryLookupStrategy.findByIdAndOrderId(STORE_CODE, ORDER_GUID, SHIPMENT_SHIPMENT_TYPE_ID);
	}

	private void shouldFindByGuidWithResult(final ExecutionResult<CartOrder> result) {
		when(cartOrderRepository.findByGuid(STORE_CODE, ORDER_GUID)).thenReturn(result);
	}

	private void shouldGetShoppingCartWithResult(final ExecutionResult<ShoppingCart> result) {
		when(shoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(result);
	}

	private void shouldTransformToEntity(final DeliveryWrapper deliveryWrapper, final DeliveryEntity deliveryEntity) {
		when(deliveryTransformer.transformToEntity(deliveryWrapper)).thenReturn(deliveryEntity);
	}

	private ShoppingCart createMockShoppingCart(final Set<ShipmentType> shipmentTypes) {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		stub(shoppingCart.getShipmentTypes()).toReturn(shipmentTypes);
		return shoppingCart;
	}

	private CartOrder createMockCartOrder() {
		CartOrder cartOrder = mock(CartOrder.class);
		stub(cartOrder.getShoppingCartGuid()).toReturn(CART_GUID);
		return cartOrder;
	}

	private Set<ShipmentType> createShipmentTypes(final ShipmentType... shipmentTypes) {
		return new HashSet<>(Arrays.asList(shipmentTypes));
	}
}
