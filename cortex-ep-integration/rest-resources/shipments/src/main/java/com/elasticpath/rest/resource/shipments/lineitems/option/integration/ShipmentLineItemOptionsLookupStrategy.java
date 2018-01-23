/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;

/**
 * The Shipment line item options lookup strategy.
 */
public interface ShipmentLineItemOptionsLookupStrategy {

	/**
	 * Finds Shipment line item option IDs for given request.
	 *
	 * @param entity the ShipmentLineItemEntity
	 * @return the list of Shipment line item option IDs
	 */
	ExecutionResult<Collection<String>> findLineItemOptionIds(ShipmentLineItemEntity entity);

	/**
	 * Finds the Shipment line item option.
	 *
	 * @param entity the ShipmentLineItemEntity
	 * @param optionId the line Item Option Id
	 * @return the ShipmentLineItemOptionEntity
	 */
	ExecutionResult<ShipmentLineItemOptionEntity> findLineItemOption(ShipmentLineItemEntity entity, String optionId);

	/**
	 * Finds the Shipment line item option values.
	 *
	 * @param entity the ShipmentLineItemOptionEntity
	 * @param valueId the line Item Option Value Id
	 * @return the ShipmentLineItemOptionValueEntity
	 */
	ExecutionResult<ShipmentLineItemOptionValueEntity> findLineItemOptionValue(ShipmentLineItemOptionEntity entity, String valueId);

}
