/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemLookup;
import com.elasticpath.rest.resource.purchases.lineitems.integration.PurchaseLineItemLookupStrategy;
import com.elasticpath.rest.resource.purchases.lineitems.transform.PurchaseLineItemTransformer;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Lookup class for purchase line item.
 */
@Singleton
@Named("purchaseLineItemLookup")
public final class PurchaseLineItemLookupImpl implements PurchaseLineItemLookup {

	private final PurchaseLineItemLookupStrategy purchaseLineItemLookupStrategy;
	private final PurchaseLineItemTransformer purchaseLineItemTransformer;


	/**
	 * Default constructor.
	 *
	 * @param purchaseLineItemLookupStrategy the purchase line item lookup strategy
	 * @param purchaseLineItemTransformer    the transformer.
	 */
	@Inject
	PurchaseLineItemLookupImpl(
			@Named("purchaseLineItemLookupStrategy")
			final PurchaseLineItemLookupStrategy purchaseLineItemLookupStrategy,
			@Named("purchaseLineItemTransformer")
			final PurchaseLineItemTransformer purchaseLineItemTransformer) {

		this.purchaseLineItemLookupStrategy = purchaseLineItemLookupStrategy;
		this.purchaseLineItemTransformer = purchaseLineItemTransformer;
	}


	@Override
	public ExecutionResult<Collection<String>> getLineItemIdsForPurchase(final String scope, final String purchaseId) {

		String decodedPurchaseId = Base32Util.decode(purchaseId);
		Collection<String> lineItemIds = Assign.ifSuccessful(
				purchaseLineItemLookupStrategy.findLineItemIds(scope, decodedPurchaseId)
		);
		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(lineItemIds));
	}

	@Override
	public ExecutionResult<ResourceState<PurchaseLineItemEntity>> getPurchaseLineItem(
			final String scope,
			final String purchaseId,
			final String purchaseLineItemId,
			final String parentUri,
			final String parentLineItemId) {

		PurchaseLineItemEntity lineItemDto = Assign.ifSuccessful(
				retrieveLineItem(scope, purchaseId, purchaseLineItemId, parentLineItemId)
		);
		ResourceState<PurchaseLineItemEntity> representation =
				purchaseLineItemTransformer.transformToRepresentation(
						scope, purchaseId, purchaseLineItemId, lineItemDto, parentUri
				);
		return ExecutionResultFactory.createReadOK(representation);
	}

	private ExecutionResult<PurchaseLineItemEntity> retrieveLineItem(
			final String scope,
			final String purchaseId,
			final String purchaseLineItemId,
			final String parentLineItemId) {

		String decodedPurchaseId = Base32Util.decode(purchaseId);
		String decodedLineItemId = Base32Util.decode(purchaseLineItemId);
		String decodedParentLineItemId = null;
		if (StringUtils.isNotEmpty(parentLineItemId)) {
			decodedParentLineItemId = Base32Util.decode(parentLineItemId);
		}

		return purchaseLineItemLookupStrategy.getLineItem(scope, decodedPurchaseId, decodedLineItemId, decodedParentLineItemId);
	}


	@Override
	public ExecutionResult<Boolean> isLineItemBundle(final String scope, final String purchaseId, final String lineItemId) {

		String decodedPurchaseId = Base32Util.decode(purchaseId);
		String decodedLineItemId = Base32Util.decode(lineItemId);

		return purchaseLineItemLookupStrategy.isLineItemBundle(scope, decodedPurchaseId, decodedLineItemId);
	}

	@Override
	public ExecutionResult<Collection<String>> getComponentIdsForLineItemId(
			final String scope,
			final String purchaseId,
			final String lineItemId) {

		boolean isLineItemBundle = Assign.ifSuccessful(isLineItemBundle(scope, purchaseId, lineItemId));

		Ensure.isTrue(isLineItemBundle,
				OnFailure.returnNotFound("Components for lineitem with id %s do not exist", lineItemId));
		String decodedPurchaseId = Base32Util.decode(purchaseId);
		String decodedLineItemId = Base32Util.decode(lineItemId);
		Collection<String> componentIds = Assign.ifSuccessful(purchaseLineItemLookupStrategy.getComponentIdsForLineItemId(scope,
				decodedPurchaseId, decodedLineItemId));

		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(componentIds));
	}
}
