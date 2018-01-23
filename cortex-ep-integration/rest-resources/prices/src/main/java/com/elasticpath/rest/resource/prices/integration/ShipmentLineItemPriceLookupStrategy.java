/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;

/**
 * Queries for the Price information.
 */
public interface ShipmentLineItemPriceLookupStrategy {

	/**
	 * Gets the price of a shipment line item.
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @param decodedShipmentId the decoded purchase id
	 * @param decodedLineItemId the decoded line item id
	 * @return the result
	 */
	ExecutionResult<ShipmentLineItemPriceEntity> getPrice(
		String scope, String decodedPurchaseId, String decodedShipmentId, String decodedLineItemId);
}
