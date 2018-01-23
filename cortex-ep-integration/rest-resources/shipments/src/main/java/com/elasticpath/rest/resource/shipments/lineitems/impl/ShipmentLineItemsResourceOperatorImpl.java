/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.shipments.LineItems;
import com.elasticpath.rest.resource.shipments.lineitems.ShipmentLineItemsLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes the resource operations on shipment line items.
 */
@Singleton
@Named("shipmentLineItemsResourceOperator")
@Path
public final class ShipmentLineItemsResourceOperatorImpl implements ResourceOperator {

	private final ShipmentLineItemsLookup shipmentLineItemsLookup;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemsLookup shipment line item lookup
	 */
	@Inject
	ShipmentLineItemsResourceOperatorImpl(
			@Named("shipmentLineItemsLookup")
			final ShipmentLineItemsLookup shipmentLineItemsLookup) {
		this.shipmentLineItemsLookup = shipmentLineItemsLookup;
	}

	/**
	 * Handles the READ all shipment line items operation.
	 *
	 * @param shipmentRepresentation the shipment representation to be used in the read.
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path({ AnyResourceUri.PATH_PART, LineItems.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadShipmentLineItems(
			@AnyResourceUri
			final ResourceState<ShipmentEntity> shipmentRepresentation,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> result =
				shipmentLineItemsLookup.findAll(shipmentRepresentation);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);

	}

	/**
	 * Handles the READ shipment line item operation.
	 *
	 * @param shipmentRepresentation the shipment representation to be used in the read.
	 * @param lineItemId the shipment line item ID
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path({ AnyResourceUri.PATH_PART, LineItems.PATH_PART, ResourceId.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadShipmentLineItem(
			@AnyResourceUri
			final ResourceState<ShipmentEntity> shipmentRepresentation,
			@ResourceId
			final String lineItemId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<ShipmentLineItemEntity>> result =
				shipmentLineItemsLookup.find(shipmentRepresentation, Base32Util.decode(lineItemId));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}
