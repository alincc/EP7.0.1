/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.shipmentdetails.integration.ShipmentDetailsLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.integration.dto.ShipmentDetailsDto;
import com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.ShipmentDetailsIntegrationProperties;

/**
 * Ep Commerce Engine implementation of {@link ShipmentDetailsLookupStrategy}.
 */
@Singleton
@Named("shipmentDetailsLookupStrategy")
public class ShipmentDetailsLookupStrategyImpl implements ShipmentDetailsLookupStrategy {

	private static final String COULD_NOT_FIND_SHIPMENT = "Could not find shipment";

	private final CartOrderRepository cartOrderRepository;
	private final ShoppingCartRepository shoppingCartRepository;

	/**
	 * Instantiates a new shipment details lookup strategy impl.
	 *
	 * @param shoppingCartRepository the shopping cart repository
	 * @param cartOrderRepository    the cart order repository
	 */
	@Inject
	public ShipmentDetailsLookupStrategyImpl(
			@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository,
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository) {

		this.shoppingCartRepository = shoppingCartRepository;
		this.cartOrderRepository = cartOrderRepository;
	}

	@Override
	public ExecutionResult<String> getShipmentDetailsIdForOrderAndDelivery(final String cartOrderGuid, final String deliveryId) {

		assert cartOrderGuid != null && deliveryId != null : "cartOrderGuid and deliveryId must not be null";
		Map<String, String> shipmentFieldValues = new TreeMap<>();
		shipmentFieldValues.put(ShipmentDetailsIntegrationProperties.ORDER_ID_KEY, cartOrderGuid);
		shipmentFieldValues.put(ShipmentDetailsIntegrationProperties.DELIVERY_ID_KEY, deliveryId);
		String shipmentDetailsId = Assign.ifNotNull(CompositeIdUtil.encodeCompositeId(shipmentFieldValues),
				OnFailure.returnServerError("could not encode shipmentDetailsId"));
		return ExecutionResultFactory.createReadOK(shipmentDetailsId);
	}

	@Override
	public ExecutionResult<Collection<String>> getShipmentDetailsIds(final String storeCode, final String customerGuid) {

		Collection<String> cartOrderGuids =
				Assign.ifSuccessful(cartOrderRepository.findCartOrderGuidsByCustomer(storeCode, customerGuid));
		Collection<String> shipmentDetailsIds = new ArrayList<>();
		for (String cartOrderGuid : cartOrderGuids) {
			try {
				Collection<String> shipmentDetailsIdsResult = Assign.ifSuccessful(getShipmentDetailsIdsForOrder(storeCode, cartOrderGuid));
				shipmentDetailsIds.addAll(shipmentDetailsIdsResult);
			} catch (BrokenChainException e) {
				//continue, nothing to add
			}
		}
		return ExecutionResultFactory.createReadOK(shipmentDetailsIds);
	}

	@Override
	public ExecutionResult<ShipmentDetailsDto> getShipmentDetail(final String storeCode, final String shipmentDetailId) {

		ShipmentDetailsDto shipmentDetailsDto = ResourceTypeFactory.createResourceEntity(ShipmentDetailsDto.class);
		//empty maps are OK
		Map<String, String> shipmentFieldValueMap = Assign.ifNotNull(CompositeIdUtil.decodeCompositeId(shipmentDetailId),
				OnFailure.returnServerError("could not decode shipmentDetailsId"));
		String cartOrderGuid = shipmentFieldValueMap.get(ShipmentDetailsIntegrationProperties.ORDER_ID_KEY);
		String deliveryId = shipmentFieldValueMap.get(ShipmentDetailsIntegrationProperties.DELIVERY_ID_KEY);

		Assign.ifSuccessful(validateOrderDeliveryIsShippable(storeCode, cartOrderGuid));
		shipmentDetailsDto.setOrderCorrelationId(cartOrderGuid).setDeliveryCorrelationId(deliveryId);

		return ExecutionResultFactory.createReadOK(shipmentDetailsDto);
	}

	@Override
	public ExecutionResult<Collection<String>> getShipmentDetailsIdsForOrder(final String scope, final String cartOrderGuid) {

		Ensure.successful(validateOrderDeliveryIsShippable(scope, cartOrderGuid));

		String shipmentDetailsId = Assign.ifSuccessful(getShipmentDetailsIdForOrderAndDelivery(cartOrderGuid,
				ShipmentDetailsIntegrationProperties.PHYSICAL_SHIPMENT_IDENTIFIER));
		return ExecutionResultFactory.<Collection<String>>createReadOK(Collections.singletonList(shipmentDetailsId));
	}

	private ExecutionResult<Boolean> validateOrderDeliveryIsShippable(final String storeCode, final String cartOrderGuid) {

		final String shoppingCartGuid = Assign.ifNotNull(
				cartOrderRepository.getShoppingCartGuid(storeCode, cartOrderGuid), OnFailure.returnNotFound(
						String.format("Shopping cart GUID doesn't exist for store code {} and cart order GUID {}", storeCode, cartOrderGuid)));

		ShoppingCart shoppingCart = Assign.ifSuccessful(shoppingCartRepository.getShoppingCart(shoppingCartGuid));

		Ensure.isTrue(containsPhysicalShipment(shoppingCart),
							OnFailure.returnNotFound(COULD_NOT_FIND_SHIPMENT));

		return ExecutionResultFactory.createReadOK(true);
	}

	private boolean containsPhysicalShipment(final ShoppingCart shoppingCart) {
		Collection<ShipmentType> shipmentTypes = shoppingCart.getShipmentTypes();
		for (ShipmentType shipmentType : shipmentTypes) {
			if (shipmentType.equals(ShipmentType.PHYSICAL)) {
				return true;
			}
		}
		return false;
	}
}
