/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.epcommerce.transform.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.epcommerce.transform.ShipmentLineItemTaxesEntityTransformer;

/**
 * Creates {@link TaxesEntity} for a single shipment line item.
 */
@Singleton
@Named("shipmentLineItemTaxesEntityTransformer")
public class ShipmentLineItemTaxesEntityTransformerImpl implements ShipmentLineItemTaxesEntityTransformer {

	private static final BigDecimal DEFAULT_TAX = BigDecimal.ZERO.setScale(2);

	private final MoneyTransformer moneyTransformer;

	private final MoneyFormatter moneyFormatter;

	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Constructor.
	 *
	 * @param moneyTransformer the {@link MoneyTransformer}
	 * @param moneyFormatter the {@link MoneyFormatter}
	 * @param pricingSnapshotRepository the {@link PricingSnapshotRepository}
	 */
	@Inject
	ShipmentLineItemTaxesEntityTransformerImpl(
			@Named("moneyTransformer")
			final MoneyTransformer moneyTransformer,
			@Named("moneyFormatter")
			final MoneyFormatter moneyFormatter,
			@Named("pricingSnapshotRepository")
			final PricingSnapshotRepository pricingSnapshotRepository) {
		this.moneyTransformer = moneyTransformer;
		this.moneyFormatter = moneyFormatter;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	/**
	 * Returns a new {@link TaxesEntity} based on the given {@link TaxJournalRecord}s.
	 *
	 * @param orderSku the line item's {@link OrderSku}
	 * @param taxJournalRecords a collection of {@link TaxJournalRecord}s detailing the per tax amount breakdown
	 * @param locale the {@link Locale}
	 * @return the {@link TaxesEntity}
	 */
	@Override
	public TaxesEntity transform(final OrderSku orderSku, final Collection<TaxJournalRecord> taxJournalRecords, final Locale locale) {

		List<NamedCostEntity> taxes = buildNamedCostEntityCollection(taxJournalRecords, locale);

		final ShoppingItemTaxSnapshot taxSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getTaxSnapshotForOrderSku(orderSku));

		BigDecimal taxAmount = taxSnapshot.getTaxAmount();
		if (taxAmount == null) {
			taxAmount = DEFAULT_TAX;
		}
		Money taxMoney = Money.valueOf(taxAmount, orderSku.getCurrency());
		CostEntity total = moneyTransformer.transformToEntity(taxMoney, locale);

		TaxesEntity taxesEntity = TaxesEntity.builder()
				.withTotal(total)
				.withCost(taxes)
				.build();
		return taxesEntity;
	}

	private List<NamedCostEntity> buildNamedCostEntityCollection(final Collection<TaxJournalRecord> taxRecords, final Locale locale) {

		List<NamedCostEntity> cost = new ArrayList<NamedCostEntity>();

		for (TaxJournalRecord record : taxRecords) {

			BigDecimal amount = record.getTaxAmount();
			String currencyCode = record.getCurrency();
			NamedCostEntity taxEntity = NamedCostEntity.builder()
					.withAmount(amount)
					.withCurrency(currencyCode)
					.withDisplay(moneyFormatter.formatCurrency(Currency.getInstance(currencyCode), amount, locale))
					.withTitle(record.getTaxName())
					.build();
			cost.add(taxEntity);
		}

		return cost;
	}
}
