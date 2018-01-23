/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Cart info look up class.
 */
public interface CartLookup {

	/**
	 * Find the cart with the given cart ID and scope.
	 *
	 * @param scope the scope.
	 * @param cartId the cart ID.
	 * @return The cart if found.
	 */
	ExecutionResult<ResourceState<CartEntity>> findCart(String scope, String cartId);

	/**
	 * Find all the cart IDs for the provided user.
	 *
	 * @param scope the scope
	 * @param userId the user
	 * @return a collection of cart IDs.
	 */
	ExecutionResult<Collection<String>> findCartIds(String scope, String userId);
	
	/**
	 * Get links to the carts that contain the given item.
	 * @param scope the scope
	 * @param itemId the id of the item to look for
	 * @return an collection of the links
	 */
	ExecutionResult<Collection<ResourceLink>> getCartMemberships(String scope, String itemId);
}
