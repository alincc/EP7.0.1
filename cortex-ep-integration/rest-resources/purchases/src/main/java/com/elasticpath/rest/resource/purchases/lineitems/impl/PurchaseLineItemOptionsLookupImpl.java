/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemOptionsLookup;
import com.elasticpath.rest.resource.purchases.lineitems.integration.PurchaseLineItemOptionsLookupStrategy;
import com.elasticpath.rest.resource.purchases.lineitems.transform.PurchaseLineItemOptionTransformer;
import com.elasticpath.rest.resource.purchases.lineitems.transform.PurchaseLineItemOptionValueTransformer;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Lookup for Purchase Line Item Options and values.
 */
@Singleton
@Named("purchaseLineItemOptionsLookup")
public final class PurchaseLineItemOptionsLookupImpl implements PurchaseLineItemOptionsLookup {

	private final PurchaseLineItemOptionsLookupStrategy purchaseLineItemOptionsLookupStrategy;
	private final PurchaseLineItemOptionTransformer purchaseLineItemOptionTransformer;
	private final PurchaseLineItemOptionValueTransformer purchaseLineItemOptionValueTransformer;


	/**
	 * Default Constructor.
	 *
	 * @param purchaseLineItemOptionsLookupStrategy  the purchase line item options lookup strategy
	 * @param purchaseLineItemOptionTransformer      the purchase line item option transformer
	 * @param purchaseLineItemOptionValueTransformer the purchase line item option value transformer
	 */
	@Inject
	public PurchaseLineItemOptionsLookupImpl(
			@Named("purchaseLineItemOptionsLookupStrategy")
			final PurchaseLineItemOptionsLookupStrategy purchaseLineItemOptionsLookupStrategy,
			@Named("purchaseLineItemOptionTransformer")
			final PurchaseLineItemOptionTransformer purchaseLineItemOptionTransformer,
			@Named("purchaseLineItemOptionValueTransformer")
			final PurchaseLineItemOptionValueTransformer purchaseLineItemOptionValueTransformer) {

		this.purchaseLineItemOptionsLookupStrategy = purchaseLineItemOptionsLookupStrategy;
		this.purchaseLineItemOptionTransformer = purchaseLineItemOptionTransformer;
		this.purchaseLineItemOptionValueTransformer = purchaseLineItemOptionValueTransformer;
	}


	@Override
	public ExecutionResult<Collection<String>> findOptionIdsForLineItem(
			final String scope,
			final String purchaseId,
			final String lineItemId) {

		String decodedPurchaseId = Base32Util.decode(purchaseId);
		String decodedLineItemId = Base32Util.decode(lineItemId);
		Collection<String> optionIds = purchaseLineItemOptionsLookupStrategy.findOptionIds(scope,
				decodedPurchaseId, decodedLineItemId).getData();
		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(optionIds));
	}

	@Override
	public ExecutionResult<ResourceState<PurchaseLineItemOptionValueEntity>> findOptionValueForLineItem(
			final String scope,
			final String purchaseId,
			final String lineItemId,
			final String optionId,
			final String valueId,
			final String lineItemUri) {

		String decodedPurchaseId = Base32Util.decode(purchaseId);
		String decodedLineItemId = Base32Util.decode(lineItemId);
		String decodedOptionId = Base32Util.decode(optionId);
		String decodedValueId = Base32Util.decode(valueId);

		PurchaseLineItemOptionValueEntity optionValueDto = Assign.ifSuccessful(purchaseLineItemOptionsLookupStrategy.findOptionValue(scope,
				decodedPurchaseId, decodedLineItemId, decodedOptionId, decodedValueId));
		return ExecutionResultFactory.createReadOK(purchaseLineItemOptionValueTransformer.transformToRepresentation(lineItemUri, optionId,
				valueId, optionValueDto));
	}

	@Override
	public ExecutionResult<ResourceState<PurchaseLineItemOptionEntity>> findOption(
			final String scope,
			final String purchaseId,
			final String lineItemId,
			final String optionId,
			final String lineItemUri) {

		String decodedPurchaseId = Base32Util.decode(purchaseId);
		String decodedLineItemId = Base32Util.decode(lineItemId);
		String decodedOptionId = Base32Util.decode(optionId);

		PurchaseLineItemOptionEntity optionDto = Assign.ifSuccessful(
				purchaseLineItemOptionsLookupStrategy.findOption(scope, decodedPurchaseId, decodedLineItemId, decodedOptionId));
		ResourceState<PurchaseLineItemOptionEntity> purchaseLineItemOptionRepresentation =
				purchaseLineItemOptionTransformer.transformToRepresentation(optionDto, lineItemUri);

		return ExecutionResultFactory.createReadOK(purchaseLineItemOptionRepresentation);
	}
}
