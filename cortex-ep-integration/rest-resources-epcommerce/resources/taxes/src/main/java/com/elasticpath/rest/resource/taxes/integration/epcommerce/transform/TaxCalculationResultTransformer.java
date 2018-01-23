/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.integration.epcommerce.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.taxes.integration.epcommerce.domain.wrapper.TaxEntryWrapper;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * Transforms {@link TaxCalculationResult} to {@link TaxesEntity}, and vice versa.
 */
@Singleton
@Named("taxCalculationResultTransformer")
public class TaxCalculationResultTransformer extends AbstractDomainTransformer<TaxCalculationResult, TaxesEntity> {

	private final MoneyTransformer moneyTransformer;
	private final TaxEntryTransformer taxEntryTransformer;

	/**
	 * Default constructor.
	 *
	 * @param moneyTransformer the money transformer
	 * @param taxEntryTransformer the tax transformer
	 */
	@Inject
	public TaxCalculationResultTransformer(
			@Named("moneyTransformer")
			final MoneyTransformer moneyTransformer,
			@Named("taxEntryTransformer")
			final TaxEntryTransformer taxEntryTransformer) {

		this.moneyTransformer = moneyTransformer;
		this.taxEntryTransformer = taxEntryTransformer;
	}

	@Override
	public TaxCalculationResult transformToDomain(final TaxesEntity taxesDto, final Locale locale) {
		throw new UnsupportedOperationException("This method is not implemented.");
	}

	@Override
	public TaxesEntity transformToEntity(final TaxCalculationResult taxCalculationResult, final Locale locale) {
		CostEntity totalTax = moneyTransformer.transformToEntity(taxCalculationResult.getTotalTaxes(), locale);

		Set<Entry<TaxCategory, Money>> domainTaxes = taxCalculationResult.getTaxMap().entrySet();
		Collection<NamedCostEntity> taxEntities = new ArrayList<>(domainTaxes.size());
		for (Entry<TaxCategory, Money> tax : domainTaxes) {
			TaxEntryWrapper taxEntryWrapper = createTaxEntryWrapper(tax);
			NamedCostEntity taxEntity = taxEntryTransformer.transformToEntity(taxEntryWrapper, locale);
			taxEntities.add(taxEntity);
		}

		return TaxesEntity.Builder
				.builder()
				.withTotal(totalTax)
				.withCost(taxEntities)
				.build();
	}

	private TaxEntryWrapper createTaxEntryWrapper(final Entry<TaxCategory, Money> tax) {
		TaxEntryWrapper taxEntryWrapper = ResourceTypeFactory.createResourceEntity(TaxEntryWrapper.class);
		taxEntryWrapper.setTaxCategory(tax.getKey())
				.setTaxValue(tax.getValue());
		return taxEntryWrapper;
	}

}
