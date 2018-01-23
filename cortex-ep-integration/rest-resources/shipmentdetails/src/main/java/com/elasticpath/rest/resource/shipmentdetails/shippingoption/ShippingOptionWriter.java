/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Shipping Option Writer.
 */
public interface ShippingOptionWriter {

	/**
	 * Select shipping option for shipment.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @param shippingOptionId the shipping option id
	 * @return the execution result with a boolean if a selection had been made previously
	 */
	ExecutionResult<Boolean> selectShippingOptionForShipment(String scope, String shipmentDetailsId, String shippingOptionId);
}
