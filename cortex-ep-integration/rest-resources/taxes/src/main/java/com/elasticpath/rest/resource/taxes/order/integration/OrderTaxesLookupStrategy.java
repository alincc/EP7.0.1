/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.order.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.taxes.TaxesEntity;

/**
 * Strategy to look up taxes.
 */
public interface OrderTaxesLookupStrategy {

	/**
	 * Gets the tax for an order with the given identifying parameters.
	 *
	 * @param scope the scope
	 * @param orderId the order ID
	 * @return the result of the taxes lookup
	 */
	ExecutionResult<TaxesEntity> getTaxes(String scope, String orderId);

}
