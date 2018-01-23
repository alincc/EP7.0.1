/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.promotions.PromotionsLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes the resource operation on Example.
 */
@Singleton
@Named("promotionsResourceOperator")
@Path(ResourceName.PATH_PART)
public final class PromotionsResourceOperatorImpl implements ResourceOperator {

	private final PromotionsLookup promotionsLookup;

	/**
	 * Constructor.
	 * @param promotionsLookup the promotions lookup.
	 *
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	@Inject
	PromotionsResourceOperatorImpl(
			@Named("promotionsLookup")
			final PromotionsLookup promotionsLookup) {
		this.promotionsLookup = promotionsLookup;
	}

	/**
	 * Handles reading promotion details for a purchase.
	 *
	 * @param purchaseRepresentation the purchase representation
	 * @param promotionId            the promotion id
	 * @param operation              the {@link ResourceOperation}
	 * @return the {@link OperationResult}
	 */
	@Path({AnyResourceUri.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadPromotionDetailsForPurchase(
			@AnyResourceUri
			final ResourceState<PurchaseEntity> purchaseRepresentation,
			@ResourceId
			final String promotionId,
			final ResourceOperation operation) {

		String scope = purchaseRepresentation.getScope();
		String purchaseId = purchaseRepresentation.getEntity().getPurchaseId();
		ExecutionResult<ResourceState<PromotionEntity>> purchasePromotionDetails =
				promotionsLookup.getPurchasePromotionDetails(
						scope,
						promotionId,
						Base32Util.decode(purchaseId),
						purchaseRepresentation
				);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(purchasePromotionDetails, operation);
	}

	/**
	 * Handles reading promotion details for a coupon.
	 *
	 * @param couponRepresentation the coupon representation
	 * @param promotionId          the promotion id
	 * @param operation            the {@link ResourceOperation}
	 * @return the {@link OperationResult}
	 */
	@Path({AnyResourceUri.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadPromotionDetailsForCoupon(
			@AnyResourceUri
			final ResourceState<CouponEntity> couponRepresentation,
			@ResourceId
			final String promotionId,
			final ResourceOperation operation) {

		String scope = couponRepresentation.getScope();
		String purchaseId = couponRepresentation.getEntity().getParentId();
		ExecutionResult<ResourceState<PromotionEntity>> purchasePromotionDetails =
				promotionsLookup.getPurchasePromotionDetails(scope, promotionId, purchaseId, couponRepresentation);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(purchasePromotionDetails, operation);
	}

	/**
	 * Handles READ for promotion details for scope and ID.
	 *
	 * @param scope       the scope.
	 * @param promotionId The promotion ID.
	 * @param operation   The resource Operation you are responding to.
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadPromotionDetails(
			@Scope
			final String scope,
			@ResourceId
			final String promotionId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<PromotionEntity>> result = promotionsLookup.getPromotionDetails(scope, promotionId);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}
