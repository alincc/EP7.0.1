/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for shipment line item price.
 */
public interface ShipmentLineItemPriceLookup {

	/**
	 * Gets the price information for a given shipment line item.
	 *
	 * @param lineItem @return the price information for a shipment line item.
	 * @return {@link ShipmentLineItemPriceEntity} for a {@link ShipmentLineItemEntity}.
	 */
	ExecutionResult<ShipmentLineItemPriceEntity> getPrice(ResourceState<ShipmentLineItemEntity> lineItem);
}
