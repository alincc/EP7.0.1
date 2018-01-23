/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.integration.epcommerce.transform.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.domain.order.impl.OrderTaxValueImpl;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Unit tests for {@link OrderShipmentTaxTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderShipmentTaxTransformerImplTest {

	@Mock
	private MoneyTransformer moneyTransformer;
	@Mock
	private MoneyFormatter moneyFormatter;
	@InjectMocks
	private OrderShipmentTaxTransformerImpl taxTransformer;

	private static final String TOTAL_VALUE = "11.00";
	private static final Locale LOCALE = Locale.CANADA;
	private static final Currency CURRENCY = Currency.getInstance(LOCALE);


	@Test
	public void testGetTaxesForSuccess() {
		TaxesEntity expectedTaxes = getExpectedTaxesEntity();
		OrderShipment orderShipment = createOrderShipment();
		when(moneyTransformer.transformToEntity(any(Money.class), eq(LOCALE)))
			.thenReturn(expectedTaxes.getTotal());

		TaxesEntity taxesEntity = taxTransformer.transformToEntity(orderShipment, LOCALE);

		assertEquals(expectedTaxes, taxesEntity);
	}

	/**
	 * Explicitly verify that the total will be set to zero if the order shipment taxes are null.
	 */
	@Test
	public void testTotalValueSetToZeroWhenShipmentTaxesNotFound() {

		OrderShipment orderShipment = createOrderShipment();
		when(orderShipment.getTotalTaxMoney()).thenReturn(null);
		CostEntity expectedTotal = CostEntity.builder().withAmount(new BigDecimal("0.00")).build();
		when(moneyTransformer.transformToEntity(any(Money.class), eq(LOCALE))).thenReturn(expectedTotal);

		TaxesEntity taxesEntity = taxTransformer.transformToEntity(orderShipment, LOCALE);

		assertEquals(expectedTotal, taxesEntity.getTotal());
	}

	/**
	 * Explicitly verify that the total will be set to zero if the order shipment taxes are null.
	 */
	@Test
	public void testTaxesCollectionPopulatedCorrectly() {

		OrderShipment orderShipment = createOrderShipment();
		when(orderShipment.getShipmentTaxes()).thenReturn(createTestInputTaxValues());

		TaxesEntity taxesEntity = taxTransformer.transformToEntity(orderShipment, LOCALE);

		NamedCostEntity[] expected = { createNamedCostEntity(BigDecimal.ONE, "PST"), createNamedCostEntity(BigDecimal.TEN, "GST") };
		assertArrayEquals(expected, taxesEntity.getCost().toArray());
	}

	private OrderShipment createOrderShipment() {

		OrderShipment orderShipment = mock(OrderShipment.class);
		Order order = mock(Order.class);
		when(order.getCurrency()).thenReturn(CURRENCY);

		when(orderShipment.getOrder()).thenReturn(order);
		when(orderShipment.getTotalTaxMoney()).thenReturn(Money.valueOf(new BigDecimal(TOTAL_VALUE), CURRENCY));

		Set<OrderTaxValue> values = Collections.emptySet();
		when(orderShipment.getShipmentTaxes()).thenReturn(values);

		return orderShipment;
	}

	private Set<OrderTaxValue> createTestInputTaxValues() {
		Set<OrderTaxValue> values = new HashSet<OrderTaxValue>(2);
		values.add(createOrderTaxValue(BigDecimal.TEN, "GST"));
		values.add(createOrderTaxValue(BigDecimal.ONE, "PST"));

		return values;
	}

	private OrderTaxValue createOrderTaxValue(final BigDecimal taxValue, final String displayName) {
		OrderTaxValue value = new OrderTaxValueImpl();
		value.setTaxValue(taxValue);
		value.setTaxCategoryDisplayName(displayName);
		return value;
	}

	private TaxesEntity getExpectedTaxesEntity() {
		Collection<NamedCostEntity> taxes = Collections.emptyList();

		CostEntity total = CostEntity.builder()
				.withAmount(new BigDecimal(TOTAL_VALUE))
				.withCurrency("CAD")
				.withDisplay("$" + TOTAL_VALUE)
				.build();

		return TaxesEntity.builder()
				.withTotal(total)
				.withCost(taxes)
				.build();
	}

	private NamedCostEntity createNamedCostEntity(final BigDecimal amount, final String title) {
		return NamedCostEntity.builder()
				.withAmount(amount)
				.withCurrency(CURRENCY.getCurrencyCode())
				.withDisplay(moneyFormatter.formatCurrency(CURRENCY, amount, LOCALE))
				.withTitle(title)
				.build();
	}
}
