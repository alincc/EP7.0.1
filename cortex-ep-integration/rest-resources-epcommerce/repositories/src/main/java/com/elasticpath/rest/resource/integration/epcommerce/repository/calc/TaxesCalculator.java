/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * Performs tax calculations on the cart order.
 */
public interface TaxesCalculator {

	/**
	 * Calculates the tax for the given cart order.
	 * @param storeCode The store code.
	 * @param cartOrderGuid the cart order to tax
	 * @return the execution result containing the tax or an error
	 */
	ExecutionResult<TaxCalculationResult> calculateTax(String storeCode, String cartOrderGuid);
}
