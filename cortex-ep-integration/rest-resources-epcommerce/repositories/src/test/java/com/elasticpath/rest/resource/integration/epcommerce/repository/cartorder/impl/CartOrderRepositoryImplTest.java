/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.PersistenceException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.test.AssertExecutionResult;
import com.elasticpath.service.cartorder.CartOrderCouponService;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.cartorder.CartOrderShippingService;
import com.elasticpath.service.rules.CartOrderCouponAutoApplier;

/**
 * Test that {@link CartOrderRepositoryImpl} behaves as expected.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public class CartOrderRepositoryImplTest {

	private static final String STORE_CODE = "store";
	private static final String CART_ORDER_GUID = "cart order guid";
	private static final String CART_GUID = "cart guid";
	private static final String CUSTOMER_GUID = "customer guid";
	private static final String SHIPPING_ADDRESS_GUID = "SHIPPING_ADDRESS_GUID";

	@Mock
	private CartOrderService cartOrderService;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private CartOrderShippingService cartOrderShippingService;

	@Mock
	private CartOrderCouponService cartOrderCouponService;

	@InjectMocks
	private CartOrderRepositoryImpl repository;

	@Mock
	private CartOrder cartOrder;

	@Mock
	private CartOrderCouponAutoApplier cartOrderCouponAutoApplier;

	@Before
	public void setUp() {
		allowingValidCartOrder();
	}

	/**
	 * Test the behaviour of find by guid.
	 */
	@Test
	public void testFindByStoreCodeAndGuid() {
		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);

		allowingCartOrderStoreCodeToBe(STORE_CODE);

		ExecutionResult<CartOrder> result = repository.findByGuid(STORE_CODE, CART_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(cartOrder);
	}

	/**
	 * Test the behaviour of find by guid not found.
	 */
	@Test
	public void testFindByStoreCodeAndGuidNotFound() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(null);

		ExecutionResult<CartOrder> result = repository.findByGuid(STORE_CODE, CART_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of find by cart guid.
	 */
	@Test
	public void testFindByCartGuid() {

		when(cartOrderService.findByShoppingCartGuid(CART_GUID)).thenReturn(cartOrder);

		ExecutionResult<CartOrder> result = repository.findByCartGuid(CART_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful();
	}

	/**
	 * Test the behaviour of find by cart guid when not found.
	 */
	@Test
	public void testFindByCartGuidWhenNotFound() {

		when(cartOrderService.findByShoppingCartGuid(CART_GUID)).thenReturn(null);

		ExecutionResult<CartOrder> result = repository.findByCartGuid(CART_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of find by shipment details id.
	 */
	@Test
	public void testFindByShipmentDetailsId() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);

		ExecutionResult<CartOrder> result = repository.findByShipmentDetailsId(STORE_CODE, getShipmentId());

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(cartOrder);
	}

	/**
	 * Test the behaviour of find by shipment details id with invalid id.
	 */
	@Test
	public void testFindByShipmentDetailsIdWithInvalidId() {

		ExecutionResult<CartOrder> result = repository.findByShipmentDetailsId(STORE_CODE, "invalid");

		AssertExecutionResult.assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	/**
	 * Test the behaviour of find cart order guids by customer.
	 */
	@Test
	public void testFindCartOrderGuidsByCustomer() {
		List<String> listOfCartOrderGuids = Collections.singletonList(CART_ORDER_GUID);
		when(cartOrderService.findCartOrderGuidsByCustomerGuid(STORE_CODE, CUSTOMER_GUID)).thenReturn(listOfCartOrderGuids);

		ExecutionResult<Collection<String>> result = repository.findCartOrderGuidsByCustomer(STORE_CODE, CUSTOMER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(listOfCartOrderGuids);
	}

	/**
	 * Test the behaviour of find cart order guids by customer when none found.
	 */
	@Test
	public void testFindCartOrderGuidsByCustomerWhenNoneFound() {
		when(cartOrderService.findCartOrderGuidsByCustomerGuid(STORE_CODE, CUSTOMER_GUID)).thenReturn(null);

		ExecutionResult<Collection<String>> result = repository.findCartOrderGuidsByCustomer(STORE_CODE, CUSTOMER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
		.isFailure()
		.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of get billing address.
	 */
	@Test
	public void testGetBillingAddress() {
		final Address address = mock(Address.class);
		when(cartOrderService.getBillingAddress(cartOrder)).thenReturn(address);

		ExecutionResult<Address> result = repository.getBillingAddress(cartOrder);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(address);
	}

	/**
	 * Test the behaviour of get billing address when none found.
	 */
	@Test
	public void testGetBillingAddressWhenNoneFound() {
		when(cartOrderService.getBillingAddress(cartOrder)).thenReturn(null);

		ExecutionResult<Address> result = repository.getBillingAddress(cartOrder);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of get shipping address.
	 */
	@Test
	public void testGetShippingAddress() {
		Address address = mock(Address.class);
		when(cartOrderService.getShippingAddress(cartOrder)).thenReturn(address);

		ExecutionResult<Address> result = repository.getShippingAddress(cartOrder);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(address);
	}

	/**
	 * Test the behaviour of get shipping address when none found.
	 */
	@Test
	public void testGetShippingAddressWhenNoneFound() {
		when(cartOrderService.getShippingAddress(cartOrder)).thenReturn(null);

		ExecutionResult<Address> result = repository.getShippingAddress(cartOrder);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of save cart order.
	 */
	@Test
	public void testSaveCartOrder() {
		final CartOrder savedCartOrder = mock(CartOrder.class, "saved");
		when(cartOrderService.saveOrUpdate(cartOrder)).thenReturn(savedCartOrder);

		ExecutionResult<CartOrder> result = repository.saveCartOrder(cartOrder);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(savedCartOrder);
	}

	/**
	 * Test the behaviour of save cart order with exception.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSaveCartOrderWithException() {
		when(cartOrderService.saveOrUpdate(cartOrder)).thenThrow(PersistenceException.class);

		ExecutionResult<CartOrder> result = repository.saveCartOrder(cartOrder);

		AssertExecutionResult.assertExecutionResult(result)
		.isFailure()
		.resourceStatus(ResourceStatus.SERVER_ERROR);
	}
	/**
	 * Test the behaviour of get enriched shopping cart.
	 */
	@Test
	public void testGetEnrichedShoppingCart() {
		final ShoppingCart shoppingCart = mock(ShoppingCart.class);
		final ShoppingCart enrichedShoppingCart = mock(ShoppingCart.class, "enriched");
		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(shoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(cartOrderCouponService.populateCouponCodesOnShoppingCart(shoppingCart, cartOrder)).thenReturn(enrichedShoppingCart);
		when(cartOrderShippingService.populateAddressAndShippingFields(enrichedShoppingCart, cartOrder)).thenReturn(enrichedShoppingCart);

		ExecutionResult<ShoppingCart> result = repository.getEnrichedShoppingCart(STORE_CODE, CART_ORDER_GUID,
																						CartOrderRepository.FindCartOrder.BY_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(enrichedShoppingCart);
	}

	/**
	 * Test the behaviour of get enriched shopping cart when order not found.
	 */
	@Test
	public void testGetEnrichedShoppingCartWhenOrderNotFound() {
		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(null);

		ExecutionResult<ShoppingCart> result = repository.getEnrichedShoppingCart(STORE_CODE, CART_ORDER_GUID,
																						CartOrderRepository.FindCartOrder.BY_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of get enriched shopping cart when shopping cart not found.
	 */
	@Test
	public void testGetEnrichedShoppingCartWhenShoppingCartNotFound() {
		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(shoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(ExecutionResultFactory.<ShoppingCart>createNotFound());

		ExecutionResult<ShoppingCart> result = repository.getEnrichedShoppingCart(STORE_CODE, CART_ORDER_GUID,
																						CartOrderRepository.FindCartOrder.BY_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testUpdateShippingAddressReturnsTrueWhenAddressIsUpdated() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(cartOrderShippingService.updateCartOrderShippingAddress(SHIPPING_ADDRESS_GUID, cartOrder, STORE_CODE)).thenReturn(true);

		ExecutionResult<Boolean> result = repository.updateShippingAddressOnCartOrder(SHIPPING_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);

		verify(cartOrderService, times(1)).saveOrUpdate(cartOrder);
		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(true);
	}

	@Test
	public void testUpdateShippingAddressReturnsFalseWhenAddressIsNotUpdated() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(cartOrderShippingService.updateCartOrderShippingAddress(SHIPPING_ADDRESS_GUID, cartOrder, STORE_CODE)).thenReturn(false);

		ExecutionResult<Boolean> result = repository.updateShippingAddressOnCartOrder(SHIPPING_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);

		verify(cartOrderService, times(0)).saveOrUpdate(any(CartOrder.class));
		AssertExecutionResult.assertExecutionResult(result)
			.isSuccessful()
			.data(false);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateShippingAddressReturnsServerErrorWhenFailsToSave() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(cartOrderShippingService.updateCartOrderShippingAddress(SHIPPING_ADDRESS_GUID, cartOrder, STORE_CODE)).thenReturn(true);
		when(cartOrderService.saveOrUpdate(cartOrder)).thenThrow(EpPersistenceException.class);

		ExecutionResult<Boolean> result = repository.updateShippingAddressOnCartOrder(SHIPPING_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);

		AssertExecutionResult.assertExecutionResult(result)
			.isFailure()
			.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void testFilterAndAutoApplyCoupons() {
		CartOrder cartOrder = new CartOrderImpl();
		Store store = new StoreImpl();
		String customerEmail = "customerEmail";

		when(cartOrderCouponAutoApplier.filterAndAutoApplyCoupons(any(CartOrder.class), any(Store.class), anyString())).thenReturn(true);

		ExecutionResult<Boolean> booleanExecutionResult = repository.filterAndAutoApplyCoupons(cartOrder, store, customerEmail);

		verify(cartOrderCouponAutoApplier).filterAndAutoApplyCoupons(any(CartOrder.class), any(Store.class), anyString());
		assertEquals(true, booleanExecutionResult.getData());
	}

	private void allowingValidCartOrder() {
		when(cartOrder.getGuid()).thenReturn(CART_ORDER_GUID);
		when(cartOrder.getShoppingCartGuid()).thenReturn(CART_GUID);
	}

	private String getShipmentId() {
		Map<String, String> shipmentFieldValues = new TreeMap<>();
		shipmentFieldValues.put(DeliveryEntity.ORDER_ID_PROPERTY, CART_ORDER_GUID);
		shipmentFieldValues.put(DeliveryEntity.DELIVERY_ID_PROPERTY, "delivery id");
		return CompositeIdUtil.encodeFieldValueMap(shipmentFieldValues);
	}

	private void allowingCartOrderStoreCodeToBe(final String storeCode) {
		when(cartOrderService.findByStoreCodeAndGuid(storeCode, CART_ORDER_GUID)).thenReturn(cartOrder);
	}

}
