/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.resource.shipments.addresses.ShippingAddress;
import com.elasticpath.rest.resource.shipments.addresses.ShippingAddressLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The operator for the shipping address.
 */
@Singleton
@Named("shippingAddressResourceOperator")
@Path
public final class ShippingAddressResourceOperatorImpl implements ResourceOperator {

	private final ShippingAddressLookup shippingAddressLookup;

	/**
	 * Constructor.
	 *
	 * @param shippingAddressLookup the shippingAddressLookup
	 */
	@Inject
	ShippingAddressResourceOperatorImpl(
			@Named("shippingAddressLookup")
			final ShippingAddressLookup shippingAddressLookup) {
		this.shippingAddressLookup = shippingAddressLookup;
	}

	/**
	 * Process READ operation on a shipment's shipping address.
	 *
	 * @param shipment the parent resource.
	 * @param operation the Resource Operation.
	 * @return the Operation result with shipment address.
	 */
	@Path({ AnyResourceUri.PATH_PART, ShippingAddress.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processShippingAddressRead(
			@SingleResourceUri
			final ResourceState<ShipmentEntity> shipment,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<AddressEntity>> result = shippingAddressLookup.getShippingAddress(shipment);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}
