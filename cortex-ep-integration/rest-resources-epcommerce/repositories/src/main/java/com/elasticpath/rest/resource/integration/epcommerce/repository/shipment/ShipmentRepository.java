/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment;

import java.util.Collection;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * The facade for operations with shipments.
 */
public interface ShipmentRepository {

	/**
	 * Find by shipment GUID.
	 *
	 * @param orderGuid the order GUID
	 * @param shipmentGuid the shipment GUID
	 * @return ExecutionResult with the shipment
	 */
	ExecutionResult<PhysicalOrderShipment> find(String orderGuid, String shipmentGuid);

	/**
	 * Find all shipments by order GUID.
	 *
	 * @param storeCode the store code
	 * @param orderGuid the order GUID
	 * @return ExecutionResult with the list of shipments, returns blank list if none found
	 */
	ExecutionResult<Collection<PhysicalOrderShipment>> findAll(String storeCode, String orderGuid);

	/**
	 * Gets the order sku.
	 *
	 * @param scope The scope.
	 * @param purchaseId The purchase.
	 * @param shipmentId The shipment.
	 * @param lineItemId The line item.
	 * @param parentOrderSkuGuid The parent order sku.
	 * @return The order sku.
	 */
	ExecutionResult<OrderSku> getOrderSku(
			String scope, String purchaseId, String shipmentId, String lineItemId, String parentOrderSkuGuid);

	/**
	 * Gets the order skus for a shipment.
	 *
	 * @param scope The scope.
	 * @param purchaseId The purchase.
	 * @param shipmentId The shipment.
	 * @return The order skus.
	 */
	ExecutionResult<Collection<OrderSku>> getOrderSkusForShipment(String scope, String purchaseId, String shipmentId);
}
