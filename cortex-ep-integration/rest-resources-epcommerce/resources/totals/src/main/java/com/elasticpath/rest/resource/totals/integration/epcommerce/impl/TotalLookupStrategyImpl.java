/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration.epcommerce.impl;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TotalsCalculator;
import com.elasticpath.rest.resource.totals.integration.TotalLookupStrategy;
import com.elasticpath.rest.resource.totals.integration.epcommerce.transform.TotalMoneyTransformer;

/**
 * Lookup strategy for totals.
 */
@Singleton
@Named("totalLookupStrategy")
public class TotalLookupStrategyImpl implements TotalLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final TotalMoneyTransformer totalMoneyTransformer;
	private final TotalsCalculator totalsCalculator;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param totalMoneyTransformer    the total money transformer
	 * @param totalsCalculator         totalsCalculator
	 */
	@Inject
	public TotalLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("totalMoneyTransformer")
			final TotalMoneyTransformer totalMoneyTransformer,
			@Named("totalsCalculator")
			final TotalsCalculator totalsCalculator) {

		this.resourceOperationContext = resourceOperationContext;
		this.totalMoneyTransformer = totalMoneyTransformer;
		this.totalsCalculator = totalsCalculator;
	}

	@Override
	public ExecutionResult<TotalEntity> getOrderTotal(final String storeCode, final String cartOrderGuid) {

		Money total = Assign.ifSuccessful(totalsCalculator.calculateTotalForCartOrder(storeCode, cartOrderGuid));

		return processTotal(total);
	}

	@Override
	public ExecutionResult<TotalEntity> getCartTotal(final String storeCode, final String cartGuid) {

		Money totalMoney = Assign.ifSuccessful(totalsCalculator.calculateTotalForShoppingCart(storeCode, cartGuid));

		return processTotal(totalMoney);
	}

	@Override
	public ExecutionResult<TotalEntity> getLineItemTotal(final String storeCode, final String cartGuid, final String cartItemGuid) {

		Money totalMoney = Assign.ifSuccessful(totalsCalculator.calculateTotalForLineItem(storeCode, cartGuid, cartItemGuid));

		return processTotal(totalMoney);
	}

	private ExecutionResult<TotalEntity> processTotal(final Money totalMoney) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		TotalEntity total = totalMoneyTransformer.transformToEntity(totalMoney, locale);

		return ExecutionResultFactory.createReadOK(total);
	}
}
