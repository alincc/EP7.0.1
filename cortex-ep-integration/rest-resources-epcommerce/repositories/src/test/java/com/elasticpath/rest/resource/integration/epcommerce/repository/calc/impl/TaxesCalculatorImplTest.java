/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.service.tax.TaxCalculationResult;

@RunWith(MockitoJUnitRunner.class)
public class TaxesCalculatorImplTest {

	private static final String STORE_CODE = "TEST_STORE";
	private static final String EXISTS_GUID = "exists guid";
	private static final String NOT_EXISTS_GUID = "not exists guid";

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@InjectMocks
	private TaxesCalculatorImpl calculator;

	@Test
	public void ensureTaxIsCalculatedBeforeTaxIsRead() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		stubGetShoppingCart(STORE_CODE, EXISTS_GUID, ExecutionResultFactory.createReadOK(shoppingCart));

		ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		when(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)).thenReturn(ExecutionResultFactory.createReadOK(taxSnapshot));

		TaxCalculationResult expectedTax = mock(TaxCalculationResult.class);
		when(taxSnapshot.getTaxCalculationResult()).thenReturn(expectedTax);

		ExecutionResult<TaxCalculationResult> result = calculator.calculateTax(STORE_CODE, EXISTS_GUID);

		assertEquals(result.getResourceStatus(), ResourceStatus.READ_OK);
		assertEquals(expectedTax, result.getData());
	}

	@Test
	public void ensureErrorPropagationOfFailedGetCartWhenCalculatingTax() {
		stubGetShoppingCart(STORE_CODE, NOT_EXISTS_GUID, ExecutionResultFactory.<ShoppingCart>createNotFound());
		ExecutionResult<TaxCalculationResult> result = calculator.calculateTax(STORE_CODE, NOT_EXISTS_GUID);
		assertTrue(result.getResourceStatus().isFailure());
	}

	private void stubGetShoppingCart(final String storeCode, final String guid, final ExecutionResult<ShoppingCart> result) {
		when(cartOrderRepository.getEnrichedShoppingCart(storeCode, guid, CartOrderRepository.FindCartOrder.BY_ORDER_GUID)).thenReturn(result);
	}
}

