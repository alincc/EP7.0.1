/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Service that provides lookup of coupon promotions data from external systems.
 */
public interface AppliedPurchaseCouponPromotionsLookupStrategy {

	/**
	 * Gets the applied promotion for the given applied coupon.
	 *
	 * @param scope the scope.
	 * @param decodedCouponId the decoded coupon ID.
	 * @param  decodedPurchaseId the decoded cart order id
	 * @return the promotion ID.
	 */
	ExecutionResult<Collection<String>> getAppliedPromotionsForCoupon(String scope, String decodedCouponId, String decodedPurchaseId);
}
