/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Contains behavior for retrieving coupon information.
 */
public interface OrderCouponsLookup {

	/**
	 * Return the coupon details for the order.
	 *
	 * @param otherRepresentation order representation
	 * @param couponId coupon id
	 * @return the coupon details of the coupon with the id for the order representation.
	 */
	ExecutionResult<ResourceState<CouponEntity>> getCouponDetailsForOrder(ResourceState<OrderEntity> otherRepresentation, String couponId);

	/**
	 * Find all of the applied coupon IDs for the provided order.
	 * @param representation the order representation
	 *
	 * @return a collection of coupon IDs.
	 */
	ExecutionResult<ResourceState<InfoEntity>> getCouponInfoForOrder(ResourceState<OrderEntity> representation);
	
	/**
	 * Return the coupon form for an order.
	 *
	 * @param representation other representation
	 * @return the coupon form for an order.
	 */
	ExecutionResult<ResourceState<CouponEntity>> getCouponFormForOrder(ResourceState<OrderEntity> representation);
}
