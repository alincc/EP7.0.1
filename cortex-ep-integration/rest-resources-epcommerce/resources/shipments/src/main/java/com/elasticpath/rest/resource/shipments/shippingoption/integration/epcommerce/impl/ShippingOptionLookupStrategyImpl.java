/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.integration.epcommerce.impl;

import java.util.Collections;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.ShippingCostCalculator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShipmentShippingServiceLevelRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.shipments.shippingoption.integration.ShippingOptionLookupStrategy;

/**
 * Default implementation of {@link ShippingOptionLookupStrategy}.
 */
@Named("shippingOptionLookupStrategy")
public class ShippingOptionLookupStrategyImpl implements ShippingOptionLookupStrategy {

	private final ShippingCostCalculator shippingCostCalculator;
	private final MoneyTransformer moneyTransformer;
	private final ShipmentRepository shipmentRepository;
	private final ShipmentShippingServiceLevelRepository shipmentShippingServiceLevelRepository;
	private final ResourceOperationContext resourceOperationContext;

	/**
	 * Constructor.
	 *
	 * @param shippingCostCalculator                 a {@link ShippingCostCalculator}
	 * @param shipmentRepository                     shipping repository
	 * @param shipmentShippingServiceLevelRepository shipmentShippingServiceLevelRepository
	 * @param resourceOperationContext               resourceOperationContext
	 * @param moneyTransformer                       a {@link MoneyTransformer}
	 */
	@Inject
	public ShippingOptionLookupStrategyImpl(
			@Named("shippingCostCalculator")
			final ShippingCostCalculator shippingCostCalculator,
			@Named("shipmentRepository")
			final ShipmentRepository shipmentRepository,
			@Named("shipmentShippingServiceLevelRepository")
			final ShipmentShippingServiceLevelRepository shipmentShippingServiceLevelRepository,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("moneyTransformer")
			final MoneyTransformer moneyTransformer) {
		this.shippingCostCalculator = shippingCostCalculator;
		this.moneyTransformer = moneyTransformer;
		this.shipmentRepository = shipmentRepository;
		this.shipmentShippingServiceLevelRepository = shipmentShippingServiceLevelRepository;
		this.resourceOperationContext = resourceOperationContext;
	}

	@Override
	public ExecutionResult<ShippingOptionEntity> getShippingOption(
			final String scope,
			final String decodedPurchaseId,
			final String decodedShipmentId) {

		ExecutionResult<Money> shippingCostMoneyResult = shippingCostCalculator.calculate(decodedPurchaseId, decodedShipmentId);
		Money shippingCostMoney = Assign.ifSuccessful(shippingCostMoneyResult);

		CostEntity costEntity = moneyTransformer.transformToEntity(shippingCostMoney);

		PhysicalOrderShipment orderShipment = Assign.ifSuccessful(shipmentRepository.find(decodedPurchaseId, decodedShipmentId));
		ShippingServiceLevel serviceLevel = Assign.ifSuccessful(
				shipmentShippingServiceLevelRepository.findByGuid(orderShipment.getShippingServiceLevelGuid()));

		ShippingOptionEntity shippingOptionEntity = ShippingOptionEntity.builder()
				.withCost(Collections.singleton(costEntity))
				.withCarrier(serviceLevel.getCarrier())
				.withName(serviceLevel.getCode())
				.withDisplayName(serviceLevel.getDisplayName(getLocaleForCurrentSubject(), true))
				.build();

		return ExecutionResultFactory.createReadOK(shippingOptionEntity);
	}

	private Locale getLocaleForCurrentSubject() {
		Subject subject = resourceOperationContext.getSubject();
		return SubjectUtil.getLocale(subject);
	}
}
