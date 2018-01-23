/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Writer class for create/update operations on coupons.
 */
public interface OrderCouponWriter {

	/**
	 * Un-apply a coupon from an order by deleting it.
	 *
	 * @param resourceState the order representation.
	 * @param couponId the coupon id
	 * @return an {@link ExecutionResult} indicating success/failure of deleting the coupon
	 * NOT_FOUND if order or coupon is not found
	 * SERVER_ERROR if coupon fails to be deleted
	 */
	ExecutionResult<Void> deleteCouponFromOrder(ResourceState<OrderEntity> resourceState, String couponId);

	/**
	 * Apply coupon to an order by creating it.
	 *
	 * @param resourceState order representation
	 * @param form form
	 * @return an {@link ExecutionResult} with coupon representation of newly created coupon
	 * NOT_FOUND if order is not found
	 * STATE_FAILURE if coupon is not found
	 * SERVER_ERROR if coupon fails to be applied
	 */
	ExecutionResult<ResourceState<ResourceEntity>> createCouponForOrder(ResourceState<OrderEntity> resourceState, ResourceState<CouponEntity> form);
}
