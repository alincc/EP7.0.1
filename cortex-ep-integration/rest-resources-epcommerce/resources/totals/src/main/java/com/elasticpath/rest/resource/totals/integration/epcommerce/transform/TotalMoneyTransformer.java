/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration.epcommerce.transform;

import java.util.Collections;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.money.Money;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms between {@link Money} and {@link TotalEntity}, and vice versa.
 */
@Singleton
@Named("totalMoneyTransformer")
public class TotalMoneyTransformer extends AbstractDomainTransformer<Money, TotalEntity> {

	private final MoneyTransformer moneyTransformer;

	/**
	 * Default constructor.
	 *
	 * @param moneyTransformer the money transformer
	 */
	@Inject
	public TotalMoneyTransformer(
			@Named("moneyTransformer")
			final MoneyTransformer moneyTransformer) {

		this.moneyTransformer = moneyTransformer;
	}

	@Override
	public Money transformToDomain(final TotalEntity resourceEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public TotalEntity transformToEntity(final Money money, final Locale locale) {
		CostEntity cost = moneyTransformer.transformToEntity(money, locale);
		return TotalEntity.builder()
				.withCost(Collections.singleton(cost))
				.build();
	}

}
