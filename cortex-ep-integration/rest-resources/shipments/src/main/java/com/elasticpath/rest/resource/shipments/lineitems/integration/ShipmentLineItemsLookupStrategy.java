/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;

/**
 * The shipment line items lookup strategy.
 */
public interface ShipmentLineItemsLookupStrategy {

	/**
	 * Finds shipment line item IDs for given request.
	 *
	 * @param scope the scope
	 * @param shipmentLineItemEntity the shipment line item
	 * @return the list of shipment line item IDs
	 */
	ExecutionResult<Collection<String>> findLineItemIds(String scope, ShipmentLineItemEntity shipmentLineItemEntity);

	/**
	 * Finds the shipment line item.
	 *
	 * @param scope the scope
	 * @param shipmentLineItemEntity the shipment line item
	 * @return the shipment line item entity
	 */
	ExecutionResult<ShipmentLineItemEntity> find(String scope, ShipmentLineItemEntity shipmentLineItemEntity);

}
