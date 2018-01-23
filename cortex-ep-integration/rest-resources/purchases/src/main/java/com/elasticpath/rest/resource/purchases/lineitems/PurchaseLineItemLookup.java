/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for purchase line item.
 */
public interface PurchaseLineItemLookup {

	/**
	 * Gets the line item ids for a purchase.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase id.
	 * @return the collection of line item ids.
	 */
	ExecutionResult<Collection<String>> getLineItemIdsForPurchase(String scope, String purchaseId);

	/**
	 * Gets the purchase line item.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase id
	 * @param purchaseLineItemId the purchase line item id
	 * @param parentUri the parent uri or an empty string if there is no parent line item
	 * @param parentLineItemId the parent line item id.
	 * @return the purchase line item component
	 */
	ExecutionResult<ResourceState<PurchaseLineItemEntity>> getPurchaseLineItem(String scope, String purchaseId, String purchaseLineItemId,
			String parentUri, String parentLineItemId);

	/**
	 * Gets whether the line item has components.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase Id.
	 * @param purchaseLineItemId the purchase line item id
	 * @return the collection of line item component IDs
	 */
	ExecutionResult<Boolean> isLineItemBundle(String scope, String purchaseId, String purchaseLineItemId);

	/**
	 * Gets the component ids for line item id.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase id
	 * @param lineItemId the line item id
	 * @return the component ids for line item id
	 */
	ExecutionResult<Collection<String>> getComponentIdsForLineItemId(String scope, String purchaseId, String lineItemId);
}
