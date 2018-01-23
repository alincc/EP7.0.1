/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * The transformer to convert a {@link OrderEntity} to a {@link ResourceState}.
 */
@Singleton
@Named("orderTransformer")
public class OrderTransformer {

	private final String resourceServerName;

	/**
	 * Default Constructor.
	 *
	 * @param resourceServerName the resource server name
	 *
	 */
	@Inject
	public OrderTransformer(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	/**
	 * Transforms a {@link OrderEntity} into a {@link ResourceState}&lt;{@link OrderEntity}>.
	 *
	 * @param scope the scope
	 * @param orderEntity the order entity
	 * @return the order representation
	 */
	public ResourceState<OrderEntity> transformToRepresentation(final String scope, final OrderEntity orderEntity) {

		String orderId = Base32Util.encode(orderEntity.getOrderId());
		String cartId = Base32Util.encode(orderEntity.getCartId());

		String orderUri = URIUtil.format(resourceServerName, scope, orderId);
		Self self = SelfFactory.createSelf(orderUri);

		OrderEntity newOrderEntity = OrderEntity.builder()
				.withOrderId(orderId)
				.withCartId(cartId)
				.build();
		return ResourceState.Builder.create(newOrderEntity)
				.withSelf(self)
				.withScope(scope)
				.build();
	}
}
