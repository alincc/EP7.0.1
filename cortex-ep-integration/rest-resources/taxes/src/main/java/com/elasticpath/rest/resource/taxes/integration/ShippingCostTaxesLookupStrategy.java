/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.taxes.TaxesEntity;

/**
 * Strategy for looking up taxes for shipping costs.
 */
public interface ShippingCostTaxesLookupStrategy {

	/**
	 * Get tax information for a shipment's shipping cost.
	 * 
	 * @param scope the scope
	 * @param purchaseId the purchase id
	 * @param shipmentId the shipment id
	 * @return the result of the tax information lookup
	 */
	ExecutionResult<TaxesEntity> getTaxes(String scope, String purchaseId, String shipmentId);

}
