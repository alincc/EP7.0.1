/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.epcommerce.transform.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Unit tests for {@link ShipmentLineItemTaxesEntityTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemTaxesEntityTransformerTest {

	private static final Locale LOCALE = Locale.CANADA;
	private static final Currency CURRENCY = Currency.getInstance(LOCALE);
	private static final BigDecimal TOTAL_AMOUNT = new BigDecimal("9.99");

	@Mock
	MoneyTransformer moneyTransformer;
	@Mock
	MoneyFormatter moneyFormatter;
	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;
	@InjectMocks
	private ShipmentLineItemTaxesEntityTransformerImpl taxTransformer;

	@Test
	public void testSuccessfulTransform() {

		TaxesEntity expectedTaxes = getExpectedTaxesEntity();
		OrderSku orderSku = createOrderSku();
		Collection<TaxJournalRecord> taxJournalRecords = Collections.emptyList();
		when(moneyTransformer.transformToEntity(any(Money.class), eq(LOCALE)))
			.thenReturn(expectedTaxes.getTotal());

		TaxesEntity taxesEntity = taxTransformer.transform(orderSku, taxJournalRecords, LOCALE);

		assertEquals(expectedTaxes, taxesEntity);
	}

	/**
	 * Explicitly verify that the total will be set to zero if the order shipment taxes are null.
	 */
	@Test
	public void testTotalValueSetToZeroWhenTaxAmountIsNull() {

		OrderSku orderSku = createOrderSku();
		ShoppingItemTaxSnapshot taxSnapshot = mock(ShoppingItemTaxSnapshot.class);

		Collection<TaxJournalRecord> taxJournalRecords = Collections.emptyList();
		when(pricingSnapshotRepository.getTaxSnapshotForOrderSku(orderSku)).thenReturn(ExecutionResultFactory.createReadOK(taxSnapshot));
		when(taxSnapshot.getTaxAmount()).thenReturn(null);
		CostEntity expectedTotal = CostEntity.builder().withAmount(new BigDecimal("0.00")).build();
		when(moneyTransformer.transformToEntity(any(Money.class), eq(LOCALE))).thenReturn(expectedTotal);

		TaxesEntity taxesEntity = taxTransformer.transform(orderSku, taxJournalRecords, LOCALE);

		assertEquals(expectedTotal, taxesEntity.getTotal());
	}

	@Test
	public void testTaxesCollectionPopulatedCorrectly() {

		List<NamedCostEntity> expected = createExpectedTaxEntityList();
		OrderSku orderSku = createOrderSku();
		Collection<TaxJournalRecord> taxJournalRecords = createTestInputTaxValues();
		for (NamedCostEntity namedCostEntity : expected) {
			when(moneyFormatter.formatCurrency(Currency.getInstance(namedCostEntity.getCurrency()),
					namedCostEntity.getAmount(), LOCALE))
				.thenReturn(namedCostEntity.getDisplay());
			when(moneyTransformer.transformToEntity(any(Money.class), eq(LOCALE)))
				.thenReturn(namedCostEntity);
		}

		TaxesEntity taxesEntity = taxTransformer.transform(orderSku, taxJournalRecords, LOCALE);

		assertEquals(expected, taxesEntity.getCost());
	}

	private List<NamedCostEntity> createExpectedTaxEntityList() {
		List<NamedCostEntity> expected = new ArrayList<>(2);
		expected.add(createExpectedNamedCostEntity(BigDecimal.TEN, "GST"));
		expected.add(createExpectedNamedCostEntity(BigDecimal.ONE, "PST"));
		return expected;
	}

	private OrderSku createOrderSku() {
		OrderSku orderSku = mock(OrderSku.class);
		ShoppingItemTaxSnapshot taxSnapshot = mock(ShoppingItemTaxSnapshot.class);

		when(pricingSnapshotRepository.getTaxSnapshotForOrderSku(orderSku)).thenReturn(ExecutionResultFactory.createReadOK(taxSnapshot));
		when(taxSnapshot.getTaxAmount()).thenReturn(TOTAL_AMOUNT);
		when(orderSku.getCurrency()).thenReturn(CURRENCY);
		return orderSku;
	}

	private TaxesEntity getExpectedTaxesEntity() {

		Collection<NamedCostEntity> taxes = Collections.emptyList();

		CostEntity total = CostEntity.builder()
				.withAmount(TOTAL_AMOUNT)
				.withCurrency(CURRENCY.getCurrencyCode())
				.withDisplay("$" + TOTAL_AMOUNT)
				.build();

		return TaxesEntity.builder()
				.withTotal(total)
				.withCost(taxes)
				.build();
	}

	private Collection<TaxJournalRecord> createTestInputTaxValues() {
		List<TaxJournalRecord> values = new ArrayList<>(2);
		values.add(createTaxJournalRecord(BigDecimal.TEN, "GST"));
		values.add(createTaxJournalRecord(BigDecimal.ONE, "PST"));

		return values;
	}

	private TaxJournalRecord createTaxJournalRecord(final BigDecimal amount, final String taxName) {
		TaxJournalRecord record = mock(TaxJournalRecord.class);
		when(record.getTaxAmount()).thenReturn(amount);
		when(record.getCurrency()).thenReturn(CURRENCY.getCurrencyCode());
		when(record.getTaxName()).thenReturn(taxName);
		return record;
	}

	private NamedCostEntity createExpectedNamedCostEntity(final BigDecimal amount, final String title) {
		return NamedCostEntity.builder()
				.withAmount(amount)
				.withCurrency(CURRENCY.getCurrencyCode())
				.withDisplay("$" + amount + ".00")
				.withTitle(title)
				.build();
	}
}
