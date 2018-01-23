/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.value.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.shipments.lineitems.option.ShipmentLineItemOptionLookup;
import com.elasticpath.rest.resource.shipments.lineitems.option.value.LineItemOptionValues;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes the resource operation on shipment line item option value.
 */
@Singleton
@Named("shipmentLineItemOptionValueResourceOperator")
@Path
public final class ShipmentLineItemOptionValueResourceOperatorImpl implements ResourceOperator {

	private final ShipmentLineItemOptionLookup shipmentLineItemOptionsLookup;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemOptionsLookup the ShipmentLineItemOptionLookup
	 */
	@Inject
	ShipmentLineItemOptionValueResourceOperatorImpl(
			@Named("shipmentLineItemOptionsLookup")
			final ShipmentLineItemOptionLookup shipmentLineItemOptionsLookup) {
		this.shipmentLineItemOptionsLookup = shipmentLineItemOptionsLookup;
	}

	/**
	 * Handles the READ shipment line item option value operation.
	 *
	 * @param shipmentLineItemOption the shipment line item option representation to be used in the read.
	 * @param lineItemOptionValueId the shipment line item option value ID
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path({ AnyResourceUri.PATH_PART, LineItemOptionValues.PATH_PART, ResourceId.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadShipmentLineItemOptionValue(
			@AnyResourceUri
			final ResourceState<ShipmentLineItemOptionEntity> shipmentLineItemOption,
			@ResourceId
			final String lineItemOptionValueId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<ShipmentLineItemOptionValueEntity>> result = shipmentLineItemOptionsLookup
				.findOptionValues(shipmentLineItemOption, Base32Util.decode(lineItemOptionValueId));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}
