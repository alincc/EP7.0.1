/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.order.integration.epcommerce.impl;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TaxesCalculator;
import com.elasticpath.rest.resource.taxes.integration.epcommerce.transform.TaxCalculationResultTransformer;
import com.elasticpath.rest.resource.taxes.order.integration.OrderTaxesLookupStrategy;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * Implementation of {@link OrderTaxesLookupStrategy} for order taxes.
 */
@Singleton
@Named("orderTaxesLookupStrategy")
public class OrderTaxesLookupStrategyImpl implements OrderTaxesLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final TaxCalculationResultTransformer taxCalculationResultTransformer;
	private final TaxesCalculator taxesCalculator;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param taxCalculationResultTransformer the tax calculation result transformer
	 * @param taxesCalculator the taxesCalculator
	 */
	@Inject
	public OrderTaxesLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("taxCalculationResultTransformer")
			final TaxCalculationResultTransformer taxCalculationResultTransformer,
			@Named("taxesCalculator")
			final TaxesCalculator taxesCalculator) {

		this.resourceOperationContext = resourceOperationContext;
		this.taxCalculationResultTransformer = taxCalculationResultTransformer;
		this.taxesCalculator = taxesCalculator;
	}

	@Override
	public ExecutionResult<TaxesEntity> getTaxes(final String scope, final String orderId) {

		TaxCalculationResult taxes = Assign.ifSuccessful(taxesCalculator.calculateTax(scope, orderId));
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		TaxesEntity taxesEntity = taxCalculationResultTransformer.transformToEntity(taxes, locale);

		return ExecutionResultFactory.createReadOK(taxesEntity);
	}
	
}
