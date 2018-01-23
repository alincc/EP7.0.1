/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Locale;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableSet;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.domain.shoppingcart.TaxPriceCalculator;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.totals.integration.epcommerce.transform.TotalMoneyTransformer;

/**
 * Tests {@link ShipmentLineItemTotalLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemTotalLookupStrategyImplTest {

	private static final String STORE_CODE = "STORE_CODE";
	private static final String PURCHASE_GUID = "test-order";
	private static final String SHIPMENT_GUID = "test-shipment";
	private static final String RESULT_SUCCESS_ASSERT_MESSAGE = "The operation should have completed successfully";
	private static final String RESULT_STATUS_OK_MESSAGE = "Result status should be OK";
	private static final String SKU1_GUID = "sku1_guid";
	private static final String SKU2_GUID = "sku2_guid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShipmentRepository shipmentRepository;

	@Mock
	@SuppressWarnings("PMD.UnusedPrivateField")
	private ResourceOperationContext resourceOperationContext;

	@Mock
	private PhysicalOrderShipment orderShipment;

	@Mock
	private TotalMoneyTransformer totalMoneyTransformer;

	@Mock
	private TaxPriceCalculator mockTaxCalculator;

	@Mock
	private OrderSku orderSku1;

	@Mock
	private OrderSku orderSku2;

	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@Mock
	private ShoppingItemTaxSnapshot taxSnapshot;

	@InjectMocks
	private ShipmentLineItemTotalLookupStrategyImpl shipmentLineItemTotalLookupStrategy;

	@Before
	public void setUp() {
		when(orderSku1.getGuid()).thenReturn(SKU1_GUID);
		when(orderSku2.getGuid()).thenReturn(SKU2_GUID);
		ImmutableSet<OrderSku> shipmentSkus = ImmutableSet.of(orderSku1, orderSku2);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(shipmentSkus);
		when(pricingSnapshotRepository.getTaxSnapshotForOrderSku(orderSku1)).thenReturn(ExecutionResultFactory.createReadOK(taxSnapshot));
		when(taxSnapshot.getTaxPriceCalculator()).thenReturn(mockTaxCalculator);
		when(mockTaxCalculator.withCartDiscounts()).thenReturn(mockTaxCalculator);
	}

	@Test
	public void testGetTotalWhenShipmentNotFound() {
		when(shipmentRepository.find(PURCHASE_GUID, SHIPMENT_GUID)).thenReturn(ExecutionResultFactory.<PhysicalOrderShipment>
				createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		callGetTotal(STORE_CODE, PURCHASE_GUID, SHIPMENT_GUID, SKU1_GUID);
	}

	@Test
	public void testGetTotalWhenSkuNotFound() {
		when(shipmentRepository.find(PURCHASE_GUID, SHIPMENT_GUID)).thenReturn(
				ExecutionResultFactory.createReadOK(orderShipment));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		callGetTotal(STORE_CODE, PURCHASE_GUID, SHIPMENT_GUID, "wrong" + SKU1_GUID);
	}

	@Test
	public void testGetTotalForSuccess() {
		when(shipmentRepository.find(PURCHASE_GUID, SHIPMENT_GUID)).thenReturn(
				ExecutionResultFactory.createReadOK(orderShipment));
		TotalEntity expectedTotal = ResourceTypeFactory.createResourceEntity(TotalEntity.class);
		when(totalMoneyTransformer.transformToEntity(any(Money.class), any(Locale.class))).thenReturn(expectedTotal);

		ExecutionResult<TotalEntity> executionResult = callGetTotal(STORE_CODE, PURCHASE_GUID, SHIPMENT_GUID, SKU1_GUID);

		assertReadOk(executionResult);
		assertEquals(expectedTotal, executionResult.getData());

	}

	private ExecutionResult<TotalEntity> callGetTotal(final String scope, final String purchaseGuid, final String shipmentGuid,
			final String lineItemGuid) {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale("scope", "user", Locale.ENGLISH);
		when(resourceOperationContext.getSubject()).thenReturn(subject);

		return shipmentLineItemTotalLookupStrategy.getTotal(scope, purchaseGuid, shipmentGuid, lineItemGuid);
	}

	private void assertReadOk(final ExecutionResult<TotalEntity> executionResult) {
		assertEquals(RESULT_STATUS_OK_MESSAGE, ResourceStatus.READ_OK, executionResult.getResourceStatus());
		assertTrue(RESULT_SUCCESS_ASSERT_MESSAGE, executionResult.isSuccessful());
	}
}
