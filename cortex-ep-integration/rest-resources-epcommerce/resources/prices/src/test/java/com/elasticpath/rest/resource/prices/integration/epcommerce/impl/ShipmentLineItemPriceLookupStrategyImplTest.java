/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Unit test.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemPriceLookupStrategyImplTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private MoneyTransformer moneyTransformer;
	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private ShipmentRepository shipmentRepository;
	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;
	@InjectMocks
	private ShipmentLineItemPriceLookupStrategyImpl shipmentLineItemPriceLookupStrategy;

	@Test
	public void testGetPrice() throws Exception {
		String scope = "scope";
		String purchaseGuid = "purchaseGuid";
		String shipmentGuid = "shipmentGuid";
		String lineitemGuid = "lineitemGuid";
		OrderSku orderSku = mock(OrderSku.class);
		PriceCalculator priceCalculator = mock(PriceCalculator.class);
		Money expectedMoney = Money.valueOf(BigDecimal.ONE, Currency.getInstance("CAD"));
		when(priceCalculator.forUnitPrice()).thenReturn(priceCalculator);
		when(priceCalculator.getMoney()).thenReturn(expectedMoney);
		when(priceCalculator.withCartDiscounts()).thenReturn(priceCalculator);
		ShoppingItemPricingSnapshot pricingSnapshot = mock(ShoppingItemPricingSnapshot.class);
		when(pricingSnapshotRepository.getPricingSnapshotForOrderSku(orderSku)).thenReturn(ExecutionResultFactory.createReadOK(pricingSnapshot));
		when(pricingSnapshot.getPriceCalc()).thenReturn(priceCalculator);
		when(shipmentRepository.getOrderSku(scope, purchaseGuid, shipmentGuid, lineitemGuid, null))
				.thenReturn(ExecutionResultFactory.createReadOK(orderSku));
		when(resourceOperationContext.getSubject())
				.thenReturn(TestSubjectFactory.createWithScopeAndUserIdAndLocale("scope", "fred", Locale.ENGLISH));
		CostEntity costEntity = CostEntity.builder()
				.withAmount(expectedMoney.getAmount())
				.withCurrency(expectedMoney.getCurrency().getCurrencyCode())
				.withDisplay("$1.00")
				.build();
		when(moneyTransformer.transformToEntity(expectedMoney, Locale.ENGLISH))
				.thenReturn(costEntity);

		ExecutionResult<ShipmentLineItemPriceEntity> priceResult
				= shipmentLineItemPriceLookupStrategy.getPrice(scope, purchaseGuid, shipmentGuid, lineitemGuid);
		assertTrue(priceResult.isSuccessful());
	}

	@Test
	public void testGetPriceOrderSkuNotFound() throws Exception {
		String scope = "scope";
		String purchaseGuid = "purchaseGuid";
		String shipmentGuid = "shipmentGuid";
		String lineitemGuid = "lineitemGuid";
		OrderSku orderSku = mock(OrderSku.class);
		PriceCalculator priceCalculator = mock(PriceCalculator.class);
		Money expectedMoney = Money.valueOf(BigDecimal.ONE, Currency.getInstance("CAD"));
		when(priceCalculator.forUnitPrice()).thenReturn(priceCalculator);
		when(priceCalculator.getMoney()).thenReturn(expectedMoney);
		when(priceCalculator.withCartDiscounts()).thenReturn(priceCalculator);
		ShoppingItemPricingSnapshot pricingSnapshot = mock(ShoppingItemPricingSnapshot.class);
		when(pricingSnapshotRepository.getPricingSnapshotForOrderSku(orderSku)).thenReturn(ExecutionResultFactory.createReadOK(pricingSnapshot));
		when(pricingSnapshot.getPriceCalc()).thenReturn(priceCalculator);
		when(shipmentRepository.getOrderSku(scope, purchaseGuid, shipmentGuid, lineitemGuid, null))
				.thenReturn(ExecutionResultFactory.<OrderSku>createNotFound("test not found"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLineItemPriceLookupStrategy.getPrice(scope, purchaseGuid, shipmentGuid, lineitemGuid);
	}
}
