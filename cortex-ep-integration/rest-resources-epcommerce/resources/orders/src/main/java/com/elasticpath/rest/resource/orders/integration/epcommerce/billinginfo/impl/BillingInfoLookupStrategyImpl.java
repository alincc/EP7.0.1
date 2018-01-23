/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.billinginfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.orders.integration.billinginfo.BillingInfoLookupStrategy;

/**
 * EP Commerce implementation of the billing info lookup strategy.
 */
@Singleton
@Named("billingInfoLookupStrategy")
public class BillingInfoLookupStrategyImpl implements BillingInfoLookupStrategy {

	private final CartOrderRepository cartOrderRepository;

	/**
	 * Instantiates a new billing info lookup strategy.
	 *
	 * @param cartOrderRepository the cart order repository
	 */
	@Inject
	public BillingInfoLookupStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository) {

		this.cartOrderRepository = cartOrderRepository;
	}

	@Override
	public ExecutionResult<String> getBillingAddress(final String storeCode, final String cartOrderGuid) {
		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		Address billingAddress = Assign.ifSuccessful(cartOrderRepository.getBillingAddress(cartOrder));
		return ExecutionResultFactory.createReadOK(billingAddress.getGuid());
	}
}
