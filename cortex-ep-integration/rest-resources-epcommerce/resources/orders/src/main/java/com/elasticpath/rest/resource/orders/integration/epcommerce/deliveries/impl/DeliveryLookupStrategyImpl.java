/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.deliveries.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.orders.integration.deliveries.DeliveryLookupStrategy;
import com.elasticpath.rest.resource.orders.integration.epcommerce.deliveries.transform.DeliveryTransformer;
import com.elasticpath.rest.resource.orders.integration.epcommerce.deliveries.wrapper.DeliveryWrapper;

/**
 * Lookup strategy for order delivery.
 */
@Singleton
@Named("deliveryLookupStrategy")
public class DeliveryLookupStrategyImpl implements DeliveryLookupStrategy {

	private static final String SHIPMENT = "SHIPMENT";

	private final CartOrderRepository cartOrderRepository;
	private final ShoppingCartRepository shoppingCartRepository;
	private final DeliveryTransformer deliveryTransformer;

	/**
	 * Default Constructor.
	 *
	 * @param cartOrderRepository the cart order repository
	 * @param shoppingCartRepository the shopping cart repository
	 * @param deliveryTransformer the delivery transformer
	 */
	@Inject
	public DeliveryLookupStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository,
			@Named("deliveryTransformer")
			final DeliveryTransformer deliveryTransformer) {

		this.cartOrderRepository = cartOrderRepository;
		this.shoppingCartRepository = shoppingCartRepository;
		this.deliveryTransformer = deliveryTransformer;
	}


	@Override
	public ExecutionResult<DeliveryEntity> findByIdAndOrderId(final String storeCode, final String orderGuid, final String deliveryCode) {
		DeliveryWrapper deliveryWrapper = Assign.ifSuccessful(getDelivery(storeCode, orderGuid, deliveryCode));
		DeliveryEntity deliveryEntity = deliveryTransformer.transformToEntity(deliveryWrapper);
		return ExecutionResultFactory.createReadOK(deliveryEntity);
	}

	/**
	 * Helper method to get a specific delivery for an order.
	 *
	 * @param storeCode the store code
	 * @param orderGuid the order guid
	 * @param deliveryCode the delivery code
	 * @return the delivery
	 */
	ExecutionResult<DeliveryWrapper> getDelivery(final String storeCode, final String orderGuid, final String deliveryCode) {

		Collection<String> deliveries = Assign.ifSuccessful(getDeliveryIds(storeCode, orderGuid));
		Ensure.isTrue(deliveries.contains(deliveryCode),
				OnFailure.returnNotFound("Order delivery not found."));
		DeliveryWrapper deliveryWrapper = ResourceTypeFactory.createResourceEntity(DeliveryWrapper.class)
				.setDeliveryCode(deliveryCode)
				.setShipmentType(deliveryCode);
		return ExecutionResultFactory.createReadOK(deliveryWrapper);
	}

	@Override
	public ExecutionResult<Collection<String>> getDeliveryIds(final String storeCode, final String orderGuid) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, orderGuid));
		String cartGuid = cartOrder.getShoppingCartGuid();
		return getShoppingCartDeliveryTypes(cartGuid);
	}

	private ExecutionResult<Collection<String>> getShoppingCartDeliveryTypes(final String cartGuid) {

		ShoppingCart shoppingCart = Assign.ifSuccessful(shoppingCartRepository.getShoppingCart(cartGuid));
		Collection<ShipmentType> shipmentTypes = shoppingCart.getShipmentTypes();
		//Only want to collect shipping types of SHIPMENT
		Collection<String> deliveryTypes = new ArrayList<>();
		for (ShipmentType shipmentType : shipmentTypes) {
			if (shipmentType.equals(ShipmentType.PHYSICAL)) {
				deliveryTypes.add(SHIPMENT);
				break;
			}
		}
		return ExecutionResultFactory.createReadOK(deliveryTypes);
	}
}
