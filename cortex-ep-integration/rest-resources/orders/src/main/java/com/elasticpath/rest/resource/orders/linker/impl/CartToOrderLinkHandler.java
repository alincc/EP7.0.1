/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.linker.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;

import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Link handler.
 */
@Singleton
@Named("cartToOrderLinkHandler")
public class CartToOrderLinkHandler implements ResourceStateLinkHandler<OrderEntity> {

	@Inject
	@Named("cartsUriBuilderFactory")
	private CartsUriBuilderFactory cartsUriBuilderFactory;

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<OrderEntity> resourceState) {
		return ImmutableList.of(
				createCartLink(
						resourceState.getEntity()
								.getCartId(),
						resourceState.getScope()
				)
		);
	}

	private ResourceLink createCartLink(final String cartId,
										final String scope) {
		String cartUri = cartsUriBuilderFactory.get()
				.setScope(scope)
				.setCartId(cartId)
				.build();

		return ResourceLinkFactory.create(cartUri,
				CartsMediaTypes.CART.id(),
				OrdersRepresentationRels.CART_REL,
				OrdersRepresentationRels.ORDER_REV);
	}
}
