/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.purchases.PurchaseLookup;
import com.elasticpath.rest.resource.purchases.integration.PurchaseLookupStrategy;
import com.elasticpath.rest.resource.purchases.transformer.PurchaseTransformer;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implementation of PurchaseLookup for accessing data from core.
 */
@Singleton
@Named("purchaseLookup")
public final class PurchaseLookupImpl implements PurchaseLookup {

	private final PurchaseTransformer purchaseTransformer;
	private final PurchaseLookupStrategy purchaseLookupStrategy;


	/**
	 * Default constructor.
	 *
	 * @param purchaseTransformer    the resource transformer
	 * @param purchaseLookupStrategy the purchase lookup strategy
	 */
	@Inject
	public PurchaseLookupImpl(
			@Named("purchaseTransformer")
			final PurchaseTransformer purchaseTransformer,
			@Named("purchaseLookupStrategy")
			final PurchaseLookupStrategy purchaseLookupStrategy) {

		this.purchaseTransformer = purchaseTransformer;
		this.purchaseLookupStrategy = purchaseLookupStrategy;
	}

	@Override
	public ExecutionResult<ResourceState<PurchaseEntity>> findPurchaseById(final String scope, final String purchaseId) {

		String decodedPurchaseId = Base32Util.decode(purchaseId);
		PurchaseEntity purchaseEntity = Assign.ifSuccessful(purchaseLookupStrategy.getPurchase(scope, decodedPurchaseId));
		return ExecutionResultFactory.createReadOK(purchaseTransformer.transformToRepresentation(scope, purchaseEntity));
	}

	@Override
	public ExecutionResult<Collection<String>> findPurchaseIds(final String scope, final String userId) {

		Collection<String> purchaseIds = Assign.ifSuccessful(purchaseLookupStrategy.getPurchaseIds(scope, userId));
		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(purchaseIds));
	}

	@Override
	public ExecutionResult<Boolean> isOrderPurchasable(final String scope, final String orderId) {
		String decodedOrderId = Base32Util.decode(orderId);
		return purchaseLookupStrategy.isOrderPurchasable(scope, decodedOrderId);
	}
}
