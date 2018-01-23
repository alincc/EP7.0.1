/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Service that provides lookup of cart order coupon promotions data from external systems.
 */
public interface AppliedOrderCouponPromotionsLookupStrategy {

	/**
	 * Gets the applied promotion for the given cart order coupon.
	 *
	 * @param scope the scope.
	 * @param decodedCouponId the decoded coupon ID.
	 * @param  decodedCartOrderId the decoded cart order id
	 * @return the promotion ID.
	 */
	ExecutionResult<Collection<String>> getAppliedPromotionsForCoupon(String scope, String decodedCouponId, String decodedCartOrderId);
}
