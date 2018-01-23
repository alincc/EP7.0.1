/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Shipment line item lookup.
 */
public interface ShipmentLineItemsLookup {

	/**
	 * Find all shipment line item links for given request.
	 *
	 * @param shipmentRepresentation the shipment representation
	 * @return the link representation
	 */
	ExecutionResult<ResourceState<LinksEntity>> findAll(ResourceState<ShipmentEntity> shipmentRepresentation);

	/**
	 * Find the shipment line item representation for given request.
	 *
	 * @param shipmentRepresentation the shipment representation
	 * @param lineItemId the line item ID
	 * @return the shipment line item representation
	 */
	ExecutionResult<ResourceState<ShipmentLineItemEntity>> find(ResourceState<ShipmentEntity> shipmentRepresentation, String lineItemId);

}
