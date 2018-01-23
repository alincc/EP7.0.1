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
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.PurchasePromotionsLookup;
import com.elasticpath.rest.resource.promotions.integration.AppliedPurchasePromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Look up class for purchase promotions.
 */
@Singleton
@Named("purchasePromotionsLookup")
public final class PurchasePromotionsLookupImpl implements PurchasePromotionsLookup {

	private final AppliedPurchasePromotionsLookupStrategy appliedPurchasePromotionsLookupStrategy;

	private final TransformRfoToResourceState<LinksEntity, Collection<String>,	PurchaseEntity> purchaseAppliedPromotionsTransformer;

	/**
	 * Constructor.
	 *
	 * @param appliedPurchasePromotionsLookupStrategy the applied promotions strategy for purchase.
	 * @param purchaseAppliedPromotionsTransformer    the applied promotions transformer.
	 */
	@Inject
	PurchasePromotionsLookupImpl(
			@Named("appliedPurchasePromotionsLookupStrategy")
			final AppliedPurchasePromotionsLookupStrategy appliedPurchasePromotionsLookupStrategy,
			@Named("purchaseAppliedPromotionsTransformer")
			final TransformRfoToResourceState<LinksEntity, Collection<String>,	PurchaseEntity> purchaseAppliedPromotionsTransformer) {

		this.appliedPurchasePromotionsLookupStrategy = appliedPurchasePromotionsLookupStrategy;
		this.purchaseAppliedPromotionsTransformer = purchaseAppliedPromotionsTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForPurchase(final ResourceState<PurchaseEntity> purchaseRepresentation) {

		String decodedPurchaseId = Base32Util.decode(purchaseRepresentation.getEntity().getPurchaseId());
		String scope = purchaseRepresentation.getScope();

		Collection<String> promotionIds = Assign
				.ifSuccessful(appliedPurchasePromotionsLookupStrategy.getAppliedPromotionsForPurchase(scope, decodedPurchaseId));

		ResourceState<LinksEntity> linksRepresentation =
				purchaseAppliedPromotionsTransformer.transform(promotionIds, purchaseRepresentation);

		return ExecutionResultFactory.createReadOK(linksRepresentation);
	}
}
