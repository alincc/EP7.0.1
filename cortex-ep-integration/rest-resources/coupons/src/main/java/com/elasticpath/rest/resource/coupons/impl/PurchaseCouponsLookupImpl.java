/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import static com.elasticpath.rest.id.util.Base32Util.decode;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.coupons.PurchaseCouponsLookup;
import com.elasticpath.rest.resource.coupons.integration.PurchaseCouponsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Implementation of lookup of coupons for purchases.
 */
@Named("purchaseCouponsLookup")
@Singleton
public class PurchaseCouponsLookupImpl implements PurchaseCouponsLookup {

	private final PurchaseCouponsLookupStrategy purchaseCouponsLookupStrategy;

	private final TransformRfoToResourceState<CouponEntity, CouponEntity, PurchaseEntity> couponDetailsTransformer;

	/**
	 * Instantiate purchase coupons look up.
	 *
	 * @param purchaseCouponsLookupStrategy purchase Coupons Lookup Strategy
	 * @param couponDetailsTransformer coupon details transformer
	 */
	@Inject
	PurchaseCouponsLookupImpl(
			@Named("purchaseCouponsLookupStrategy")
			final PurchaseCouponsLookupStrategy purchaseCouponsLookupStrategy,
			@Named("couponDetailsTransformer")
			final TransformRfoToResourceState<CouponEntity, CouponEntity, PurchaseEntity> couponDetailsTransformer) {
		this.purchaseCouponsLookupStrategy = purchaseCouponsLookupStrategy;
		this.couponDetailsTransformer = couponDetailsTransformer;
	}

	@Override
	public Iterable<String> getCouponLinksForPurchase(final ResourceState<PurchaseEntity> purchaseRepresentation) {
		String scope = purchaseRepresentation.getScope();
		String decodedPurchaseId = decode(
				purchaseRepresentation.getEntity()
						.getPurchaseId()
		);

		return purchaseCouponsLookupStrategy.getCouponsForPurchase(scope, decodedPurchaseId);
	}

	@Override
	public ExecutionResult<ResourceState<CouponEntity>> getCouponDetailsForPurchase(
			final ResourceState<PurchaseEntity> purchaseRepresentation,
			final String couponId) {

		String decodedCouponId = decode(couponId);
		String decodedPurchaseId = decode(purchaseRepresentation.getEntity()
				.getPurchaseId());
		String scope = purchaseRepresentation.getScope();

		CouponEntity coupon = Assign.ifSuccessful(purchaseCouponsLookupStrategy.getCouponDetailsForPurchase(scope, decodedPurchaseId,
				decodedCouponId));

		CouponEntity enhancedCoupon = CouponEntity.builderFrom(coupon)
				.withParentId(decodedPurchaseId)
				.withParentType(purchaseRepresentation.getSelf().getType())
				.build();

		ResourceState<CouponEntity> couponRepresentation = couponDetailsTransformer.transform(enhancedCoupon,
				purchaseRepresentation);

		return ExecutionResultFactory.createReadOK(couponRepresentation);
	}

}
