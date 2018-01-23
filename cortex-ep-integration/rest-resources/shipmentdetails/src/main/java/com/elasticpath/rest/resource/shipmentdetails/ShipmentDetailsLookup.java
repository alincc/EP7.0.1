/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.schema.ResourceState;


/**
 * The shipment details lookup.
 */
public interface ShipmentDetailsLookup {

	/**
	 * Find shipment details IDs by user ID.
	 *
	 * @param scope the scope
	 * @param userId the user ID
	 * @return the execution result
	 */
	ExecutionResult<Collection<String>> findShipmentDetailsIds(String scope, String userId);

	/**
	 * Finds all shipment detail ids for order.
	 *
	 * @param scope the scope
	 * @param orderId the order id
	 * @return all shipment detail ids for order
	 */
	ExecutionResult<Collection<String>> findShipmentDetailsIdsForOrder(String scope, String orderId);

	/**
	 * Find by shipment details id.
	 *
	 * @param scope the scope
	 * @param shipmentDetailId the shipment detail id
	 * @return the execution result
	 */
	ExecutionResult<ShipmentDetail> getShipmentDetail(String scope, String shipmentDetailId);

	/**
	 * Find shipment details id for a delivery.
	 *
	 * @param delivery the delivery representation
	 * @return the execution result
	 */
	ExecutionResult<String> findShipmentDetailsIdForDelivery(ResourceState<DeliveryEntity> delivery);
}
