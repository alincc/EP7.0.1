/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.integration.epcommerce.impl;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.prices.integration.ShipmentLineItemPriceLookupStrategy;

/**
 * Look up shipment line item prices.
 */
@Singleton
@Named("shipmentLineItemPriceLookupStrategy")
public class ShipmentLineItemPriceLookupStrategyImpl implements ShipmentLineItemPriceLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final ShipmentRepository shipmentRepository;
	private final MoneyTransformer moneyTransformer;
	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Constructor.
	 *
	 * @param shipmentRepository the shipment repository.
	 * @param moneyTransformer the line item price transformer.
	 * @param pricingSnapshotRepository the pricing snapshot repository
	 * @param resourceOperationContext the resource operation context
	 */
	@Inject
	public ShipmentLineItemPriceLookupStrategyImpl(
			@Named("shipmentRepository")
			final ShipmentRepository shipmentRepository,
			@Named("moneyTransformer")
			final MoneyTransformer moneyTransformer,
			@Named("pricingSnapshotRepository")
			final PricingSnapshotRepository pricingSnapshotRepository,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {

		this.shipmentRepository = shipmentRepository;
		this.moneyTransformer = moneyTransformer;
		this.resourceOperationContext = resourceOperationContext;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Override
	public ExecutionResult<ShipmentLineItemPriceEntity> getPrice(
			final String scope, final String purchaseGuid, final String shipmentGuid, final String lineItemGuid) {

		OrderSku orderSku
				= Assign.ifSuccessful(shipmentRepository.getOrderSku(scope, purchaseGuid, shipmentGuid, lineItemGuid, null));
		final ShoppingItemPricingSnapshot pricingSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getPricingSnapshotForOrderSku(orderSku));
		Money purchasePrice = pricingSnapshot.getPriceCalc().forUnitPrice().withCartDiscounts().getMoney();
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		ShipmentLineItemPriceEntity shipmentLineItemEntity = createShipmentLineItemPriceEntity(purchasePrice, locale);
		return ExecutionResultFactory.createReadOK(shipmentLineItemEntity);

	}

	private ShipmentLineItemPriceEntity createShipmentLineItemPriceEntity(final Money purchasePrice, final Locale locale) {
		CostEntity purchaseCostEntity = moneyTransformer.transformToEntity(purchasePrice, locale);
		ShipmentLineItemPriceEntity.Builder builder = ShipmentLineItemPriceEntity.builder();
		builder.addingPurchasePrice(purchaseCostEntity);
		return builder.build();
	}
}
