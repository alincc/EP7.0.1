/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.DiscountsUriBuilderFactory;

/**
 * Transform a discount entity (RFO on Cart) to a discount representation.
 */
@Singleton
@Named("discountsCartTransformer")
public final class CartDiscountsRfoResourceStateTransformerImpl
		implements TransformRfoToResourceState<DiscountEntity, DiscountEntity, CartEntity> {

	private final DiscountsUriBuilderFactory discountsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param discountsUriBuilderFactory the URI builder factory.
	 */
	@Inject
	CartDiscountsRfoResourceStateTransformerImpl(
			@Named("discountsUriBuilderFactory")
			final DiscountsUriBuilderFactory discountsUriBuilderFactory) {

		this.discountsUriBuilderFactory = discountsUriBuilderFactory;
	}

	@Override
	public ResourceState<DiscountEntity> transform(
			final DiscountEntity discountForCart, final ResourceState<CartEntity> cart) {

		String otherUri = cart.getSelf()
											.getUri();
		String selfUri = discountsUriBuilderFactory.get()
												.setSourceUri(otherUri)
												.build();
		Self self = SelfFactory.createSelf(selfUri);

		return ResourceState.Builder.create(DiscountEntity.builderFrom(discountForCart)
															.withCartId(cart.getEntity().getCartId())
															.build())
									.withSelf(self)
									.withScope(cart.getScope())
									.build();
	}
}
