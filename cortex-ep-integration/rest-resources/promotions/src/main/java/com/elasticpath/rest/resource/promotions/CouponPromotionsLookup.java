/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for coupon promotions.
 */
public interface CouponPromotionsLookup {

	/**
	 * Get the applied promotions for a coupon.
	 *
	 * @param couponRepresentation coupon representation
	 * @return applied promotions for the coupon.
	 */
	ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForCoupon(ResourceState<CouponEntity> couponRepresentation);

}
