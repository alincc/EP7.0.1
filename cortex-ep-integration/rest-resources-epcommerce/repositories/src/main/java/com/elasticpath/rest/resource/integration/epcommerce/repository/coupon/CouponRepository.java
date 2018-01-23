/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * The facade for {@link Coupon} related operations.
 */
public interface CouponRepository {
	
	/**
	 * True if coupon is valid for system.
	 * @param couponCode coupon code to verify.
	 * @param storeCode  store code to verify coupon is valid within.
	 * @param customerEmail 
	 *
	 * @return true if coupon is valid within store scope, false otherwise.
	 */
	ExecutionResult<Boolean> isCouponValidInStore(String couponCode, String storeCode, String customerEmail);

	/**
	 * Find the Coupon using the coupon code.
	 *
	 * @param couponCode coupon code to use in look up.
	 * @return Coupon corresponding to coupon code.
	 */
	ExecutionResult<Coupon> findByCouponCode(String couponCode);

}
