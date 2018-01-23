/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.taxes.TaxesLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes resource operations on shipment line item taxes.
 */
@Singleton
@Named("shipmentLineItemTaxesResourceOperator")
@Path(ResourceName.PATH_PART)
public class ShipmentLineItemTaxesResourceOperatorImpl implements ResourceOperator {

	private final TaxesLookup<ShipmentLineItemEntity> taxesLookup;

	/**
	 * Constructor.
	 *
	 * @param taxesLookup a {@link TaxesLookup} for {@link ShipmentLineItemEntity}.
	 */
	@Inject
	public ShipmentLineItemTaxesResourceOperatorImpl(
			@Named("shipmentLineItemTaxesLookup")
			final TaxesLookup<ShipmentLineItemEntity> taxesLookup) {
		this.taxesLookup = taxesLookup;
	}

	/**
	 * Handle a READ operation for a shipmentLineItem's taxes.
	 *
	 * @param shipmentLineItem the shipmentLineItem for which to read taxes
	 * @param operation the resource operation
	 * @return the result of the read operation
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@AnyResourceUri
			final ResourceState<ShipmentLineItemEntity> shipmentLineItem,
			final ResourceOperation operation) {
		ExecutionResult<ResourceState<TaxesEntity>> taxesResult = taxesLookup.getTaxes(shipmentLineItem);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(taxesResult, operation);
	}
}
