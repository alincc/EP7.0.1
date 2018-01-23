/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipping;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Repository for accessing {@link ShippingServiceLevel}s directly, as is necessary for a shipment.<br/>
 * This differs from the {@link ShippingServiceLevelRepository}, which contains logic that is specific
 * to finding {@link ShippingServiceLevel}s that are valid for a given cart.
 */
public interface ShipmentShippingServiceLevelRepository {

	/**
	 * Find a {@link ShippingServiceLevel} by its GUID.
	 * 
	 * @param shippingServiceLevelGuid the GUID
	 * @return result of {@link ShippingServiceLevel} lookup
	 */
	ExecutionResult<ShippingServiceLevel> findByGuid(String shippingServiceLevelGuid);

}
