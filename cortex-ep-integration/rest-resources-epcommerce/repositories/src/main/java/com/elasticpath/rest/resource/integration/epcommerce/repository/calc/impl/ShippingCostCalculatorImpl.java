/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.ShippingCostCalculator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Default implementation of {@link ShippingCostCalculator}.
 */
@Singleton
@Named("shippingCostCalculator")
public class ShippingCostCalculatorImpl implements ShippingCostCalculator {

	private final ShipmentRepository shipmentRepository;

	/**
	 * Constructor.
	 * 
	 * @param shipmentRepository a {@link ShipmentRepository}
	 */
	@Inject
	public ShippingCostCalculatorImpl(
			@Named("shipmentRepository")
			final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}

	@Override
	public ExecutionResult<Money> calculate(final String orderGuid, final String shipmentGuid) {
		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				ExecutionResult<PhysicalOrderShipment> shipmentResult = shipmentRepository.find(orderGuid, shipmentGuid);
				PhysicalOrderShipment physicalShipment = Assign.ifSuccessful(shipmentResult);

				return ExecutionResultFactory.createReadOK(physicalShipment.getShippingCostMoney());
			}
		}.execute();
	}

}
