/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.purchases.addresses.integration.BillingAddressLookupStrategy;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.addresses.transform.BillingAddressTransformer;

/**
 * DCE implementation of {@link BillingAddressLookupStrategy}.
 */
@Singleton
@Named("billingAddressLookupStrategy")
public class BillingAddressLookupStrategyImpl implements BillingAddressLookupStrategy {

	private final OrderRepository orderRepository;
	private final BillingAddressTransformer billingAddressTransformer;

	/**
	 * Default constructor.
	 *
	 * @param orderRepository           the order repository
	 * @param billingAddressTransformer the purchase address transformer
	 */
	@Inject
	public BillingAddressLookupStrategyImpl(
			@Named("orderRepository")
			final OrderRepository orderRepository,
			@Named("billingAddressTransformer")
			final BillingAddressTransformer billingAddressTransformer) {

		this.orderRepository = orderRepository;
		this.billingAddressTransformer = billingAddressTransformer;
	}

	@Override
	public ExecutionResult<AddressEntity> getBillingAddress(final String scope, final String purchaseId) {

		Order order = Assign.ifSuccessful(orderRepository.findByGuid(scope, purchaseId));
		OrderAddress address = Assign.ifNotNull(order.getBillingAddress(),
				OnFailure.returnNotFound("Default address not found"));
		AddressEntity data = billingAddressTransformer.transformToEntity(address);
		return ExecutionResultFactory.createReadOK(data);
	}
}