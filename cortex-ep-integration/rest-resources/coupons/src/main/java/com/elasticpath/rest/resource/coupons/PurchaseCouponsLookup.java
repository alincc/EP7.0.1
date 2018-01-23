/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Coupons look up for purchase.
 */
public interface PurchaseCouponsLookup {

	/**
	 * Return a links representation of all coupons applied to a purchase.
	 *
	 *
	 * @param purchaseRepresentation the purchase to get the coupons from
	 * @return all coupons applied to a purchase.
	 */
	Iterable<String> getCouponLinksForPurchase(ResourceState<PurchaseEntity> purchaseRepresentation);

	/**
	 * Return a coupon representation of the coupon for the purchase.
	 *
	 * @param other purchase representation.
	 * @param couponId coupon id
	 * @return a coupon representation of the coupon for the purchase.
	 */
	ExecutionResult<ResourceState<CouponEntity>> getCouponDetailsForPurchase(ResourceState<PurchaseEntity> other, String couponId);

}
