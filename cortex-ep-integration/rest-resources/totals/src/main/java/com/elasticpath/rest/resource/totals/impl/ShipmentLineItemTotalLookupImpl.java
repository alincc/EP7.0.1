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
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.integration.ShipmentLineItemTotalLookupStrategy;
import com.elasticpath.rest.resource.totals.integration.transform.TotalTransformer;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implementation of {@link TotalLookup}<{@link ShipmentLineItemEntity}>.
 */
@Singleton
@Named("shipmentLineItemTotalLookup")
public final class ShipmentLineItemTotalLookupImpl implements TotalLookup<ShipmentLineItemEntity> {

	private final ShipmentLineItemTotalLookupStrategy shipmentLineItemTotalLookupStrategy;

	private final TotalTransformer totalTransformer;

	/**
	 * Constructor.
	 *
	 * @param totalTransformer                    the total transformer
	 * @param shipmentLineItemTotalLookupStrategy the ShipmentLineItemTotalLookupStrategy
	 */
	@Inject
	ShipmentLineItemTotalLookupImpl(
			@Named("totalTransformer")
			final TotalTransformer totalTransformer,
			@Named("shipmentLineItemTotalLookupStrategy")
			final ShipmentLineItemTotalLookupStrategy shipmentLineItemTotalLookupStrategy) {
		this.totalTransformer = totalTransformer;
		this.shipmentLineItemTotalLookupStrategy = shipmentLineItemTotalLookupStrategy;
	}

	@Override
	public ExecutionResult<ResourceState<TotalEntity>> getTotal(final ResourceState<ShipmentLineItemEntity> representation) {

		String scope = representation.getScope();
		final ShipmentLineItemEntity shipmentLineItemEntity = representation.getEntity();
		String purchaseId = shipmentLineItemEntity.getPurchaseId();
		String shipmentId = shipmentLineItemEntity.getShipmentId();
		String lineItemId = shipmentLineItemEntity.getLineItemId();

		TotalEntity totalDto = Assign.ifSuccessful(shipmentLineItemTotalLookupStrategy.getTotal(scope, purchaseId, shipmentId, lineItemId));
		ResourceState<TotalEntity> total = totalTransformer.transform(totalDto, representation, TotalResourceRels.LINE_ITEM_REL);

		return ExecutionResultFactory.createReadOK(total);
	}

}
