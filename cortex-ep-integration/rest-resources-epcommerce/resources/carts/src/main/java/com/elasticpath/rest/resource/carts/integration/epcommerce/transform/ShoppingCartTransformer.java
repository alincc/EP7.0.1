/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.transform;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link ShoppingCart} into a {@link CartEntity}, and vice versa.
 */
@Singleton
@Named("shoppingCartTransformer")
public class ShoppingCartTransformer extends AbstractDomainTransformer<ShoppingCart, CartEntity> {

	@Override
	public ShoppingCart transformToDomain(final CartEntity cartEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public CartEntity transformToEntity(final ShoppingCart cart, final Locale locale) {
		return CartEntity.builder()
				.withCartId(cart.getGuid())
				.withTotalQuantity(cart.getNumItems())
				.build();
	}
}
