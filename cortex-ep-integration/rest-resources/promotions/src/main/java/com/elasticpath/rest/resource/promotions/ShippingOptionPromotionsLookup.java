/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Looks up applied promotions.
 */
public interface ShippingOptionPromotionsLookup {

	/**
	 * Gets the applied promotions.
	 *
	 *
	 * @param shippingOptionRepresentation the representation to read promotions for
	 * @return the links representation with promotions links.
	 */
	ExecutionResult<ResourceState<LinksEntity>>
	getAppliedPromotionsForShippingOption(ResourceState<ShippingOptionEntity> shippingOptionRepresentation);
}
