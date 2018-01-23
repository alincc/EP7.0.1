/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;
import com.elasticpath.rest.resource.prices.ShipmentLineItemPriceLookup;
import com.elasticpath.rest.resource.prices.integration.ShipmentLineItemPriceLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Look up shipment line item prices.
 */
@Singleton
@Named("shipmentLineItemPriceLookup")
public final class ShipmentLineItemPriceLookupImpl implements ShipmentLineItemPriceLookup {

	private final ShipmentLineItemPriceLookupStrategy shipmentLineItemPriceLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemPriceLookupStrategy the price lookup strategy
	 */
	@Inject
	public ShipmentLineItemPriceLookupImpl(
			@Named("shipmentLineItemPriceLookupStrategy")
			final ShipmentLineItemPriceLookupStrategy shipmentLineItemPriceLookupStrategy) {
		this.shipmentLineItemPriceLookupStrategy = shipmentLineItemPriceLookupStrategy;
	}

	@Override
	public ExecutionResult<ShipmentLineItemPriceEntity> getPrice(final ResourceState<ShipmentLineItemEntity> lineItemRepresentation) {

		ShipmentLineItemEntity entity = lineItemRepresentation.getEntity();
		String decodedPurchaseId = entity.getPurchaseId();
		String decodedShipmentId = entity.getShipmentId();
		String decodedLineItemId = entity.getLineItemId();

		ShipmentLineItemPriceEntity shipmentLineItemPriceEntity
			= Assign.ifSuccessful(shipmentLineItemPriceLookupStrategy.getPrice(
				lineItemRepresentation.getScope(),
				decodedPurchaseId,
				decodedShipmentId,
				decodedLineItemId));
		return ExecutionResultFactory.createReadOK(shipmentLineItemPriceEntity);
	}
}