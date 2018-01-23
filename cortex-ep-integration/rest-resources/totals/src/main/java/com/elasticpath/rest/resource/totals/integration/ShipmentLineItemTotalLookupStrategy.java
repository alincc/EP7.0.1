/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.totals.TotalEntity;

/**
 * Lookup strategy for Shipment Line Item totals.
 */
public interface ShipmentLineItemTotalLookupStrategy {

	/**
	 * Get the shipment line item total.
	 *
	 * @param storeCode the store code
	 * @param purchaseId the purchase id
	 * @param shipmentId the shipment id
	 * @param lineItemId the line item id
	 * @return the shipment line item total
	 */
	ExecutionResult<TotalEntity> getTotal(String storeCode, String purchaseId, String shipmentId, String lineItemId);

}