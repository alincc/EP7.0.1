/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.taxes.TaxesEntity;

/**
 * Strategy to look up shipment line item taxes.
 */
public interface ShipmentLineItemTaxesLookupStrategy {

	/**
	 * Gets the tax for request.
	 * 
	 * @param scope the scope
	 * @param purchaseId the purchase id
	 * @param shipmentId the shipment id
	 * @param lineItemId the line item id
	 * @return the tax for given request
	 */
	ExecutionResult<TaxesEntity> getTaxes(String scope, String purchaseId, String shipmentId, String lineItemId);
}
