/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Queries for shipment information.
 */
public interface ShipmentLookup {

	/**
	 * Gets shipment information for a given shipment ID.
	 * 
	 * @param purchase  purchase representation
	 * @param shipmentId the shipment ID
	 * @return the shipment
	 */
	ExecutionResult<ResourceState<ShipmentEntity>> getShipmentForPurchase(ResourceState<PurchaseEntity> purchase, String shipmentId);
	
	/**
	 * Gets all shipments for a given purchase ID.
	 * 
	 * @param purchase  purchase representation
	 * @return the shipment links
	 */
	ExecutionResult<ResourceState<LinksEntity>> getShipmentsForPurchase(ResourceState<PurchaseEntity> purchase);
	
}
