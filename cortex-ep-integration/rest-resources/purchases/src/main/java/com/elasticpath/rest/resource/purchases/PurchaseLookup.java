/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for Purchases data.
 */
public interface PurchaseLookup {

	/**
	 * Find the purchase data by the given purchase ID.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase ID
	 * @return the execution result with a {@link ResourceState} on success
	 */
	ExecutionResult<ResourceState<PurchaseEntity>> findPurchaseById(String scope, String purchaseId);

	/**
	 * Find all displayable purchases for the user.
	 * This includes all completed purchases, plus the last purchase to fail if it hasn't been completed.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the execution result with a collection of purchase ids
	 */
	ExecutionResult<Collection<String>> findPurchaseIds(String scope, String userId);

	/**
	 * Determine if the order is ready to be purchased.
	 *
	 * @param scope the scope
	 * @param orderId the order ID
	 * @return the execution result with a boolean answer
	 */
	ExecutionResult<Boolean> isOrderPurchasable(String scope, String orderId);
}
