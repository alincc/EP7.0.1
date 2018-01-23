/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
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
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.ShipmentTotalsCalculator;
import com.elasticpath.rest.resource.totals.integration.ShipmentTotalLookupStrategy;
import com.elasticpath.rest.resource.totals.integration.epcommerce.transform.TotalMoneyTransformer;

/**
 * Lookup strategy for Shipment totals.
 */
@Singleton
@Named("shipmentTotalLookupStrategy")
public class ShipmentTotalLookupStrategyImpl implements ShipmentTotalLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;

	private final TotalMoneyTransformer totalMoneyTransformer;

	private final ShipmentTotalsCalculator totalsCalculator;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param totalMoneyTransformer    the total money transformer
	 * @param totalsCalculator         totalsCalculator
	 */
	@Inject
	public ShipmentTotalLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("totalMoneyTransformer")
			final TotalMoneyTransformer totalMoneyTransformer,
			@Named("shipmentTotalsCalculator")
			final ShipmentTotalsCalculator totalsCalculator) {

		this.resourceOperationContext = resourceOperationContext;
		this.totalMoneyTransformer = totalMoneyTransformer;
		this.totalsCalculator = totalsCalculator;
	}

	@Override
	public ExecutionResult<TotalEntity> getTotal(final String purchaseId, final String shipmentId) {

		final Money total = Assign.ifSuccessful(totalsCalculator.calculateTotal(purchaseId, shipmentId));

		return processTotal(total);
	}

	private ExecutionResult<TotalEntity> processTotal(final Money totalMoney) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		TotalEntity total = totalMoneyTransformer.transformToEntity(totalMoney, locale);
		return ExecutionResultFactory.createReadOK(total);
	}

}
