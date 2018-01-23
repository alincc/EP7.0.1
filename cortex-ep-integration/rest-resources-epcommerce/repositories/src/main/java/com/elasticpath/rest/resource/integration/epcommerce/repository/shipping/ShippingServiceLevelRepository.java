/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipping;

import java.util.Collection;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * The facade for operations with shipping service levels.
 */
public interface ShippingServiceLevelRepository {

	/**
	 * Find a Shipping Service Level by guid.
	 * @param storeCode the store code
	 * @param shipmentDetailsId the shipment details id
	 * @param shippingServiceLevelGuid the shipping service level guid
	 *
	 * @return ExecutionResult with the shipping service level
	 */
	ExecutionResult<ShippingServiceLevel> findByGuid(String storeCode, String shipmentDetailsId, String shippingServiceLevelGuid);

	/**
	 * Gets a shipping service level from the list.
	 * @param shippingServiceLevels The shipping service levels.
	 * @param shippingServiceLevelGuid The shipping service level to find.
	 * @return The shipping service level.  May be null if not found.
	 */
	ExecutionResult<ShippingServiceLevel> getShippingServiceLevel(
			Collection<ShippingServiceLevel> shippingServiceLevels, String shippingServiceLevelGuid);

	/**
	 * Find available shipping service levels for the given shipment.
	 *
	 * @param storeCode the store code
	 * @param shipmentDetailsId the shipment details id
	 * @return ExecutionResult with the collection of valid shipping service levels
	 */
	ExecutionResult<Collection<String>> findShippingServiceLevelGuidsForShipment(String storeCode, String shipmentDetailsId);

	/**
	 * Gets the selected shipping service level GUID for the given shipment.
	 *
	 * @param storeCode the store code
	 * @param shipmentDetailsId the shipment details id
	 * @return ExecutionResult with the selected shipping service level GUID
	 */
	ExecutionResult<String> getSelectedShipmentOptionIdForShipmentDetails(String storeCode, String shipmentDetailsId);
}
