/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.ShipmentTotalsCalculator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Performs totals calculations for the Shipment.
 */
@Singleton
@Named("shipmentTotalsCalculator")
public class ShipmentTotalsCalculatorImpl implements ShipmentTotalsCalculator {

	private final ShipmentRepository shipmentRepository;

	/**
	 * Constructor.
	 *
	 * @param shipmentRepository the shipmentRepository
	 */
	@Inject
	ShipmentTotalsCalculatorImpl(@Named("shipmentRepository")
	final ShipmentRepository shipmentRepository) {

		this.shipmentRepository = shipmentRepository;
	}

	@Override
	public ExecutionResult<Money> calculateTotal(final String orderGuid, final String shipmentGuid) {
		OrderShipment orderShipment = Assign.ifSuccessful(getOrderShipment(orderGuid, shipmentGuid));
		return ExecutionResultFactory.createReadOK(orderShipment.getTotalMoney());
	}

	private ExecutionResult<OrderShipment> getOrderShipment(final String orderGuid, final String shipmentGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				OrderShipment orderShipment = Assign.ifSuccessful(shipmentRepository.find(orderGuid, shipmentGuid));
				return ExecutionResultFactory.createReadOK(orderShipment);
			}
		}.execute();
	}

}
