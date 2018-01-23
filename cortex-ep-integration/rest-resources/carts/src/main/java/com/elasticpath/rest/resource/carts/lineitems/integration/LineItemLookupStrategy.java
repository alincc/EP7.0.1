/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.integration;

import java.util.Collection;


import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;

/**
 * Strategy for cart lineitem look up.
 */
public interface LineItemLookupStrategy {

	/**
	 * Gets the line item ids for cart.
	 *
	 * @param scope the scope
	 * @param decodedCartId the decoded cart id
	 * @param decodedLineItemId the decoded line item id
	 * @return the line item ids for cart
	 */
	ExecutionResult<LineItemEntity> getLineItem(String scope, String decodedCartId, String decodedLineItemId);

	/**
	 * Gets the line item ids for cart.
	 *
	 * @param scope the scope
	 * @param decodedCartId the decoded cart id
	 * @return the line item ids for cart
	 */
	ExecutionResult<Collection<String>> getLineItemIdsForCart(String scope, String decodedCartId);

	/**
	 * Checks if the item is purchasable.
	 *
	 * @param scope the scope
	 * @param itemId the item ID
	 * @return the execution result with the boolean result
	 */
	ExecutionResult<Boolean> isItemPurchasable(String scope, String itemId);

	/**
	 * Gets the item configuration details.
	 *
	 * @param scope the scope
	 * @param itemId the item ID
	 * @return the execution result with configuration details
	 */
	ExecutionResult<LineItemConfigurationEntity> getItemConfiguration(String scope, String itemId);
}
