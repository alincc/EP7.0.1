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
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
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
@Named("shipmentTotalsResourceOperator")
@Path(ResourceName.PATH_PART)
public final class ShipmentTotalsResourceOperatorImpl implements ResourceOperator {

	private final TotalLookup<ShipmentEntity> shipmentTotalLookup;

	/**
	 * Constructor.
	 * @param shipmentTotalLookup totals look up.
	 */
	@Inject
	ShipmentTotalsResourceOperatorImpl(
			@Named("shipmentTotalLookup")
			final TotalLookup<ShipmentEntity> shipmentTotalLookup) {

		this.shipmentTotalLookup = shipmentTotalLookup;
	}

	/**
	 * Handles the READ operations for totals on other resources.
	 *
	 * @param shipmentEntityResourceState the shipment entity resource state
	 * @param operation the resource operation
	 * @return the result
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@AnyResourceUri
			final ResourceState<ShipmentEntity> shipmentEntityResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<TotalEntity>> total = shipmentTotalLookup.getTotal(shipmentEntityResourceState);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(total, operation);
	}
}
