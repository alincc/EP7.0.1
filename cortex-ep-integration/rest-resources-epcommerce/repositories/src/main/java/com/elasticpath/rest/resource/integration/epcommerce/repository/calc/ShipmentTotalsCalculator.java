/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc;

import com.elasticpath.money.Money;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Performs totals calculations for the Shipment.
 */
public interface ShipmentTotalsCalculator {

	/**
	 * Calculates the total for the given Shipment.
	 *
	 * @param orderGuid the orderGuid of the Order
	 * @param shipmentGuid the shipmentGuid of the Shipment to total
	 * @return the execution result containing the total or an error
	 */
	ExecutionResult<Money> calculateTotal(String orderGuid, String shipmentGuid);

}
