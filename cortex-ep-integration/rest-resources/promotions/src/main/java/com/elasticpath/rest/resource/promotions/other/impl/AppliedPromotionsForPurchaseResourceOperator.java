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
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.promotions.AppliedPromotions;
import com.elasticpath.rest.resource.promotions.PurchasePromotionsLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implementation of ResourceOperator for applied Purchase Promotions.
 */
@Singleton
@Named("appliedPromotionsForPurchaseResourceOperator")
@Path(ResourceName.PATH_PART)
public final class AppliedPromotionsForPurchaseResourceOperator implements ResourceOperator {

	private final PurchasePromotionsLookup purchasePromotionsLookup;

	/**
	 * Constructor.
	 *
	 * @param purchasePromotionsLookup the promotions lookup.
	 */
	@Inject
	AppliedPromotionsForPurchaseResourceOperator(
			@Named("purchasePromotionsLookup")
			final PurchasePromotionsLookup purchasePromotionsLookup) {
		this.purchasePromotionsLookup = purchasePromotionsLookup;
	}


	/**
	 * Handles reading applied promotions for purchase.
	 *
	 * @param purchaseEntityResourceState the purchase representation
	 * @param operation the {@link com.elasticpath.rest.ResourceOperation}
	 * @return the {@link com.elasticpath.rest.OperationResult}
	 */
	@Path({AnyResourceUri.PATH_PART, AppliedPromotions.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processAppliedPromotionsForPurchase(
			@AnyResourceUri
			final ResourceState<PurchaseEntity> purchaseEntityResourceState,
			final ResourceOperation operation) {


		ExecutionResult<ResourceState<LinksEntity>> appliedPromotionsForPurchase =
				purchasePromotionsLookup.getAppliedPromotionsForPurchase(purchaseEntityResourceState);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(appliedPromotionsForPurchase, operation);

	}
}
