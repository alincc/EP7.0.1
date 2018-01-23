/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.dto.ShippingOptionDto;

/**
 * The The ShippingOption Lookup Strategy.
 */
public interface ShippingOptionLookupStrategy {

	/**
	 * Gets the shipping option for shipment details.
	 *
	 * @param scope the scope
	 * @param decodedShipmentDetailsId the decoded shipment details id
	 * @param decodedShippingOptionId the decoded shipping option id
	 * @return the shipping option for shipment
	 */
	ExecutionResult<ShippingOptionDto> getShippingOptionForShipmentDetails(String scope, String decodedShipmentDetailsId,
			String decodedShippingOptionId);

	/**
	 * Gets the shipping option ids for shipment details.
	 *
	 * @param scope the scope
	 * @param decodedShipmentDetailsId the decoded shipment details id
	 * @return the shipping option ids for shipment
	 */
	ExecutionResult<Collection<String>> getShippingOptionIdsForShipmentDetails(String scope, String decodedShipmentDetailsId);

	/**
	 * Gets selected shipment option GUID for given shipment details id.
	 *
	 * @param storeCode the store code
	 * @param shipmentDetailsId the shipment details id
	 * @return the execution result with selected shipment service level GUID
	 */
	ExecutionResult<String> getSelectedShipmentOptionIdForShipmentDetails(String storeCode, String shipmentDetailsId);
	/**
	 * Checks if is shipping option selected for shipment details.
	 *
	 * @param scope the scope
	 * @param decodedShipmentDetailsId the decoded shipment details id
	 * @return the execution result
	 */
	ExecutionResult<Boolean> isShippingDestinationSelectedForShipmentDetails(String scope, String decodedShipmentDetailsId);

	/**
	 * Checks if is supported delivery type.
	 *
	 * @param deliveryType the delivery type
	 * @return the execution result
	 */
	ExecutionResult<Boolean> isSupportedShippingOptionType(String deliveryType);
}
