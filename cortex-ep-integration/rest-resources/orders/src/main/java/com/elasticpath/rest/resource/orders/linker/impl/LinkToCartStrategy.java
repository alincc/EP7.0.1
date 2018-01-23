/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.linker.impl;

import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.orders.OrderLookup;
import com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Create a link to order or order form on a cart representation.
 */
@Singleton
@Named("linkToCartStrategy")
public final class LinkToCartStrategy implements ResourceStateLinkHandler<CartEntity> {

	private final String resourceServerName;
	private final OrderLookup orderLookup;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resource server name
	 * @param orderLookup        order lookup
	 */
	@Inject
	LinkToCartStrategy(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("orderLookup")
			final OrderLookup orderLookup) {

		this.orderLookup = orderLookup;
		this.resourceServerName = resourceServerName;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<CartEntity> cartResourceState) {

		final Collection<ResourceLink> result;

		CartEntity cartEntity = cartResourceState.getEntity();
		String scope = cartResourceState.getScope();
		ExecutionResult<ResourceState<OrderEntity>> orderResult = orderLookup.findOrderByCartId(scope, cartEntity.getCartId());

		if (orderResult.isSuccessful()) {
			ResourceState<OrderEntity> orderRepresentation = orderResult.getData();
			OrderEntity orderEntity = orderRepresentation.getEntity();
			String encodedOrderId = orderEntity.getOrderId();
			String orderUri = URIUtil.format(resourceServerName, scope, encodedOrderId);
			ResourceLink resultLink = ResourceLinkFactory.create(orderUri,
					ORDER.id(),
					OrdersRepresentationRels.ORDER_REL,
					OrdersRepresentationRels.CART_REV);
			result = Collections.singleton(resultLink);
		} else {
			result = Collections.emptyList();
		}

		return result;
	}
}
