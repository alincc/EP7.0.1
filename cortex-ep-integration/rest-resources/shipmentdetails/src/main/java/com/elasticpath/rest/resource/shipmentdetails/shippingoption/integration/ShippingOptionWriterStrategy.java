/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * The Shipping Option Writer Strategy.
 */
public interface ShippingOptionWriterStrategy {

	/**
	 * Select shipping option for shipment.
	 *
	 * @param scope the scope
	 * @param decodedShipmentDetailsId the decoded shipment details id
	 * @param decodedShippingOptionId the decoded shipping option id
	 * @return the execution result with a boolean if a selection had been made previously
	 */
	ExecutionResult<Boolean> selectShippingOptionForShipment(String scope, String decodedShipmentDetailsId, String decodedShippingOptionId);
}
