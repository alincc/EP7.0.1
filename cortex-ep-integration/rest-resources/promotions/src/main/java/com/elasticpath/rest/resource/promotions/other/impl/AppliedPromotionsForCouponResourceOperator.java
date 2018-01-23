/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.other.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.promotions.AppliedPromotions;
import com.elasticpath.rest.resource.promotions.CouponPromotionsLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implementation of ResourceOperator for applied Coupon Promotions.
 */
@Singleton
@Named("appliedPromotionsForCouponStrategyResourceOperator")
@Path(ResourceName.PATH_PART)
public final class AppliedPromotionsForCouponResourceOperator implements ResourceOperator {

	private final CouponPromotionsLookup couponPromotionsLookup;

	/**
	 * Constructor.
	 *
	 * @param couponPromotionsLookup the promotions lookup.
	 */
	@Inject
	AppliedPromotionsForCouponResourceOperator(
			@Named("couponPromotionsLookup")
			final CouponPromotionsLookup couponPromotionsLookup) {
		this.couponPromotionsLookup = couponPromotionsLookup;
	}


	/**
	 * Handles reading applied promotions for coupon.
	 *
	 * @param couponEntityResourceState the coupon representation
	 * @param operation the {@link com.elasticpath.rest.ResourceOperation}
	 * @return the {@link com.elasticpath.rest.OperationResult}
	 */
	@Path({AnyResourceUri.PATH_PART, AppliedPromotions.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processAppliedPromotionsForCoupon(
			@AnyResourceUri
			final ResourceState<CouponEntity> couponEntityResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> appliedPromotionsForCoupon =
				couponPromotionsLookup.getAppliedPromotionsForCoupon(couponEntityResourceState);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(appliedPromotionsForCoupon, operation);
	}
}
