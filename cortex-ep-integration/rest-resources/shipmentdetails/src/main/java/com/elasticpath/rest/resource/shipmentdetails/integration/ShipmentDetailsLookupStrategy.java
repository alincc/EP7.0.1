/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.shipmentdetails.integration.dto.ShipmentDetailsDto;

/**
 * The ShipmentDetails Lookup Strategy.
 */
public interface ShipmentDetailsLookupStrategy {

	/**
	 * Gets the shipment id for the given order and delivery ids.
	 *
	 * @param decodedOrderId the decoded order id
	 * @param decodedDeliveryId the decoded delivery id
	 * @return the shipment details id for order and delivery
	 */
	ExecutionResult<String> getShipmentDetailsIdForOrderAndDelivery(String decodedOrderId, String decodedDeliveryId);

	/**
	 * Gets the shipment IDs for scope and user id.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the shipment details IDs for scope and user id
	 */
	ExecutionResult<Collection<String>> getShipmentDetailsIds(String scope, String userId);

	/**
	 * Find by shipment details id.
	 *
	 * @param scope the scope
	 * @param decodedShipmentDetailId the decoded shipment detail id
	 * @return the execution result
	 */
	ExecutionResult<ShipmentDetailsDto> getShipmentDetail(String scope, String decodedShipmentDetailId);

	/**
	 * Gets the shipment details ids for an order.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order id
	 * @return all shipment details ids for an order
	 */
	ExecutionResult<Collection<String>> getShipmentDetailsIdsForOrder(String scope, String decodedOrderId);
}
