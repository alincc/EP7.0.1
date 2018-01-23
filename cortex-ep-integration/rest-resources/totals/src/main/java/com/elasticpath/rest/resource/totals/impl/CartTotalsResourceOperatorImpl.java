/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes the resource operation on profiles.
 */
@Singleton
@Named("cartTotalsResourceOperator")
@Path(ResourceName.PATH_PART)
public final class CartTotalsResourceOperatorImpl implements ResourceOperator {

	private final TotalLookup<CartEntity> cartTotalLookup;

	/**
	 * Contructor.
	 * @param cartTotalLookup cart total lookup.
	 */
	@Inject
	CartTotalsResourceOperatorImpl(
			@Named("cartTotalLookup")
			final TotalLookup<CartEntity> cartTotalLookup) {

		this.cartTotalLookup = cartTotalLookup;
	}

	/**
	 * Handles the READ operations for totals on other resources.
	 *
	 * @param cartEntityResourceState the cart entity resource state
	 * @param operation the resource operation
	 * @return the result
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@AnyResourceUri
			final ResourceState<CartEntity> cartEntityResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<TotalEntity>> result = cartTotalLookup.getTotal(cartEntityResourceState);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}
