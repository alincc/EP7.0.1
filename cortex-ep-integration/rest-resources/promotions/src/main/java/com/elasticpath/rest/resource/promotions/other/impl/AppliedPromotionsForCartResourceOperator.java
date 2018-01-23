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
import com.elasticpath.rest.definition.carts.CartEntity;
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
 * Implementation of ResourceOperator for applied Cart Promotions.
 */
@Singleton
@Named("appliedPromotionsForCartResourceOperator")
@Path(ResourceName.PATH_PART)
public final class AppliedPromotionsForCartResourceOperator implements ResourceOperator {

	private final CartPromotionsLookup cartPromotionsLookup;

	/**
	 * Constructor.
	 *
	 * @param cartPromotionsLookup the promotions lookup.
	 */
	@Inject
	AppliedPromotionsForCartResourceOperator(
			@Named("cartPromotionsLookup")
			final CartPromotionsLookup cartPromotionsLookup) {
		this.cartPromotionsLookup = cartPromotionsLookup;
	}


	/**
	 * Handles reading applied promotions for cart.
	 *
	 * @param cartEntityResourceState the cart representation
	 * @param operation the {@link com.elasticpath.rest.ResourceOperation}
	 * @return the {@link com.elasticpath.rest.OperationResult}
	 */
	@Path({AnyResourceUri.PATH_PART, AppliedPromotions.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processAppliedPromotionsForCart(
			@AnyResourceUri
			final ResourceState<CartEntity> cartEntityResourceState,
			final ResourceOperation operation) {


		ExecutionResult<ResourceState<LinksEntity>> appliedPromotionsForCart =
				cartPromotionsLookup.getAppliedPromotionsForCart(cartEntityResourceState);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(appliedPromotionsForCart, operation);

	}
}
