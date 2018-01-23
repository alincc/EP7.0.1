/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK;
import static com.elasticpath.rest.resource.coupons.constant.CouponsConstants.PURCHASES_FOR_COUPONS_LIST;
import static com.elasticpath.rest.resource.coupons.constant.CouponsConstants.PURCHASE_COUPON_MAX_AGE;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;
import static com.elasticpath.rest.schema.util.ResourceStateUtil.getSelfUri;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.coupons.PurchaseCouponsLookup;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;

/**
 * Processes the resource operation for purchase coupons.
 */
@Singleton
@Named("purchaseCouponsResourceOperator")
@Path(ResourceName.PATH_PART)
public final class PurchaseCouponsResourceOperatorImpl implements ResourceOperator {

	private final CouponsUriBuilderFactory couponsUriBuilderFactory;
	private final PurchaseCouponsLookup purchaseCouponsLookup;

	/**
	 * Initialize the resource operator.
	 *
	 * @param couponsUriBuilderFactory couponsUriBuilderFactory
	 * @param purchaseCouponsLookup    purchase coupons lookup.
	 */
	@Inject
	PurchaseCouponsResourceOperatorImpl(
			@Named("couponsUriBuilderFactory")
			final CouponsUriBuilderFactory couponsUriBuilderFactory,
			@Named("purchaseCouponsLookup")
			final PurchaseCouponsLookup purchaseCouponsLookup) {
		this.couponsUriBuilderFactory = couponsUriBuilderFactory;
		this.purchaseCouponsLookup = purchaseCouponsLookup;
	}

	/**
	 * Handles READ for specific coupon from purchase resource.
	 *
	 *
	 * @param purchaseRepresentation the purchase representation to be used in the read.
	 * @param couponId               coupon id
	 * @param operation              operation
	 * @return the operation result.
	 */
	@Path({SingleResourceUri.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadCouponDetailsForPurchase(
			@SingleResourceUri
			final ResourceState<PurchaseEntity> purchaseRepresentation,
			@ResourceId
			final String couponId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<CouponEntity>> couponDetailsForPurchase =
				purchaseCouponsLookup.getCouponDetailsForPurchase(purchaseRepresentation, couponId);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(couponDetailsForPurchase, operation);
	}

	/**
	 * Process reading coupons for another resource.
	 *
	 * @param purchaseState other representation
	 * @param  operation ResourceOperation
	 * @return the operation result
	 */
	@Path({SingleResourceUri.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadCouponsForPurchase(
			@SingleResourceUri
			final ResourceState<PurchaseEntity> purchaseState,
			final ResourceOperation operation) {

		String selfUri = couponsUriBuilderFactory.get()
				.setSourceUri(getSelfUri(purchaseState))
				.build();

		LinksEntity resourceEntity = LinksEntity.builder()
				.withElementListId(purchaseState.getEntity()
						.getPurchaseId())
				.withName(PURCHASES_FOR_COUPONS_LIST)
				.build();
		ResourceState<LinksEntity> linksState = ResourceState.Builder
				.create(resourceEntity)
				.withSelf(createSelf(selfUri))
				.withResourceInfo(ResourceInfo.builder()
						.withMaxAge(PURCHASE_COUPON_MAX_AGE)
						.build())
				.withScope(purchaseState.getScope())
				.build();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(createReadOK(linksState), operation);
	}

}
