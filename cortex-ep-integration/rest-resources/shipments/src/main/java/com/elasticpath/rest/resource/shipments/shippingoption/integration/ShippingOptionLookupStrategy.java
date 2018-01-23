/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;

/**
 * Service that provides lookup of shipping option data from external systems.
 */
public interface ShippingOptionLookupStrategy {

	/**
	 * Finds shipment option using the shipment's identifying information.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the purchase ID
	 * @param decodedShipmentId the shipment ID
	 * @return result of the {@link ShippingOptionEntity} lookup
	 */
	ExecutionResult<ShippingOptionEntity> getShippingOption(String scope, String decodedPurchaseId, String decodedShipmentId);

}
