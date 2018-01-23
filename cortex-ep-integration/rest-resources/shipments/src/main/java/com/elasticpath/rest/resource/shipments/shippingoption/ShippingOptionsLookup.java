/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Queries for shipping option information.
 */
public interface ShippingOptionsLookup {

	/**
	 * Get shipping option information for a given shipment.
	 * 
	 * @param shipment the shipment
	 * @return shipping option lookup result
	 */
	ExecutionResult<ResourceState<ShippingOptionEntity>> getShippingOption(ResourceState<ShipmentEntity> shipment);

}
