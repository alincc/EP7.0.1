/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.integration.ShipmentTotalLookupStrategy;
import com.elasticpath.rest.resource.totals.integration.transform.TotalTransformer;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implements {@link TotalLookup} for {@link ShipmentEntity} by connecting to core.
 */
@Singleton
@Named("shipmentTotalLookup")
public class ShipmentTotalLookupImpl implements TotalLookup<ShipmentEntity> {

	private final ShipmentTotalLookupStrategy totalLookupStrategy;

	private final TotalTransformer totalTransformer;

	/**
	 * Constructor.
	 *
	 * @param totalLookupStrategy the total lookup strategy
	 * @param totalTransformer the total transformer
	 */
	@Inject
	ShipmentTotalLookupImpl(@Named("shipmentTotalLookupStrategy")
	final ShipmentTotalLookupStrategy totalLookupStrategy, @Named("totalTransformer")
	final TotalTransformer totalTransformer) {

		this.totalLookupStrategy = totalLookupStrategy;
		this.totalTransformer = totalTransformer;
	}

	/**
	 * @param shipmentRepresentation the shipment representation
	 * @return the execution result
	 */
	@Override
	public ExecutionResult<ResourceState<TotalEntity>> getTotal(final ResourceState<ShipmentEntity> shipmentRepresentation) {
		final ShipmentEntity shipmentEntity = shipmentRepresentation.getEntity();
		String shipmentId = shipmentEntity.getShipmentId();
		String purchaseId = shipmentEntity.getPurchaseId();
		ExecutionResult<TotalEntity> shipmentTotalResult = totalLookupStrategy.getTotal(purchaseId, shipmentId);
		return processResult(shipmentRepresentation, TotalResourceRels.SHIPMENT_REL, shipmentTotalResult);
	}

	private ExecutionResult<ResourceState<TotalEntity>> processResult(final ResourceState otherResourceRepresentation, final String resourceRel,
			final ExecutionResult<TotalEntity> totalResult) {

		TotalEntity totalDto = Assign.ifSuccessful(totalResult);
		ResourceState<TotalEntity> total = totalTransformer.transform(totalDto, otherResourceRepresentation, resourceRel);

		return ExecutionResultFactory.createReadOK(total);
	}

}
