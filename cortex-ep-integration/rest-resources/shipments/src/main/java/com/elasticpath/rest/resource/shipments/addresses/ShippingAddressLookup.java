/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup for a shipping address.
 */
public interface ShippingAddressLookup {
	/**
	 * Returns an {@link com.elasticpath.rest.schema.ResourceState} that represents the given shipment's shipping address.
	 *
	 * @param shipmentRepresentation the shipmentRepresentation
	 * @return the shipping address for the specified shipment
	 */
	ExecutionResult<ResourceState<AddressEntity>> getShippingAddress(ResourceState<ShipmentEntity> shipmentRepresentation);
}
