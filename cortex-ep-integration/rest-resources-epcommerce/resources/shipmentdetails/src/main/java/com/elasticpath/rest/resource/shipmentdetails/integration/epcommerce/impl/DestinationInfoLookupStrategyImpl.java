/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.integration.DestinationInfoLookupStrategy;

/**
 * Lookup strategy for destination info.
 */
@Singleton
@Named("destinationInfoLookupStrategy")
public class DestinationInfoLookupStrategyImpl implements DestinationInfoLookupStrategy {

	private final CartOrderRepository cartOrderRepository;

	/**
	 * Default constructor.
	 *
	 * @param cartOrderRepository the cart order repository
	 */
	@Inject
	public DestinationInfoLookupStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository) {

		this.cartOrderRepository = cartOrderRepository;
	}

	@Override
	public ExecutionResult<String> findSelectedAddressIdForShipment(
			final String storeCode, final String cartOrderGuid, final String shipmentDetailsId) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		Address shippingAddress = Assign.ifSuccessful(cartOrderRepository.getShippingAddress(cartOrder));
		return ExecutionResultFactory.createReadOK(shippingAddress.getGuid());
	}
}
