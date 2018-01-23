/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.billinginfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.orders.integration.billinginfo.BillingInfoWriterStrategy;

/**
 * Writer strategy for billing information.
 */
@Singleton
@Named("billingInfoWriterStrategy")
public class BillingInfoWriterStrategyImpl implements BillingInfoWriterStrategy {

	private final CartOrderRepository cartOrderRepository;

	/**
	 * Constructor.
	 *
	 * @param cartOrderRepository the cart order repository
	 */
	@Inject
	BillingInfoWriterStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository) {

		this.cartOrderRepository = cartOrderRepository;
	}

	@Override
	public ExecutionResult<Boolean> setBillingAddress(final String storeCode, final String addressGuid, final String cartOrderGuid) {
		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		return setBillingAddress(addressGuid, cartOrder);
	}

	/**
	 * Sets the billing address for an cart order.
	 *
	 * @param billingAddressGuid the billing address GUID
	 * @param cartOrder the target cart order
	 * @return true if the cart order already had a billing address, false if not, or error
	 */
	private ExecutionResult<Boolean> setBillingAddress(final String billingAddressGuid, final CartOrder cartOrder) {
		boolean replacedExisting = cartOrder.getBillingAddressGuid() != null;
		cartOrder.setBillingAddressGuid(billingAddressGuid);

		// ensure cart order is successful saved
		Ensure.successful(cartOrderRepository.saveCartOrder(cartOrder));
		return ExecutionResultFactory.createReadOK(replacedExisting);
	}
}
