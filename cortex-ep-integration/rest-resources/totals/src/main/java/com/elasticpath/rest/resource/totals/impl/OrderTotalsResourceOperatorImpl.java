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
import com.elasticpath.rest.definition.orders.OrderEntity;
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
@Named("orderTotalsResourceOperator")
@Path(ResourceName.PATH_PART)
public final class OrderTotalsResourceOperatorImpl implements ResourceOperator {

	private final TotalLookup<OrderEntity> orderTotalLookup;

	/**
	 * Constructor.
	 * @param orderTotalLookup totals look up.
	 */
	@Inject
	OrderTotalsResourceOperatorImpl(
			@Named("orderTotalLookup")
			final TotalLookup<OrderEntity> orderTotalLookup) {

		this.orderTotalLookup = orderTotalLookup;
	}

	/**
	 * Handles the READ operations for totals on other resources.
	 *
	 * @param orderEntityResourceState the order en
	 * @param operation the resource operation
	 * @return the result
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@AnyResourceUri
			final ResourceState<OrderEntity> orderEntityResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<TotalEntity>> result = orderTotalLookup.getTotal(orderEntityResourceState);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}
