/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.coupons.CouponEntity;

/**
 * Coupon write strategy for integrations.
 */
public interface OrderCouponWriterStrategy {

	/**
	 * Delete the specified coupon from the order.
	 *
	 * @param scope the scope.
	 * @param decodedOrderId decoded order id
	 * @param decodedCouponId decoded coupon id
	 * @return DELETE_OK if delete succeeds.
	 * NOT_FOUND if coupon or order is not found.
	 * SERVER_ERROR if an error occurs deleting the coupon
	 */
	ExecutionResult<Void> deleteCouponForOrder(String scope, String decodedOrderId, String decodedCouponId);
	
	/**
	 * Create a coupon for an order.
	 *
	 * @param storeCode the scope
	 * @param decodedOrderId the decoded order id
	 * @param form the form to create the coupon from
	 * @return CREATE_OK if creation succeeds.
	 * NOT_FOUND if order is not found.
	 * STATE_FAILURE if coupon is not found
	 * SERVER_ERROR if an error occurs creating the coupon
	 */
	ExecutionResult<CouponEntity> createCouponForOrder(String storeCode, String decodedOrderId, CouponEntity form);

}
