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
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.promotions.AppliedPromotions;
import com.elasticpath.rest.resource.promotions.CartPromotionsLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implementation of ResourceOperator for applied Cart Line Item Promotions.
 */
@Singleton
@Named("appliedPromotionsForCartLineItemResourceOperator")
@Path(ResourceName.PATH_PART)
public final class AppliedPromotionsForCartLineItemResourceOperatorImpl implements ResourceOperator {

	private final CartPromotionsLookup cartPromotionsLookup;

	/**
	 * Constructor.
	 *
	 * @param cartPromotionsLookup the promotions lookup.
	 */
	@Inject
	AppliedPromotionsForCartLineItemResourceOperatorImpl(
			@Named("cartPromotionsLookup")
			final CartPromotionsLookup cartPromotionsLookup) {
		this.cartPromotionsLookup = cartPromotionsLookup;
	}


	/**
	 * Handles reading applied promotions for cart line items.
	 *
	 * @param lineItemEntityResourceState the cart line item representation
	 * @param operation the {@link com.elasticpath.rest.ResourceOperation}
	 * @return the {@link com.elasticpath.rest.OperationResult}
	 */
	@Path({AnyResourceUri.PATH_PART, AppliedPromotions.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processAppliedPromotionsForCartLineItem(
			@AnyResourceUri
			final ResourceState<LineItemEntity> lineItemEntityResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> appliedPromotionsForItemInCart =
				cartPromotionsLookup.getAppliedPromotionsForItemInCart(lineItemEntityResourceState);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(appliedPromotionsForItemInCart, operation);
	}
}
