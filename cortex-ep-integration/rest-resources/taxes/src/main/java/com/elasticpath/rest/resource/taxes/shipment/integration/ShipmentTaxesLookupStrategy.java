/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.taxes.TaxesEntity;

/**
 * Strategy to look up taxes.
 */
public interface ShipmentTaxesLookupStrategy {

	/**
	 * Gets the taxes for a shipment with the given identifying parameters.
	 * 
	 * @param purchaseId the purchase ID
	 * @param shipmentId the shipment ID
	 * @return the result of the taxes lookup
	 */
	ExecutionResult<TaxesEntity> getTaxes(String purchaseId, String shipmentId);

}
