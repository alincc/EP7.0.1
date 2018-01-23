/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Lookup class for destination info.
 */
public interface DestinationInfoLookup {

	/**
	 * Find selected shipping address ID for the shipment.
	 *
	 * @param scope the scope
	 * @param shipmentId the shipment id
	 * @return the execution result
	 */
	ExecutionResult<String> findSelectedAddressIdForShipment(String scope, String shipmentId);
}
