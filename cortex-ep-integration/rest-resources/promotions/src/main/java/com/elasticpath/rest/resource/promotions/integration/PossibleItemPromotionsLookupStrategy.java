/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Service that provides lookup of possible item promotions data from external systems.
 */
public interface PossibleItemPromotionsLookupStrategy {

	/**
	 * Gets the possible promotions for the given item.
	 *
	 * @param scope the scope.
	 * @param itemId the item ID.
	 * @return a collection of possible promotion IDs.
	 */
	ExecutionResult<Collection<String>> getPossiblePromotionsForItem(String scope, String itemId);


	/**
	 * Checks if the given item has possible promotions.
	 *
	 * @param scope the scope.
	 * @param itemId the item ID.
	 * @return true if item contains possible promotions.
	 */
	ExecutionResult<Boolean> itemHasPossiblePromotions(String scope, String itemId);

}
