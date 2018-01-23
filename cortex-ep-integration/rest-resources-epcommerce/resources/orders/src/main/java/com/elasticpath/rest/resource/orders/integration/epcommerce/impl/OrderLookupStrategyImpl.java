/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.orders.integration.OrderLookupStrategy;
import com.elasticpath.rest.resource.orders.integration.epcommerce.transform.CartOrderTransformer;

/**
 * EP Commerce implementation of the order lookup strategy.
 */
@Singleton
@Named("orderLookupStrategy")
public class OrderLookupStrategyImpl implements OrderLookupStrategy {
	private static final String CART_WAS_NOT_FOUND = "No cart was found with GUID = %s.";

	private final CartOrderRepository cartOrderRepository;
	private final CartOrderTransformer cartOrderTransformer;
	private final ShoppingCartRepository shoppingCartRepository;


	/**
	 * Instantiates a new order lookup strategy.
	 *
	 * @param cartOrderRepository the cart order repository
	 * @param shoppingCartRepository the shopping cart service
	 * @param cartOrderTransformer the cart order transformer
	 */
	@Inject
	public OrderLookupStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository,
			@Named("cartOrderTransformer")
			final CartOrderTransformer cartOrderTransformer) {

		this.cartOrderRepository = cartOrderRepository;
		this.shoppingCartRepository = shoppingCartRepository;
		this.cartOrderTransformer = cartOrderTransformer;
	}


	@Override
	public ExecutionResult<OrderEntity> getOrder(final String storeCode, final String cartOrderGuid) {
		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		OrderEntity orderEntity = cartOrderTransformer.transformToEntity(cartOrder);
		return ExecutionResultFactory.createReadOK(orderEntity);
	}

	@Override
	public ExecutionResult<OrderEntity> getOrderForCart(final String storeCode, final String cartGuid) {
		Ensure.isTrue(shoppingCartRepository.verifyShoppingCartExistsForStore(cartGuid, storeCode),
				OnFailure.returnNotFound(CART_WAS_NOT_FOUND, cartGuid));
		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByCartGuid(cartGuid));
		OrderEntity orderEntity = cartOrderTransformer.transformToEntity(cartOrder);
		return ExecutionResultFactory.createReadOK(orderEntity);
	}

	@Override
	public ExecutionResult<Collection<String>> getOrderIds(final String storeCode, final String customerGuid) {
		Collection<String> cartOrderGuids = Assign.ifSuccessful(cartOrderRepository.findCartOrderGuidsByCustomer(storeCode, customerGuid));
		return ExecutionResultFactory.createReadOK(cartOrderGuids);
	}
}
