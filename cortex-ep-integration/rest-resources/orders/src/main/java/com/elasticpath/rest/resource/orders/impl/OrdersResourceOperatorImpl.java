/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.orders.OrderLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Operator for the orders resource.
 */
@Singleton
@Named("ordersResourceOperator")
@Path(ResourceName.PATH_PART)
public final class OrdersResourceOperatorImpl implements ResourceOperator {

	private final OrderLookup orderLookup;

	/**
	 * Constructor.
	 *
	 * @param orderLookup the {@link com.elasticpath.rest.resource.orders.OrderLookup}
	 */
	@Inject
	OrdersResourceOperatorImpl(@Named("orderLookup")
								final OrderLookup orderLookup) {

		this.orderLookup = orderLookup;
	}

	/**
	 * Handles READ operations for orders.
	 *
	 * @param scope     the scope
	 * @param orderId   the order ID
	 * @param operation the Resource Operation
	 * @return the operation result with the order information
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadOrder(
			@Scope final String scope,
			@ResourceId final String orderId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<OrderEntity>> result = orderLookup.findOrderByOrderId(scope, orderId);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}
