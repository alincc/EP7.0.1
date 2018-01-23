/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.orders.OrderLookup;
import com.elasticpath.rest.resource.orders.integration.OrderLookupStrategy;
import com.elasticpath.rest.resource.orders.transform.OrderTransformer;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Look up orders.
 */
@Singleton
@Named("orderLookup")
public final class OrderLookupImpl implements OrderLookup {

	private final OrderTransformer orderTransformer;
	private final OrderLookupStrategy orderLookupStrategy;

	/**
	 * Default Constructor.
	 *
	 * @param orderLookupStrategy the order lookup strategy
	 * @param orderTransformer the order transformer
	 */
	@Inject
	OrderLookupImpl(
			@Named("orderLookupStrategy")
			final OrderLookupStrategy orderLookupStrategy,
			@Named("orderTransformer")
			final OrderTransformer orderTransformer) {

		this.orderTransformer = orderTransformer;
		this.orderLookupStrategy = orderLookupStrategy;
	}

	@Override
	public ExecutionResult<ResourceState<OrderEntity>> findOrderByOrderId(final String scope, final String orderId) {
		assert orderId != null : "Cannot get Order for null orderId";

		String decodedOrderId = Base32Util.decode(orderId);
		OrderEntity orderEntity = Assign.ifSuccessful(orderLookupStrategy.getOrder(scope, decodedOrderId));
		ResourceState<OrderEntity> orderRepresentation = orderTransformer.transformToRepresentation(scope, orderEntity);
		return ExecutionResultFactory.createReadOK(orderRepresentation);
	}

	@Override
	public ExecutionResult<Collection<String>> findOrderIds(final String scope, final String userId) {
				Collection<String> orderIds = Assign.ifSuccessful(orderLookupStrategy.getOrderIds(scope, userId));
				return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(orderIds));
	}

	@Override
	public ExecutionResult<ResourceState<OrderEntity>> findOrderByCartId(final String scope, final String cartId) {
				String decodedCartId = Base32Util.decode(cartId);
				OrderEntity orderEntity = Assign.ifSuccessful(orderLookupStrategy.getOrderForCart(scope, decodedCartId));
				ResourceState<OrderEntity> orderRepresentation = orderTransformer.transformToRepresentation(scope, orderEntity);
				return ExecutionResultFactory.createReadOK(orderRepresentation);
	}
}
