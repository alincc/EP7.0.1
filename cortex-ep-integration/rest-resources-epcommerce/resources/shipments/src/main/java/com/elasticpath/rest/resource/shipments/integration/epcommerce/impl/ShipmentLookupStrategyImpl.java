/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.StatusEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.shipments.integration.ShipmentLookupStrategy;

/**
 * Service that provides lookup of shipment data from external systems.
 */
@Singleton
@Named("shipmentLookupStrategy")
public class ShipmentLookupStrategyImpl implements ShipmentLookupStrategy {

	private final ShipmentRepository shipmentRepository;

	/**
	 * Constructor.
	 *
	 * @param shipmentRepository the shipment repository
	 */
	@Inject
	public ShipmentLookupStrategyImpl(
			@Named("shipmentRepository")
			final ShipmentRepository shipmentRepository) {

		this.shipmentRepository = shipmentRepository;
	}

	@Override
	public ExecutionResult<ShipmentEntity> find(final ShipmentEntity shipmentEntity) {

		PhysicalOrderShipment orderShipment = Assign.ifSuccessful(shipmentRepository.find(shipmentEntity.getPurchaseId(),
																								shipmentEntity.getShipmentId()));

		OrderShipmentStatus shipmentStatus = orderShipment.getShipmentStatus();
		StatusEntity resultStatusEntity = StatusEntity.builder()
				.withCode(shipmentStatus.getName())
				.build();
		ShipmentEntity resultShipmentEntity = ShipmentEntity.builder()
				.withStatus(resultStatusEntity)
				.withShipmentId(orderShipment.getShipmentNumber())
				.withPurchaseId(shipmentEntity.getPurchaseId())
				.build();

		return ExecutionResultFactory.createReadOK(resultShipmentEntity);
	}

	@Override
	public ExecutionResult<Collection<String>> findShipmentIds(final String storeCode, final String purchaseId) {

		Collection<PhysicalOrderShipment> orderShipments = Assign.ifSuccessful(shipmentRepository.findAll(storeCode, purchaseId));

		return ExecutionResultFactory.createReadOK(getOrderShipmentIds(orderShipments));
	}

	private Collection<String> getOrderShipmentIds(final Collection<PhysicalOrderShipment> orderShipments) {
		Collection<String> shipmentIds = new ArrayList<>(orderShipments.size());
		for (PhysicalOrderShipment orderShipment : orderShipments) {
			shipmentIds.add(orderShipment.getShipmentNumber());
		}
		return shipmentIds;
	}
}
