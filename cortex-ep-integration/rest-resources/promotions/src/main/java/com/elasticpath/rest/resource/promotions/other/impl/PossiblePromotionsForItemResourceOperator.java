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
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.promotions.AppliedPromotions;
import com.elasticpath.rest.resource.promotions.ItemPromotionsLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implementation of ResourceOperator for Possible Item Promotions.
 */
@Singleton
@Named("possiblePromotionsForItemResourceOperator")
@Path(ResourceName.PATH_PART)
public final class PossiblePromotionsForItemResourceOperator implements ResourceOperator {

	private final ItemPromotionsLookup itemPromotionsLookup;

	/**
	 * Constructor.
	 *
	 * @param itemPromotionsLookup the promotions lookup.
	 */
	@Inject
	PossiblePromotionsForItemResourceOperator(
			@Named("itemPromotionsLookup")
			final ItemPromotionsLookup itemPromotionsLookup) {
		this.itemPromotionsLookup = itemPromotionsLookup;
	}


	/**
	 * Handles reading possible promotions for item.
	 *
	 * @param itemEntityResourceState the item representation
	 * @param operation the {@link com.elasticpath.rest.ResourceOperation}
	 * @return the {@link com.elasticpath.rest.OperationResult}
	 */
	@Path({AnyResourceUri.PATH_PART, AppliedPromotions.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processPossiblePromotionsForItem(
			@AnyResourceUri
			final ResourceState<ItemEntity> itemEntityResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> possiblePromotionsForItem =
				itemPromotionsLookup.getPossiblePromotionsForItem(itemEntityResourceState);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(possiblePromotionsForItem, operation);

	}
}
