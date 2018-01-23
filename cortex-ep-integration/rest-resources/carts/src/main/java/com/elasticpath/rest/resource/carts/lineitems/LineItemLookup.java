/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Line item info look up class.
 */
public interface LineItemLookup {

	/**
	 * Look for line item based on the Cart and Line Item IDs.
	 *
	 *
	 *
	 * @param cartRepresentation the cart {@link com.elasticpath.rest.schema.ResourceState}
	 * @param lineItemId the line item ID.
	 * @return the line item object, null if not found.
	 */
	ExecutionResult<ResourceState<LineItemEntity>> find(ResourceState<CartEntity> cartRepresentation, String lineItemId);

	/**
	 * Get the collection of line items for the given cart.
	 *
	 * @param cartId the cart id
	 * @param scope the scope
	 * @return a collection of line items for the cart.
	 */
	ExecutionResult<Collection<String>> findIdsForCart(String cartId, String scope);

	/**
	 * Returns whether or not a line item is purchasable (and can be added to cart).
	 *
	 * @param scope the scope
	 * @param itemId the item id
	 * @return true if the line item purchasable
	 */
	ExecutionResult<Boolean> isItemPurchasable(String scope, String itemId);
}
