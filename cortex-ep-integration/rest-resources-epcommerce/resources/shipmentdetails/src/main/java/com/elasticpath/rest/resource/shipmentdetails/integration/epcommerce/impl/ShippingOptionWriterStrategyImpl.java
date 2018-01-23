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
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.ShippingOptionWriterStrategy;

/**
 * Writer strategy for shipping options.
 */
@Singleton
@Named("shippingOptionWriterStrategy")
public class ShippingOptionWriterStrategyImpl implements ShippingOptionWriterStrategy {

	private final CartOrderRepository cartOrderRepository;

	/**
	 * Default Constructor.
	 *
	 * @param cartOrderRepository the cart order repository
	 */
	@Inject
	public ShippingOptionWriterStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository) {

		this.cartOrderRepository = cartOrderRepository;
	}

	@Override
	public ExecutionResult<Boolean> selectShippingOptionForShipment(
			final String storeCode, final String shipmentDetailsId, final String shippingOptionCode) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByShipmentDetailsId(storeCode, shipmentDetailsId));
		boolean shippingServiceLevelChoiceExisted = cartOrder.getShippingServiceLevelGuid() != null;
		cartOrder.setShippingServiceLevelGuid(shippingOptionCode);
		// ensure cart order is saved
		Ensure.successful(cartOrderRepository.saveCartOrder(cartOrder));
		return ExecutionResultFactory.createCreateOKWithData(shippingServiceLevelChoiceExisted, shippingServiceLevelChoiceExisted);
	}
}
