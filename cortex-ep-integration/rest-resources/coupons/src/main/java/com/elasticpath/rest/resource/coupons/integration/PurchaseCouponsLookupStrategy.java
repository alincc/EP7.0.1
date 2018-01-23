/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.coupons.CouponEntity;

/**
 * Integration point for retrieving coupon information for purchases.
 */
public interface PurchaseCouponsLookupStrategy {

	/**
	 * Get the collection of coupon ids for the purchase.
	 *
	 * @param scope             scope
	 * @param decodedPurchaseId the decoded purchase id.
	 * @return the collection of decoded coupon ids for purchase.
	 */
	Iterable<String> getCouponsForPurchase(String scope, String decodedPurchaseId);

	/**
	 * Get the coupon entity corresponding to the coupon for the purchase.
	 *
	 * @param scope             scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @param decodedCouponId   the decoded coupon id
	 * @return the coupon entity corresponding to the coupon for the purchase.
	 */
	ExecutionResult<CouponEntity> getCouponDetailsForPurchase(String scope,
															String decodedPurchaseId,
															String decodedCouponId);

}
