/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.integration;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Writer strategy for destination info.
 */
public interface DestinationInfoWriterStrategy {

	/**
	 * Update shipping address for shipment.
	 *
	 * @param scope the scope
	 * @param decodedShipmentDetailsId the decoded shipment details id
	 * @param decodedAddressId the decoded address id
	 * @return the execution result
	 */
	ExecutionResult<Void> updateShippingAddressForShipment(String scope, String decodedShipmentDetailsId, String decodedAddressId);
}
