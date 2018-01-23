/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.shipments.shippingoption.ShippingOption;
import com.elasticpath.rest.resource.shipments.shippingoption.ShippingOptionsLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes operations on shipping option resources.
 */
@Singleton
@Path
@Named("shippingOptionsResourceOperator")
public final class ShippingOptionsResourceOperatorImpl implements ResourceOperator {

	private final ShippingOptionsLookup shippingOptionsLookup;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionsLookup a {@link ShippingOptionsLookup}
	 */
	@Inject
	public ShippingOptionsResourceOperatorImpl(
			@Named("shippingOptionsLookup")
			final ShippingOptionsLookup shippingOptionsLookup) {
		this.shippingOptionsLookup = shippingOptionsLookup;
	}

	/**
	 * Handle a read operation for a shipment's shipping option.
	 *
	 * @param shipment the shipment's representation
	 * @param operation the operation
	 * @return result of shipping option lookup
	 */
	@Path({ AnyResourceUri.PATH_PART, ShippingOption.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@AnyResourceUri
			final ResourceState<ShipmentEntity> shipment,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<ShippingOptionEntity>> lookupResult = shippingOptionsLookup.getShippingOption(shipment);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(lookupResult, operation);
	}
}
