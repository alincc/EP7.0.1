/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
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
 * Implementation of ResourceOperator for applied Item Promotions.
 */
@Singleton
@Named("appliedPromotionsForItemResourceOperator")
@Path(ResourceName.PATH_PART)
public final class AppliedPromotionsForItemResourceOperator implements ResourceOperator {

	private final ItemPromotionsLookup itemPromotionsLookup;

	/**
	 * Constructor.
	 *
	 * @param itemPromotionsLookup the promotions lookup.
	 */
	@Inject
	AppliedPromotionsForItemResourceOperator(
			@Named("itemPromotionsLookup")
			final ItemPromotionsLookup itemPromotionsLookup) {
		this.itemPromotionsLookup = itemPromotionsLookup;
	}


	/**
	 * Handles reading applied promotions for item.
	 *
	 * @param itemEntityResourceState the item representation
	 * @param operation the {@link com.elasticpath.rest.ResourceOperation}
	 * @return the {@link com.elasticpath.rest.OperationResult}
	 */
	@Path({AnyResourceUri.PATH_PART, AppliedPromotions.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processAppliedPromotionsForItem(
			@AnyResourceUri
			final ResourceState<ItemEntity> itemEntityResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> appliedPromotionsForItem =
				itemPromotionsLookup.getAppliedPromotionsForItem(itemEntityResourceState);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(appliedPromotionsForItem, operation);
	}
}
