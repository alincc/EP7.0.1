/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Service that provides lookup of applied cart promotions data from external systems.
 */
public interface AppliedCartPromotionsLookupStrategy {

	/**
	 * Gets the applied promotions for the given line item in the cart.
	 *
	 * @param scope the scope.
	 * @param decodedCartId the decoded cart ID.
	 * @param decodedLineItemId the decoded line item ID.
	 * @param quantity the item quantity.
	 * @return a collection of applied promotion IDs.
	 */
	ExecutionResult<Collection<String>> getAppliedPromotionsForItemInCart(String scope, String decodedCartId, String decodedLineItemId, int quantity);

	/**
	 * Gets the applied promotions for the given cart.
	 *
	 * @param scope the scope.
	 * @param decodedCartId the decoded cart ID.
	 * @return a collection of applied promotion IDs.
	 */
	ExecutionResult<Collection<String>> getAppliedPromotionsForCart(String scope, String decodedCartId);

}
