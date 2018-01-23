/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.order.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.taxes.TaxesLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes resource operations on order taxes.
 */
@Singleton
@Named("orderTaxesResourceOperator")
@Path(ResourceName.PATH_PART)
public class OrderTaxesResourceOperatorImpl implements ResourceOperator {

	private final TaxesLookup<OrderEntity> orderTaxesLookup;

	/**
	 * Constructor.
	 *
	 * @param orderTaxesLookup a {@link TaxesLookup}&lt;{@link OrderEntity}>
	 */
	@Inject
	public OrderTaxesResourceOperatorImpl(
			@Named("orderTaxesLookup")
			final TaxesLookup<OrderEntity> orderTaxesLookup) {
		this.orderTaxesLookup = orderTaxesLookup;
	}

	/**
	 * Handle a READ operation for an order's taxes.
	 *
	 * @param order the order for which to read taxes
	 * @param operation the resource operation
	 * @return the result of the read operation
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@AnyResourceUri
			final ResourceState<OrderEntity> order,
			final ResourceOperation operation) {
		ExecutionResult<ResourceState<TaxesEntity>> taxesResult = orderTaxesLookup.getTaxes(order);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(taxesResult, operation);
	}

}
