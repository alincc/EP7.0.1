/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.taxes.TaxesLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes resource operations on shipment taxes.
 */
@Singleton
@Named("shipmentTaxesResourceOperator")
@Path(ResourceName.PATH_PART)
public class ShipmentTaxesResourceOperatorImpl implements ResourceOperator {

	private final TaxesLookup<ShipmentEntity> shipmentTaxesLookup;

	/**
	 * Constructor.
	 *
	 * @param shipmentTaxesLookup a {@link TaxesLookup}&lt;{@link ShipmentEntity}>
	 */
	@Inject
	public ShipmentTaxesResourceOperatorImpl(
			@Named("shipmentTaxesLookup")
			final TaxesLookup<ShipmentEntity> shipmentTaxesLookup) {
		this.shipmentTaxesLookup = shipmentTaxesLookup;
	}

	/**
	 * Handle a READ operation for a shipment's taxes.
	 *
	 * @param shipment the shipment for which to read taxes
	 * @param operation the resource operation
	 * @return the result of the read operation
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@AnyResourceUri
			final ResourceState<ShipmentEntity> shipment,
			final ResourceOperation operation) {
		ExecutionResult<ResourceState<TaxesEntity>> taxesResult = shipmentTaxesLookup.getTaxes(shipment);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(taxesResult, operation);
	}
}
