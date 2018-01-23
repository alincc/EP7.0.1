/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Writer class for update/creating Line Item data.
 */
public interface LineItemWriter {

	/**
	 * Delete the line item based on the Cart and Line Item IDs.
	 *
	 * @param cartRepresentation the cart {@link ResourceState}
	 * @param lineItemId the line item ID.
	 * @return the result of the line item removal.
	 */
	ExecutionResult<Void> remove(ResourceState<CartEntity> cartRepresentation, String lineItemId);

	/**
	 * Delete all line items in the Cart given.
	 *
	 * @param cart the cart {@link ResourceState}
	 * @return the result of the removal of all line items from the cart.
	 */
	ExecutionResult<Void> removeAll(ResourceState<CartEntity> cart);

	/**
	 * Adds a new line item to the cart.
	 *
	 * @param cartRepresentation the cart {@link ResourceState}
	 * @param itemId the item id.
	 * @param postedLineItemEntity the quantity to set.
	 * @return the new line item id
	 */
	ExecutionResult<ResourceState<ResourceEntity>> addLineItemToCart(ResourceState<CartEntity> cartRepresentation,
													String itemId,
													LineItemEntity postedLineItemEntity);

	/**
	 * Updates the existing line item with the given quantity.
	 *
	 * @param cartRepresentation the cart {@link ResourceState}
	 * @param lineItemId the line item id.
	 * @param updatedLineItemEntity the quantity of the item to add
	 * @return success or failure result of update.
	 */
	ExecutionResult<Void> update(ResourceState<CartEntity> cartRepresentation, String lineItemId, LineItemEntity updatedLineItemEntity);
}
