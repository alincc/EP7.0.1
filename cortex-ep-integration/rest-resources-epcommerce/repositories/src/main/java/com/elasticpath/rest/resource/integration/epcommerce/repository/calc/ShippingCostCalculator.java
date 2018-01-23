/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc;

import com.elasticpath.money.Money;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Calculates shipping costs.
 */
public interface ShippingCostCalculator {

	/**
	 * Get the shipping cost of a shipment.<br/>
	 * In tax-inclusive zones, this will include tax. In tax-exclusive zones, this will not.
	 * 
	 * @param orderGuid the order's GUID
	 * @param shipmentGuid the shipment's GUID
	 * @return result of total calculation
	 */
	ExecutionResult<Money> calculate(String orderGuid, String shipmentGuid);

}
