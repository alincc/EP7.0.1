/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Service that provides lookup of applied item promotions data from external systems.
 */
public interface AppliedItemPromotionsLookupStrategy {

	/**
	 * Gets the applied promotions for the given item.
	 *
	 * @param scope the scope.
	 * @param itemId the item ID.
	 * @return a collection of applied promotion IDs.
	 */
	ExecutionResult<Collection<String>> getAppliedPromotionsForItem(String scope, String itemId);

}
