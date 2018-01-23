/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.integration.DestinationInfoWriterStrategy;
import com.elasticpath.rest.resource.shipmentdetails.integration.ShipmentDetailsLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.integration.dto.ShipmentDetailsDto;

/**
 * Writer for delivery address info.
 */
@Singleton
@Named("destinationInfoWriterStrategy")
public class DestinationInfoWriterStrategyImpl implements DestinationInfoWriterStrategy {

	private final CartOrderRepository cartOrderRepository;
	private final ShipmentDetailsLookupStrategy shipmentDetailsLookupStrategy;

	/**
	 * Default constructor.
	 *
	 * @param cartOrderRepository           the cart order repository
	 * @param shipmentDetailsLookupStrategy the shipment details lookup strategy
	 */
	@Inject
	public DestinationInfoWriterStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("shipmentDetailsLookupStrategy")
			final ShipmentDetailsLookupStrategy shipmentDetailsLookupStrategy) {

		this.cartOrderRepository = cartOrderRepository;
		this.shipmentDetailsLookupStrategy = shipmentDetailsLookupStrategy;
	}

	@Override
	public ExecutionResult<Void> updateShippingAddressForShipment(final String storeCode, final String shipmentDetailsId, final String addressGuid) {

		ShipmentDetailsDto shipmentDetail = Assign.ifSuccessful(shipmentDetailsLookupStrategy.getShipmentDetail(storeCode, shipmentDetailsId));
		String cartOrderGuid = shipmentDetail.getOrderCorrelationId();
		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		boolean isUpdatingExistingShippingAddress = cartOrder.getShippingAddressGuid() != null;

		Ensure.successful(cartOrderRepository.updateShippingAddressOnCartOrder(addressGuid, cartOrderGuid, storeCode));

		return ExecutionResultFactory.createCreateOKWithData(null, isUpdatingExistingShippingAddress);
	}

}
