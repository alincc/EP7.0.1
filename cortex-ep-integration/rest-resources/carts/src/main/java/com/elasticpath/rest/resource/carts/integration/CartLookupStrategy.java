/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;

/**
 * Strategy for cart look up.
 */
public interface CartLookupStrategy {

	/**
	 * Retrieves a cart.
	 *
	 * @param scope the scope
	 * @param decodedCartId the decoded cart id.
	 * @return the {@link CartEntity}
	 */
	ExecutionResult<CartEntity> getCart(String scope, String decodedCartId);

	/**
	 * Get all the cart IDs for the given user.
	 *
	 * @param scope the scope.
	 * @param userId the user.
	 * @return collection of cart IDs.
	 */
	ExecutionResult<Collection<String>> getCartIds(String scope, String userId);

	/**
	 * Get all the cart IDs for the given user that contain the given item.
	 * @param itemId the item id
	 * @return collection of cart IDs
	 */
	ExecutionResult<Collection<String>> findContainingItem(String itemId);
}
