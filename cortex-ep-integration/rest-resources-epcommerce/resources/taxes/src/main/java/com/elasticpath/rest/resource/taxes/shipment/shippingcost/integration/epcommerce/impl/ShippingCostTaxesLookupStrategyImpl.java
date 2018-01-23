/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.shippingcost.integration.epcommerce.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.TaxDocumentRepository;
import com.elasticpath.rest.resource.taxes.integration.ShippingCostTaxesLookupStrategy;

/**
 * Implementation of {@link ShippingCostTaxesLookupStrategy} for shipping cost taxes.
 */
@Named("shippingCostTaxesLookupStrategy")
public class ShippingCostTaxesLookupStrategyImpl implements ShippingCostTaxesLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final ShipmentRepository shipmentRepository;
	private final TaxDocumentRepository taxDocumentRepository;
	private final MoneyFormatter moneyFormatter;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext a {@link ResourceOperationContext}
	 * @param shipmentRepository       a {@link ShipmentRepository}
	 * @param taxDocumentRepository    a {@link TaxDocumentRepository}
	 * @param moneyFormatter           a {@link MoneyFormatter}
	 */
	@Inject
	public ShippingCostTaxesLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("shipmentRepository")
			final ShipmentRepository shipmentRepository,
			@Named("taxDocumentRepository")
			final TaxDocumentRepository taxDocumentRepository,
			@Named("moneyFormatter")
			final MoneyFormatter moneyFormatter) {
		this.resourceOperationContext = resourceOperationContext;
		this.taxDocumentRepository = taxDocumentRepository;
		this.moneyFormatter = moneyFormatter;
		this.shipmentRepository = shipmentRepository;
	}

	@Override
	public ExecutionResult<TaxesEntity> getTaxes(final String scope, final String purchaseId, final String shipmentId) {

		PhysicalOrderShipment shipment = Assign.ifSuccessful(shipmentRepository.find(purchaseId, shipmentId));

		return getTaxesForPhysicalShipment(shipment);
	}

	private ExecutionResult<TaxesEntity> getTaxesForPhysicalShipment(final PhysicalOrderShipment shipment) {

		Collection<NamedCostEntity> taxEntities = Assign.ifSuccessful(getTaxEntities(shipment));
		CostEntity total;
		if (taxEntities.isEmpty()) {
			total = getZeroTotal(shipment);
		} else {
			total = getTotal(taxEntities);
		}
		TaxesEntity taxes = TaxesEntity.builder()
				.withCost(taxEntities)
				.withTotal(total)
				.build();

		return ExecutionResultFactory.createReadOK(taxes);
	}

	private ExecutionResult<Collection<NamedCostEntity>> getTaxEntities(final PhysicalOrderShipment shipment) {

		ExecutionResult<Collection<TaxJournalRecord>> taxDocumentResult = taxDocumentRepository
				.getTaxDocument(shipment.getTaxDocumentId(), TaxCode.TAX_CODE_SHIPPING);
		Collection<TaxJournalRecord> taxRecords = Assign.ifSuccessful(taxDocumentResult);

		return ExecutionResultFactory.createReadOK(convertToTaxEntities(taxRecords));
	}

	private Collection<NamedCostEntity> convertToTaxEntities(final Collection<TaxJournalRecord> taxRecords) {
		Locale locale = getLocaleForSubject(resourceOperationContext.getSubject());
		Collection<NamedCostEntity> taxEntities = new ArrayList<>(taxRecords.size());
		for (TaxJournalRecord record : taxRecords) {
			taxEntities.add(convertToTaxEntity(record, locale));
		}
		return taxEntities;
	}

	/**
	 * Method to isolate dependency on {@link SubjectUtil#getLocale}.
	 *
	 * @param subject the subject
	 * @return a locale
	 */
	protected Locale getLocaleForSubject(final Subject subject) {
		return SubjectUtil.getLocale(subject);
	}

	private NamedCostEntity convertToTaxEntity(final TaxJournalRecord record, final Locale locale) {
		String currencyCode = record.getCurrency();
		BigDecimal taxAmount = record.getTaxAmount();

		return NamedCostEntity.builder()
				.withAmount(taxAmount)
				.withCurrency(currencyCode)
				.withDisplay(moneyFormatter.formatCurrency(Currency.getInstance(currencyCode), taxAmount, locale))
				.withTitle(record.getTaxName())
				.build();
	}

	private CostEntity getZeroTotal(final PhysicalOrderShipment shipment) {
		String currencyCode = shipment.getShippingTaxMoney().getCurrency().getCurrencyCode();
		Locale locale = getLocaleForSubject(resourceOperationContext.getSubject());
		String display = moneyFormatter.formatCurrency(Currency.getInstance(currencyCode), BigDecimal.ZERO, locale);

		return CostEntity.builder()
				.withAmount(BigDecimal.ZERO)
				.withCurrency(currencyCode)
				.withDisplay(display)
				.build();
	}

	private CostEntity getTotal(final Collection<NamedCostEntity> taxEntities) {
		String currencyCode = getSingleCurrencyCode(taxEntities);
		BigDecimal taxSum = getTaxSum(taxEntities);
		Locale locale = getLocaleForSubject(resourceOperationContext.getSubject());
		String display = moneyFormatter.formatCurrency(Currency.getInstance(currencyCode), taxSum, locale);

		return CostEntity.builder()
				.withAmount(taxSum)
				.withCurrency(currencyCode)
				.withDisplay(display)
				.build();
	}

	/**
	 * Confirm that each {@link NamedCostEntity} has the same currency code, and return it.
	 */
	private String getSingleCurrencyCode(final Collection<NamedCostEntity> taxEntities) {
		String currencyCode = null;
		for (NamedCostEntity taxEntity : taxEntities) {
			if (currencyCode == null) {
				currencyCode = taxEntity.getCurrency();
			} else if (!currencyCode.equals(taxEntity.getCurrency())) {
				throw new IllegalStateException("Cannot add together taxes of differing currencies.");
			}
		}
		return currencyCode;
	}

	private BigDecimal getTaxSum(final Collection<NamedCostEntity> taxEntities) {
		BigDecimal taxSum = BigDecimal.ZERO;
		for (NamedCostEntity taxEntity : taxEntities) {
			taxSum = taxSum.add(taxEntity.getAmount());
		}
		return taxSum;
	}

}
