/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetail;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.integration.ShipmentDetailsLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.integration.dto.ShipmentDetailsDto;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The shipment details lookup implementation.
 */
@Singleton
@Named("shipmentDetailsLookup")
public final class ShipmentDetailsLookupImpl implements ShipmentDetailsLookup {

	private final ShipmentDetailsLookupStrategy shipmentDetailsLookupStrategy;


	/**
	 * Constructor.
	 *
	 * @param shipmentDetailsLookupStrategy the shipment details lookup strategy
	 */
	@Inject
	ShipmentDetailsLookupImpl(
			@Named("shipmentDetailsLookupStrategy")
			final ShipmentDetailsLookupStrategy shipmentDetailsLookupStrategy) {

		this.shipmentDetailsLookupStrategy = shipmentDetailsLookupStrategy;
	}


	@Override
	public ExecutionResult<Collection<String>> findShipmentDetailsIds(final String scope, final String userId) {

		Collection<String> shipmentDetailsIds = Assign.ifSuccessful(
				shipmentDetailsLookupStrategy.getShipmentDetailsIds(scope, userId));
		return ExecutionResultFactory.createReadOK(shipmentDetailsIds);
	}

	@Override
	public ExecutionResult<Collection<String>> findShipmentDetailsIdsForOrder(final String scope, final String orderId) {

		String decodedOrderId = Base32Util.decode(orderId);
		Collection<String> shipmentDetailsIds =
				Assign.ifSuccessful(shipmentDetailsLookupStrategy.getShipmentDetailsIdsForOrder(scope, decodedOrderId));
		return ExecutionResultFactory.createReadOK(shipmentDetailsIds);
	}

	@Override
	public ExecutionResult<ShipmentDetail> getShipmentDetail(final String scope, final String shipmentDetailId) {

		ShipmentDetailsDto shipmentDetailsDto = Assign.ifSuccessful(shipmentDetailsLookupStrategy.getShipmentDetail(
				scope, shipmentDetailId));
		ShipmentDetail shipmentDetails = new ShipmentDetail()
				.setDeliveryId(Base32Util.encode(shipmentDetailsDto.getDeliveryCorrelationId()))
				.setOrderId(Base32Util.encode(shipmentDetailsDto.getOrderCorrelationId()));
		return ExecutionResultFactory.createReadOK(shipmentDetails);
	}

	@Override
	public ExecutionResult<String> findShipmentDetailsIdForDelivery(final ResourceState<DeliveryEntity> delivery) {

		String decodedOrderId = Base32Util.decode(delivery.getEntity().getOrderId());
		String decodedDeliveryId = Base32Util.decode(delivery.getEntity().getDeliveryId());
		String shipmentDetailsId = Assign.ifSuccessful(
				shipmentDetailsLookupStrategy.getShipmentDetailsIdForOrderAndDelivery(decodedOrderId, decodedDeliveryId));
		return ExecutionResultFactory.createReadOK(shipmentDetailsId);
	}
}
