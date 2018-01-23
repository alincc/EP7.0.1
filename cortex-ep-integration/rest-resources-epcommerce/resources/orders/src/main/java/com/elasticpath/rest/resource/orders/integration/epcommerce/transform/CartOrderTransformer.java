/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.transform;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link CartOrder} into a {@link OrderEntity}, and vice versa.
 */
@Singleton
@Named("cartOrderTransformer")
public class CartOrderTransformer extends AbstractDomainTransformer<CartOrder, OrderEntity> {

	/**
	 * {@inheritDoc} <br>
	 * This should not be used to create a {@link CartOrder}. <br>
	 * Use an alternate method to obtain cart order information.
	 */
	@Override
	public CartOrder transformToDomain(final OrderEntity orderEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public OrderEntity transformToEntity(final CartOrder cartOrder, final Locale locale) {
		return OrderEntity.builder()
				.withOrderId(cartOrder.getGuid())
				.withCartId(cartOrder.getShoppingCartGuid())
				.build();
	}
}
