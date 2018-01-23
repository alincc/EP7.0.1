/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * The Cart Transformer.
 */
@Singleton
@Named("cartTransformer")
public final class CartTransformer implements TransformToResourceState<CartEntity, CartEntity> {

	private final CartsUriBuilderFactory cartsUriBuilderFactory;

	/**
	 * Default Constructor.
	 *
	 * @param cartsUriBuilderFactory carts URI Builder Provider.
	 */
	@Inject
	public CartTransformer(
			@Named("cartsUriBuilderFactory")
			final CartsUriBuilderFactory cartsUriBuilderFactory) {

		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
	}

	/**
	 * Transforms a {@link CartEntity} to an {@link ResourceState}.
	 *
	 * @param scope the scope
	 * @param cartEntity the cart cartEntity
	 * @return the cart representation
	 */
	@Override
	public ResourceState<CartEntity> transform(final String scope,
			final CartEntity cartEntity) {
		String cartId = Base32Util.encode(cartEntity.getCartId());

		String selfUri = cartsUriBuilderFactory.get()
				.setCartId(cartId)
				.setScope(scope)
				.build();
		Self self = SelfFactory.createSelf(selfUri);
		CartEntity encodedCartEntity = CartEntity.builderFrom(cartEntity)
											.withCartId(cartId)
											.build();
		return ResourceState.Builder.create(encodedCartEntity)
				.withSelf(self)
				.withScope(scope)
				.build();
	}
}
