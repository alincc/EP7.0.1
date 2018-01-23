/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Writer for destination info.
 */
public interface DestinationInfoWriter {

	/**
	 * Update shipping address for shipment.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @param address the address representation
	 * @return the execution result
	 */
	ExecutionResult<Void> updateShippingAddressForShipment(String scope, String shipmentDetailsId, ResourceState<AddressEntity> address);
}
