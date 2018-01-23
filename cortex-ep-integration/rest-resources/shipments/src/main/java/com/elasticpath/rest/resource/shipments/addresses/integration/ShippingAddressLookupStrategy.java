/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;

/**
 * Lookup strategy for a shipping address.
 */
public interface ShippingAddressLookupStrategy {

	/**
	 * Returns an {@link AddressEntity} that represents the given shipment's shipping address.
	 * 
	 * @param scope the scope in which the order exists
	 * @param purchaseId the ID of the purchase
	 * @param shipmentId the shipment ID
	 * @return the shipping address for the specified shipment
	 */
	ExecutionResult<AddressEntity> getShippingAddress(String scope, String purchaseId, String shipmentId);
}
