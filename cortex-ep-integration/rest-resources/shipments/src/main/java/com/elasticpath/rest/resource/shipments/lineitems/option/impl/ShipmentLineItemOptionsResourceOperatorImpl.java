/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.shipments.lineitems.option.LineItemOptions;
import com.elasticpath.rest.resource.shipments.lineitems.option.ShipmentLineItemOptionLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes the resource operation on shipment line item options.
 */
@Singleton
@Named("shipmentLineItemOptionsResourceOperator")
@Path
public final class ShipmentLineItemOptionsResourceOperatorImpl implements ResourceOperator {

	private final ShipmentLineItemOptionLookup shipmentLineItemOptionsLookup;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemOptionsLookup the ShipmentLineItemOptionLookup
	 */
	@Inject
	ShipmentLineItemOptionsResourceOperatorImpl(
			@Named("shipmentLineItemOptionsLookup")
			final ShipmentLineItemOptionLookup shipmentLineItemOptionsLookup) {

		this.shipmentLineItemOptionsLookup = shipmentLineItemOptionsLookup;

	}

	/**
	 * Handles the READ all shipment line item options operation.
	 *
	 * @param shipmentLineItem the shipment line item representation to be used in the read.
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path({ AnyResourceUri.PATH_PART, LineItemOptions.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadShipmentLineItemOptions(
			@AnyResourceUri
			final ResourceState<ShipmentLineItemEntity> shipmentLineItem,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> result = shipmentLineItemOptionsLookup.findAll(shipmentLineItem);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);

	}

	/**
	 * Handles the READ shipment line item operations.
	 *
	 * @param shipmentLineItem the shipment line item representation to be used in the read.
	 * @param lineItemOptionId the shipment line item option ID
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path({ AnyResourceUri.PATH_PART, LineItemOptions.PATH_PART, ResourceId.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadShipmentLineItemOption(
			@AnyResourceUri
			final ResourceState<ShipmentLineItemEntity> shipmentLineItem,
			@ResourceId
			final String lineItemOptionId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<ShipmentLineItemOptionEntity>> result =
				shipmentLineItemOptionsLookup.find(shipmentLineItem, Base32Util.decode(lineItemOptionId));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}
