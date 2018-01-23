/**
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.totals.TotalEntity;

/**
 * Lookup strategy for Shipment totals.
 */
public interface ShipmentTotalLookupStrategy {

	/**
	 * Get the shipment total.
	 *
	 * @param decodedPurchaseId the decoded purchase id
	 * @param decodedShipmentId the decoded shipment id
	 * @return the shipment total
	 */
	ExecutionResult<TotalEntity> getTotal(String decodedPurchaseId, String decodedShipmentId);

}
