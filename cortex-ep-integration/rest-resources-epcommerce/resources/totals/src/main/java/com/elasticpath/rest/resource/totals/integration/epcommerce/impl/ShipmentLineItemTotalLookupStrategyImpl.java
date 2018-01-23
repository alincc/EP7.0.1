/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration.epcommerce.impl;

import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.totals.integration.ShipmentLineItemTotalLookupStrategy;
import com.elasticpath.rest.resource.totals.integration.epcommerce.transform.TotalMoneyTransformer;

/**
 * Implementation of {@link ShipmentLineItemTotalLookupStrategy}.
 */
@Singleton
@Named("shipmentLineItemTotalLookupStrategy")
public class ShipmentLineItemTotalLookupStrategyImpl implements ShipmentLineItemTotalLookupStrategy {

	/**
	 * Error message when line item not found.
	 */
	public static final String LINE_ITEM_NOT_FOUND = "Line item not found";

	private final ShipmentRepository shipmentRepository;
	private final TotalMoneyTransformer totalMoneyTransformer;
	private final PricingSnapshotRepository pricingSnapshotRepository;
	private final ResourceOperationContext resourceOperationContext;


	/**
	 * Constructor.
	 *
	 * @param shipmentRepository       the ShipmentRepository
	 * @param totalMoneyTransformer    the TotalMoneyTransformer
	 * @param pricingSnapshotRepository   the pricing snapshot repository
	 * @param resourceOperationContext the resource operation context
	 */
	@Inject
	ShipmentLineItemTotalLookupStrategyImpl(
			@Named("shipmentRepository")
			final ShipmentRepository shipmentRepository,
			@Named("totalMoneyTransformer")
			final TotalMoneyTransformer totalMoneyTransformer,
			@Named("pricingSnapshotRepository")
			final PricingSnapshotRepository pricingSnapshotRepository,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {
		this.shipmentRepository = shipmentRepository;
		this.totalMoneyTransformer = totalMoneyTransformer;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
		this.resourceOperationContext = resourceOperationContext;
	}

	@Override
	public ExecutionResult<TotalEntity> getTotal(final String storeCode, final String purchaseId, final String shipmentId, final String lineItemId) {

		OrderShipment orderShipment = Assign.ifSuccessful(shipmentRepository.find(purchaseId, shipmentId));
		OrderSku orderSku = Assign.ifNotNull(getOrderSkuByGuid(orderShipment.getShipmentOrderSkus(), lineItemId),
			OnFailure.returnNotFound(LINE_ITEM_NOT_FOUND));

		final ShoppingItemTaxSnapshot taxSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getTaxSnapshotForOrderSku(orderSku));

		Money amountWithTaxes = taxSnapshot.getTaxPriceCalculator()
				.withCartDiscounts()
				.getMoney();
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		TotalEntity totalDto = totalMoneyTransformer.transformToEntity(amountWithTaxes, locale);

		return ExecutionResultFactory.createReadOK(totalDto);
	}

	private OrderSku getOrderSkuByGuid(final Set<OrderSku> orderSkus, final String skuGuid) {

		if (skuGuid != null) {
			for (OrderSku orderSku : orderSkus) {
				if (skuGuid.equals(orderSku.getGuid())) {
					return orderSku;
				}
			}
		}

		return null;
	}

}
