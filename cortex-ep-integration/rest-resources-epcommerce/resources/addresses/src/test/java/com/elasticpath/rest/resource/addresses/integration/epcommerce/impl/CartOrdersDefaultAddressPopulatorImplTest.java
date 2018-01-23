/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.epcommerce.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

@RunWith(MockitoJUnitRunner.class)
public class CartOrdersDefaultAddressPopulatorImplTest {
	private static final String NEW_ADDRESS_GUID = "NEW_ADDRESS_GUID";

	private static final String EXISTING_ADDRESS_GUID = "EXISTING_ADDRESS_GUID";

	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";

	private static final String CUSTOMER_GUID = "CUSTOMER_GUID";

	private static final String STORE_CODE = "STORE_CODE";

	private final Collection<String> cartOrderGuids = new ArrayList<>();

	@Mock
	private CartOrderRepository cartOrderRepository;

	@InjectMocks
	private CartOrdersDefaultAddressPopulatorImpl cartOrdersDefaultAddressPopulator;
	
	@Mock
	private CustomerAddress mockAddress;
	
	@Mock
	private Customer mockCustomer;

	@Mock
	private CartOrder mockCartOrder;
	
	@Before
	public void setUp() {
		when(mockCustomer.getGuid()).thenReturn(CUSTOMER_GUID);
		when(mockAddress.getGuid()).thenReturn(NEW_ADDRESS_GUID);
		when(mockCartOrder.getGuid()).thenReturn(CART_ORDER_GUID);
	}

	@Test
	public void testUpdateBillingAddressOnCartOrdersSuccessfully() throws Exception {
		setUpSuccessfulCartOrderRetrieval();
		allowingCartOrderBillingAddressToBe(null);
		
		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, true, false);
		
		verify(mockCartOrder, times(1)).setBillingAddressGuid(NEW_ADDRESS_GUID);
		verify(cartOrderRepository, times(1)).saveCartOrder(mockCartOrder);
	}
	
	@Test
	public void testBillingAddressNotSetWhenExisting() throws Exception {
		setUpSuccessfulCartOrderRetrieval();
		allowingCartOrderBillingAddressToBe(EXISTING_ADDRESS_GUID);
		
		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, true, false);
		
		verify(mockCartOrder, times(0)).setBillingAddressGuid(NEW_ADDRESS_GUID);
		verify(cartOrderRepository, times(0)).saveCartOrder(mockCartOrder);
	}
	
	@Test
	public void testUpdateShippingAddressOnCartOrdersSuccessfully() throws Exception {
		setUpSuccessfulCartOrderRetrieval();
		allowingCartOrderShippingAddressToBe(null);
		
		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, false, true);
		
		verify(cartOrderRepository, times(1)).updateShippingAddressOnCartOrder(NEW_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);
	}
	
	@Test
	public void testShippingAddressNotSetWhenExisting() throws Exception {
		setUpSuccessfulCartOrderRetrieval();
		allowingCartOrderShippingAddressToBe(EXISTING_ADDRESS_GUID);
		
		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, false, true);
		
		verify(cartOrderRepository, times(0)).updateShippingAddressOnCartOrder(NEW_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);
	}
	
	@Test
	public void testNoAddressesSetWhenStoreNotValid() throws Exception {
		allowingCartOrderGuidsByCustomer(ExecutionResultFactory.<Collection<String>>createNotFound());
		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, true, true);
		
		verify(cartOrderRepository, times(0)).updateShippingAddressOnCartOrder(NEW_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);
		verify(mockCartOrder, times(0)).setBillingAddressGuid(NEW_ADDRESS_GUID);
		verify(cartOrderRepository, times(0)).saveCartOrder(mockCartOrder);
	}
	
	@Test
	public void testNoAddressesSetWhenNoCartOrdersFoundForCustomer() throws Exception {
		allowingCartOrderGuidsByCustomer(ExecutionResultFactory.<Collection<String>>createNotFound());
		
		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, true, true);
		
		verify(cartOrderRepository, times(0)).updateShippingAddressOnCartOrder(NEW_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);
		verify(mockCartOrder, times(0)).setBillingAddressGuid(NEW_ADDRESS_GUID);
		verify(cartOrderRepository, times(0)).saveCartOrder(mockCartOrder);
	}
	
	@Test
	public void testNoAddressesSetWhenNoCartOrderFoundForGuid() throws Exception {
		cartOrderGuids.add(CART_ORDER_GUID);
		allowingCartOrderGuidsByCustomer(ExecutionResultFactory.createReadOK(cartOrderGuids));
		allowingCartOrderForGuid(ExecutionResultFactory.<CartOrder>createNotFound());
		
		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, true, true);
		
		verify(cartOrderRepository, times(0)).updateShippingAddressOnCartOrder(NEW_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);
		verify(mockCartOrder, times(0)).setBillingAddressGuid(NEW_ADDRESS_GUID);
		verify(cartOrderRepository, times(0)).saveCartOrder(mockCartOrder);
	}
	
	private void setUpSuccessfulCartOrderRetrieval() {
		cartOrderGuids.add(CART_ORDER_GUID);
		allowingCartOrderGuidsByCustomer(ExecutionResultFactory.createReadOK(cartOrderGuids));
		allowingCartOrderForGuid(ExecutionResultFactory.createReadOK(mockCartOrder));
	}

	private void allowingCartOrderShippingAddressToBe(final String existingAddressGuid) {
		when(mockCartOrder.getShippingAddressGuid()).thenReturn(existingAddressGuid);
	}

	private void allowingCartOrderBillingAddressToBe(final String existingAddressGuid) {
		when(mockCartOrder.getBillingAddressGuid()).thenReturn(existingAddressGuid);
	}

	private void allowingCartOrderForGuid(final ExecutionResult<CartOrder> result) {
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(result);
	}

	private void allowingCartOrderGuidsByCustomer(final ExecutionResult<Collection<String>> result) {
		when(cartOrderRepository.findCartOrderGuidsByCustomer(STORE_CODE, CUSTOMER_GUID)).thenReturn(result);
	}


}
