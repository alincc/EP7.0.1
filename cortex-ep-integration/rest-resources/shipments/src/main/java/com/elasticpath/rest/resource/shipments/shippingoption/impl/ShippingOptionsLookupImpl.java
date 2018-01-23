/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.shipments.shippingoption.ShippingOptionsLookup;
import com.elasticpath.rest.resource.shipments.shippingoption.integration.ShippingOptionLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Default implementation of {@link ShippingOptionsLookupImpl}.
 */
@Named("shippingOptionsLookup")
public final class ShippingOptionsLookupImpl implements ShippingOptionsLookup {

	private final ShippingOptionLookupStrategy shippingOptionLookupStrategy;
	private final TransformRfoToResourceState<ShippingOptionEntity, ShippingOptionEntity, ShipmentEntity> shippingOptionTransformer;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionLookupStrategy a {@link com.elasticpath.rest.resource.shipments.shippingoption.integration.ShippingOptionLookupStrategy}
	 * @param shippingOptionTransformer    a {@link com.elasticpath.rest.resource.shipments.shippingoption.transform.ShippingOptionTransformer}
	 */
	@Inject
	public ShippingOptionsLookupImpl(
			@Named("shippingOptionLookupStrategy")
			final ShippingOptionLookupStrategy shippingOptionLookupStrategy,
			@Named("shippingOptionTransformer")
			final TransformRfoToResourceState<ShippingOptionEntity, ShippingOptionEntity, ShipmentEntity> shippingOptionTransformer) {
		this.shippingOptionLookupStrategy = shippingOptionLookupStrategy;
		this.shippingOptionTransformer = shippingOptionTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<ShippingOptionEntity>> getShippingOption(
			final ResourceState<ShipmentEntity> shipment) {

		ShipmentEntity shipmentEntity = shipment.getEntity();
		ShippingOptionEntity entity =
				Assign.ifSuccessful(shippingOptionLookupStrategy
						.getShippingOption(shipment.getScope(), shipmentEntity.getPurchaseId(), shipmentEntity.getShipmentId()));

		ResourceState<ShippingOptionEntity> shippingOption =
				shippingOptionTransformer.transform(entity, shipment);

		return ExecutionResultFactory.createReadOK(shippingOption);
	}


}
