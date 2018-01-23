/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Service that provides lookup of possible cart promotions data from external systems.
 */
public interface PossibleCartPromotionsLookupStrategy {


	/**
	 * Gets the possible promotions for the given cart.
	 *
	 * @param scope the scope.
	 * @param decodedCartId the decoded cart ID.
	 * @return a collection of possible promotion IDs.
	 */
	ExecutionResult<Collection<String>> getPossiblePromotionsForCart(String scope, String decodedCartId);


	/**
	 * Checks if the given cart has possible promotions.
	 *
	 * @param scope the scope.
	 * @param decodedCartId the decoded cart ID.
	 * @return true if cart contains possible promotions.
	 */
	ExecutionResult<Boolean> cartHasPossiblePromotions(String scope, String decodedCartId);
}
