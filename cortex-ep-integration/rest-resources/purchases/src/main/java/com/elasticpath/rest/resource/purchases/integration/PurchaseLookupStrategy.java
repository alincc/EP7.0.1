/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;

/**
 * The Interface PurchaseLookupStrategy.
 */
public interface PurchaseLookupStrategy {

	/**
	 * Gets the purchase.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase ID
	 * @return the purchase DTO
	 */
	ExecutionResult<PurchaseEntity> getPurchase(String scope, String decodedPurchaseId);

	/**
	 * Gets the purchase IDs for the user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the collection of purchase IDs
	 */
	ExecutionResult<Collection<String>> getPurchaseIds(String scope, String userId);

	/**
	 * Checks if the order is purchasable.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order ID
	 * @return true, if the order is purchasable, false otherwise
	 */
	ExecutionResult<Boolean> isOrderPurchasable(String scope, String decodedOrderId);
}