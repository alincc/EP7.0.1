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
import com.elasticpath.rest.definition.carts.LineItemEntity;
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
@Named("cartLineItemTotalsResourceOperator")
@Path(ResourceName.PATH_PART)
public final class CartLineItemTotalsResourceOperatorImpl implements ResourceOperator {

	private final TotalLookup<LineItemEntity> lineItemTotalLookup;

	/**
	 * Constructor.
	 * @param lineItemTotalLookup the  Line item totals look up
	 */
	@Inject
	CartLineItemTotalsResourceOperatorImpl(
			@Named("lineItemTotalLookup")
			final TotalLookup<LineItemEntity> lineItemTotalLookup) {

		this.lineItemTotalLookup = lineItemTotalLookup;
	}

	/**
	 * Handles the READ operations for totals on other resources.
	 *
	 * @param lineItemEntityResourceState the entity resource state
	 * @param operation the resource operation
	 * @return the result
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@AnyResourceUri
			final ResourceState<LineItemEntity> lineItemEntityResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<TotalEntity>> result = lineItemTotalLookup.getTotal(lineItemEntityResourceState);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}
