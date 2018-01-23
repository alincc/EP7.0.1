/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc;

import com.elasticpath.money.Money;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Calculates shipping cost totals.
 */
public interface ShippingCostTotalCalculator {

	/**
	 * Get the total shipping cost of a shipment.
	 * 
	 * @param orderGuid the order's GUID
	 * @param shipmentGuid the shipment's GUID
	 * @return result of total calculation
	 */
	ExecutionResult<Money> calculateTotal(String orderGuid, String shipmentGuid);
	
}
