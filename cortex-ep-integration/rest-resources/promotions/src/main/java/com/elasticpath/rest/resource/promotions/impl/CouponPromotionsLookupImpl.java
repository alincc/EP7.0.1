/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.promotions.CouponPromotionsLookup;
import com.elasticpath.rest.resource.promotions.integration.AppliedOrderCouponPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.AppliedPurchaseCouponPromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Lookup class for coupon promotions.
 */
@Singleton
@Named("couponPromotionsLookup")
public final class CouponPromotionsLookupImpl implements CouponPromotionsLookup {

	private final AppliedPurchaseCouponPromotionsLookupStrategy appliedPurchaseCouponPromotionsLookupStrategy;

	private final AppliedOrderCouponPromotionsLookupStrategy appliedOrderCouponPromotionsLookupStrategy;

	private final TransformRfoToResourceState<LinksEntity, Collection<String>,	CouponEntity> purchaseAppliedPromotionsTransformer;

	private final TransformRfoToResourceState<LinksEntity, Collection<String>, CouponEntity> appliedPromotionsTransformer;

	/**
	 * Constructor.
	 *
	 * @param purchaseAppliedPromotionsTransformer          purchase applied promotions transformer.
	 * @param appliedPurchaseCouponPromotionsLookupStrategy applied purchase coupon promotions lookup strategy
	 * @param appliedOrderCouponPromotionsLookupStrategy    applied cart order coupon promotions lookup strategy
	 * @param appliedPromotionsTransformer                  applied promotions transformer.
	 */
	@Inject
	CouponPromotionsLookupImpl(
			@Named("purchaseAppliedPromotionsTransformer")
			final TransformRfoToResourceState<LinksEntity, Collection<String>, CouponEntity> purchaseAppliedPromotionsTransformer,
			@Named("appliedPurchaseCouponPromotionsLookupStrategy")
			final AppliedPurchaseCouponPromotionsLookupStrategy appliedPurchaseCouponPromotionsLookupStrategy,
			@Named("appliedOrderCouponPromotionsLookupStrategy")
			final AppliedOrderCouponPromotionsLookupStrategy appliedOrderCouponPromotionsLookupStrategy,
			@Named("appliedPromotionsTransformer")
			final TransformRfoToResourceState<LinksEntity, Collection<String>, CouponEntity> appliedPromotionsTransformer) {
		this.appliedPurchaseCouponPromotionsLookupStrategy = appliedPurchaseCouponPromotionsLookupStrategy;
		this.purchaseAppliedPromotionsTransformer = purchaseAppliedPromotionsTransformer;
		this.appliedOrderCouponPromotionsLookupStrategy = appliedOrderCouponPromotionsLookupStrategy;
		this.appliedPromotionsTransformer = appliedPromotionsTransformer;
	}


	@Override
	public ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForCoupon(
			final ResourceState<CouponEntity> couponRepresentation) {

		CouponEntity couponEntity = couponRepresentation.getEntity();
		String decodedCouponId = couponEntity.getCouponId();
		String scope = couponRepresentation.getScope();

		String innerType = couponEntity.getParentType();
		String decodedInnerId = couponEntity.getParentId();

		if (innerType.equals(OrdersMediaTypes.ORDER.id())) {
			return getAppliedPromotionsForOrder(decodedCouponId, scope, decodedInnerId, couponRepresentation);
		} else if (innerType.equals(PurchasesMediaTypes.PURCHASE.id())) {
			return getAppliedPromotionsForPurchase(decodedCouponId, scope, decodedInnerId, couponRepresentation);
		} else {
			return ExecutionResultFactory.createServerError(
					"Inner Type " + innerType + "is not supported for coupon promotions look up.");
		}
	}


	private ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForOrder(
			final String decodedCouponId,
			final String scope,
			final String decodedInnerId,
			final ResourceState<CouponEntity> couponRepresentation) {

		Collection<String> promotionIds;
		promotionIds = Assign.ifSuccessful(appliedOrderCouponPromotionsLookupStrategy
				.getAppliedPromotionsForCoupon(scope, decodedCouponId, decodedInnerId));
		ResourceState<LinksEntity> linksRepresentation =
				appliedPromotionsTransformer.transform(promotionIds, couponRepresentation);
		return ExecutionResultFactory.createReadOK(linksRepresentation);
	}

	private ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForPurchase(
			final String decodedCouponId,
			final String scope,
			final String decodedInnerId,
			final ResourceState<CouponEntity> couponRepresentation) {

		Collection<String> promotionIds;
		promotionIds = Assign.ifSuccessful(appliedPurchaseCouponPromotionsLookupStrategy
				.getAppliedPromotionsForCoupon(scope, decodedCouponId, decodedInnerId));
		ResourceState<LinksEntity> linksRepresentation =
				purchaseAppliedPromotionsTransformer.transform(promotionIds, couponRepresentation);
		return ExecutionResultFactory.createReadOK(linksRepresentation);
	}
}
