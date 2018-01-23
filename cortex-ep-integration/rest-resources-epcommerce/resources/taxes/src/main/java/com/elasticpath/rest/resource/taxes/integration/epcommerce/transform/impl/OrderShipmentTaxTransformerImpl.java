/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.integration.epcommerce.transform.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.taxes.integration.epcommerce.transform.OrderShipmentTaxTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms an {@link OrderShipment} into a {@link TaxesEntity}.
 */
@Singleton
@Named("orderShipmentTaxTransformer")
public class OrderShipmentTaxTransformerImpl extends AbstractDomainTransformer<OrderShipment, TaxesEntity> implements OrderShipmentTaxTransformer {

	private static final BigDecimal DEFAULT_TAX = BigDecimal.ZERO.setScale(2);

	private final MoneyTransformer moneyTransformer;
	private final MoneyFormatter moneyFormatter;

	/**
	 * Constructor.
	 *
	 * @param moneyTransformer the money transformer
	 * @param moneyFormatter the money formatter
	 */
	@Inject
	OrderShipmentTaxTransformerImpl(
			@Named("moneyTransformer")
			final MoneyTransformer moneyTransformer,
			@Named("moneyFormatter")
			final MoneyFormatter moneyFormatter) {
		this.moneyTransformer = moneyTransformer;
		this.moneyFormatter = moneyFormatter;
	}

	@Override
	public OrderShipment transformToDomain(final TaxesEntity taxesEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public TaxesEntity transformToEntity(final OrderShipment orderShipment, final Locale locale) {

		Money taxMoney = orderShipment.getTotalTaxMoney();

		if (taxMoney == null) {
			taxMoney = Money.valueOf(DEFAULT_TAX, orderShipment.getOrder().getCurrency());
		}

		CostEntity total = moneyTransformer.transformToEntity(taxMoney, locale);
		Collection<NamedCostEntity> taxes = buildCost(orderShipment, locale);

		return TaxesEntity.builder()
				.withTotal(total)
				.withCost(taxes)
				.build();
	}

	private Collection<NamedCostEntity> buildCost(final OrderShipment orderShipment, final Locale locale) {
		Set<OrderTaxValue> shipmentTaxes = orderShipment.getShipmentTaxes();
		Collection<NamedCostEntity> taxEntities = new ArrayList<>(shipmentTaxes.size());
		for (OrderTaxValue taxValue : shipmentTaxes) {
			Currency currency = orderShipment.getOrder().getCurrency();
			BigDecimal amount = taxValue.getTaxValue();
			NamedCostEntity taxEntity = NamedCostEntity.builder()
					.withAmount(amount)
					.withCurrency(currency.getCurrencyCode())
					.withDisplay(moneyFormatter.formatCurrency(currency, amount, locale))
					.withTitle(taxValue.getTaxCategoryDisplayName())
					.build();
			taxEntities.add(taxEntity);
		}
		return taxEntities;
	}

}
