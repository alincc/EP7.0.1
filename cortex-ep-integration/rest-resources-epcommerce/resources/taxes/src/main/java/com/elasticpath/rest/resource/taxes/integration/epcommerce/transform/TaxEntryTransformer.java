/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.integration.epcommerce.transform;

import static com.elasticpath.rest.ResourceTypeFactory.adaptResourceEntity;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.taxes.integration.epcommerce.domain.wrapper.TaxEntryWrapper;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link TaxEntryWrapper} to a {@link NamedCostEntity} and vice versa.
 */
@Singleton
@Named("taxEntryTransformer")
public class TaxEntryTransformer extends AbstractDomainTransformer<TaxEntryWrapper, NamedCostEntity> {

	private final MoneyTransformer moneyTransformer;

	/**
	 * Default constructor.
	 *
	 * @param moneyTransformer the money transformer
	 */
	@Inject
	public TaxEntryTransformer(
			@Named("moneyTransformer")
			final MoneyTransformer moneyTransformer) {
		this.moneyTransformer = moneyTransformer;
	}

	@Override
	public TaxEntryWrapper transformToDomain(final NamedCostEntity taxEntity, final Locale locale) {
		throw new UnsupportedOperationException("This method is not implemented.");
	}

	@Override
	public NamedCostEntity transformToEntity(final TaxEntryWrapper taxEntryWrapper, final Locale locale) {
		CostEntity costEntity = moneyTransformer.transformToEntity(taxEntryWrapper.getTaxValue(), locale);
		return NamedCostEntity.builderFrom(adaptResourceEntity(costEntity, NamedCostEntity.class))
				.withTitle(taxEntryWrapper.getTaxCategory().getName())
				.build();
	}

}
