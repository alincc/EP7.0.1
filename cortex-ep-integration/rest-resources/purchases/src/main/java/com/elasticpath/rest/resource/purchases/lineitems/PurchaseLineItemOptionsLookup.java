/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for purchase line item options and values.
 */
public interface PurchaseLineItemOptionsLookup {

	/**
	 * Find option ids for line item.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase id
	 * @param lineItemId the item id
	 * @return the execution result
	 */
	ExecutionResult<Collection<String>> findOptionIdsForLineItem(String scope, String purchaseId, String lineItemId);

	/**
	 * Find option value for item.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase id
	 * @param lineItemId the item id
	 * @param optionId the option id
	 * @param valueId the value id
	 * @param lineItemUri the line item uri
	 * @return the execution result
	 */
	ExecutionResult<ResourceState<PurchaseLineItemOptionValueEntity>> findOptionValueForLineItem(String scope, String purchaseId, String lineItemId,
			String optionId, String valueId, String lineItemUri);

	/**
	 * Find option for line item with the given option ID.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase id
	 * @param lineItemId the item id
	 * @param optionId the option id
	 * @param lineItemUri the line item uri
	 * @return the execution result
	 */
	ExecutionResult<ResourceState<PurchaseLineItemOptionEntity>> findOption(String scope, String purchaseId, String lineItemId,
			String optionId, String lineItemUri);
}
