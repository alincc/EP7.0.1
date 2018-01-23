/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;

/**
 * Service that provides lookup of shipment data from external systems.
 */
public interface ShipmentLookupStrategy {

	/**
	 * Finds shipment using details set in the shipment entity and the scope. Search criteria is currently set in the ShipmentEntity, which only
	 * supports finding by shipment ID.
	 * 
	 * @param shipmentEntity the shipment DTO.
	 * @return the shipment as a representation.
	 */
	ExecutionResult<ShipmentEntity> find(ShipmentEntity shipmentEntity);

	/**
	 * Finds shipment IDs by scope and purchase ID.
	 * 
	 * @param scope the scope.
	 * @param purchaseId the purchase ID
	 * @return the list of shipment IDs, empty if none found
	 */
	ExecutionResult<Collection<String>> findShipmentIds(String scope, String purchaseId);
}
