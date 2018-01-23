/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.shipments.lineitems.integration.ShipmentLineItemsLookupStrategy;

/**
 * Implementation of {@link ShipmentLineItemsLookupStrategy} for shipment line items.
 */
@Singleton
@Named("shipmentLineItemsLookupStrategy")
public class ShipmentLineItemsLookupStrategyImpl implements ShipmentLineItemsLookupStrategy {

	private final ShipmentRepository shipmentRepository;

	/**
	 * Constructor.
	 *
	 * @param shipmentRepository the shipment repository.
	 */
	@Inject
	public ShipmentLineItemsLookupStrategyImpl(
			@Named("shipmentRepository")
			final ShipmentRepository shipmentRepository) {

		this.shipmentRepository = shipmentRepository;
	}


	@Override
	public ExecutionResult<Collection<String>> findLineItemIds(
			final String scope, final ShipmentLineItemEntity shipmentLineItemEntity) {

		Collection<? extends ShoppingItem> shoppingItems = Assign.ifSuccessful(shipmentRepository.getOrderSkusForShipment(
				scope,
				shipmentLineItemEntity.getPurchaseId(),
				shipmentLineItemEntity.getShipmentId()));
		Collection<String> lineItemIDs = new ArrayList<>(shoppingItems.size());
		for (ShoppingItem shoppingItem : shoppingItems) {
			lineItemIDs.add(shoppingItem.getGuid());
		}
		return ExecutionResultFactory.createReadOK(lineItemIDs);
	}

	@Override
	public ExecutionResult<ShipmentLineItemEntity> find(
			final String scope, final ShipmentLineItemEntity entity) {

		OrderSku orderSku = Assign.ifSuccessful(getOrderSku(scope, entity));
		ShipmentLineItemEntity result = createShipmentLineItemEntity(entity, orderSku);

		return ExecutionResultFactory.createReadOK(result);
	}

	private ExecutionResult<OrderSku> getOrderSku(final String scope, final ShipmentLineItemEntity shipmentLineItemEntity) {
		return shipmentRepository.getOrderSku(
				scope,
				shipmentLineItemEntity.getPurchaseId(),
				shipmentLineItemEntity.getShipmentId(),
				shipmentLineItemEntity.getLineItemId(),
				shipmentLineItemEntity.getParentLineItemId());
	}

	private ShipmentLineItemEntity createShipmentLineItemEntity(
			final ShipmentLineItemEntity shipmentLineItemEntity,
			final OrderSku orderSku) {

		return ShipmentLineItemEntity.builderFrom(shipmentLineItemEntity)
				.withName(orderSku.getDisplayName())
				.withQuantity(orderSku.getQuantity())
				.build();
	}
}