/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.coupons.CouponEntity;

/**
 * Services that provides look up of coupon from external system.
 */
public interface OrderCouponsLookupStrategy {

	/**
	 * Gets a specific coupon based on order id and decoded coupon id.
	 *
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order id.
	 * @param decodedCouponId the decoded coupon id
	 * @return the coupon from the order
	 */
	ExecutionResult<CouponEntity> getCouponDetailsForOrder(String scope, String decodedOrderId, String decodedCouponId);

	/**
	 * Get all of the applied coupon IDs for an order.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order id
	 * @return collection of coupon ids
	 */
	ExecutionResult<Collection<String>> findCouponIdsForOrder(String scope, String decodedOrderId);
}
