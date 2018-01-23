/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;

/**
 * Strategy to look up availabilities for items from external system.
 */
public interface AvailabilityLookupStrategy {

	/**
	 * Gets the availability for an item.
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return the {@link AvailabilityEntity} for the item.
	 */
	ExecutionResult<AvailabilityEntity> getAvailability(String scope, String itemId);

	/**
	 * Get unavailable line items.
	 *
	 * @param scope the scope
	 * @param cartGuid the cart GUID
	 * @return the collection of {@link com.elasticpath.rest.definition.carts.LineItemEntity}
	 */
	ExecutionResult<Collection<LineItemEntity>> getUnavailableLineItems(String scope, String cartGuid);
}
