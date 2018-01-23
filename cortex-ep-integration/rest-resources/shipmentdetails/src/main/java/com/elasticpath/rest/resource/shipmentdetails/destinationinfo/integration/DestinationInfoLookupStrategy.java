/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.integration;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Lookup strategy for destination info.
 */
public interface DestinationInfoLookupStrategy {

	/**
	 * Find selected address id for order.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order id
	 * @param decodedShipmentId the decoded shipment id
	 * @return the execution result
	 */
	ExecutionResult<String> findSelectedAddressIdForShipment(String scope, String decodedOrderId, String decodedShipmentId);
}
