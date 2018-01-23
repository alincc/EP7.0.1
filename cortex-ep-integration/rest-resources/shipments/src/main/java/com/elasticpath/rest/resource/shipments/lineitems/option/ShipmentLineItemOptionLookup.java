/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Shipment line item option lookup.
 */
public interface ShipmentLineItemOptionLookup {

	/**
	 * Find all shipment line item options links for given request.
	 *
	 * @param shipmentLineItem the shipment line item
	 * @return the link representation
	 */
	ExecutionResult<ResourceState<LinksEntity>> findAll(ResourceState<ShipmentLineItemEntity> shipmentLineItem);

	/**
	 * Find the shipment line item option representation for given request.
	 *
	 * @param shipmentLineItem the shipment line item
	 * @param lineItemOptionId the line item option id.
	 * @return the shipment line item option representation
	 */
	ExecutionResult<ResourceState<ShipmentLineItemOptionEntity>> find(
			ResourceState<ShipmentLineItemEntity> shipmentLineItem, String lineItemOptionId);

	/**
	 * Find the shipment line item option value representation for given request.
	 *
	 * @param shipmentLineOptionItem the shipment line item option
	 * @param lineItemOptionValueId the line item option value id.
	 * @return the shipment line item option value representation
	 */
	ExecutionResult<ResourceState<ShipmentLineItemOptionValueEntity>> findOptionValues(
			ResourceState<ShipmentLineItemOptionEntity> shipmentLineOptionItem, String lineItemOptionValueId);

}
