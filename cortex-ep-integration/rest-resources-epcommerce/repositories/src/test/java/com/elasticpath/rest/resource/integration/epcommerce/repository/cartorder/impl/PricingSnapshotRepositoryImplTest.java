/**
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;

/**
 * Test for {@link PricingSnapshotRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PricingSnapshotRepositoryImplTest {

	private static final String SUCCESSFUL_OPERATION = "The operation should have been successful";

	@Mock
	private PricingSnapshotService pricingSnapshotService;

	@Mock
	private TaxSnapshotService taxSnapshotService;

	@Mock
	private ShoppingCart shoppingCart;

	@InjectMocks
	private PricingSnapshotRepositoryImpl repository;

	@Test
	public void testGetShoppingCartPricingSnapshot() throws Exception {
		final ShoppingCartPricingSnapshot pricingSnapshot = mock(ShoppingCartPricingSnapshot.class);
		when(pricingSnapshotService.getPricingSnapshotForCart(shoppingCart)).thenReturn(pricingSnapshot);

		final ExecutionResult<ShoppingCartPricingSnapshot> result = repository.getShoppingCartPricingSnapshot(shoppingCart);
		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals("The cart snapshot should match the one from the service", pricingSnapshot, result.getData());
	}

	@Test
	public void testGetPricingSnapshotForOrderSku() throws Exception {
		final OrderSku orderSku = mock(OrderSku.class);
		final ShoppingItemPricingSnapshot itemPricingSnapshot = mock(ShoppingItemPricingSnapshot.class);
		when(pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku)).thenReturn(itemPricingSnapshot);

		final ExecutionResult<ShoppingItemPricingSnapshot> result = repository.getPricingSnapshotForOrderSku(orderSku);
		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals("The item snapshot should match the one from the service", itemPricingSnapshot, result.getData());

	}

	@Test
	public void testGetShoppingCartTaxSnapshot() throws Exception {
		final ShoppingCartPricingSnapshot pricingSnapshot = mock(ShoppingCartPricingSnapshot.class);
		when(pricingSnapshotService.getPricingSnapshotForCart(shoppingCart)).thenReturn(pricingSnapshot);

		final ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		when(taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot)).thenReturn(taxSnapshot);

		final ExecutionResult<ShoppingCartTaxSnapshot> result = repository.getShoppingCartTaxSnapshot(shoppingCart);
		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals("The cart tax snapshot should match the one from the service", taxSnapshot, result.getData());

	}

	@Test
	public void testGetTaxSnapshotForOrderSku() throws Exception {
		final OrderSku orderSku = mock(OrderSku.class);
		final ShoppingItemPricingSnapshot itemPricingSnapshot = mock(ShoppingItemPricingSnapshot.class);
		when(pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku)).thenReturn(itemPricingSnapshot);

		final ShoppingItemTaxSnapshot itemTaxSnapshot = mock(ShoppingItemTaxSnapshot.class);
		when(taxSnapshotService.getTaxSnapshotForOrderSku(orderSku, itemPricingSnapshot)).thenReturn(itemTaxSnapshot);

		final ExecutionResult<ShoppingItemTaxSnapshot> result = repository.getTaxSnapshotForOrderSku(orderSku);
		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals("The item tax snapshot should match the one from the service", itemTaxSnapshot, result.getData());

	}
}