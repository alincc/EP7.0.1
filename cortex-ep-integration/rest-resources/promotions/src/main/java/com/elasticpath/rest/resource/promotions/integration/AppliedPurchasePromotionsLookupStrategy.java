/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Strategy for looking up applied promotions for purchases.
 */
public interface AppliedPurchasePromotionsLookupStrategy {

	/**
	 * Get the collection of applied promotions for a purchase.
	 * 
	 * @param scope scope
	 * @param purchaseId the purchase id.
	 * @return the collection of applied promotions for a purchase.
	 */
	ExecutionResult<Collection<String>> getAppliedPromotionsForPurchase(String scope, String purchaseId);

}
