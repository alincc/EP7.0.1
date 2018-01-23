/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.link.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.resource.discounts.rel.DiscountsResourceRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Add cart link to discount {@link ResourceStateLinkHandler}.
 */
@Singleton
@Named("addCartLinkToDiscountHandler")
public class AddCartLinkToDiscountHandler implements ResourceStateLinkHandler<DiscountEntity> {
	private final CartsUriBuilderFactory cartsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param cartsUriBuilderFactory the {@link CartsUriBuilderFactory}
	 */
	@Inject
	public AddCartLinkToDiscountHandler(
			@Named("cartsUriBuilderFactory")
			final CartsUriBuilderFactory cartsUriBuilderFactory) {
		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<DiscountEntity> discount) {
		String cartId = discount.getEntity()
								.getCartId();
		if (cartId != null) {
			String cartUri = cartsUriBuilderFactory.get()
												.setCartId(cartId)
												.setScope(discount.getScope())
												.build();
			return Collections.singleton(ResourceLinkFactory.create(cartUri, CartsMediaTypes.CART.id(),
									DiscountsResourceRels.CART_REL, DiscountsResourceRels.DISCOUNT_REV));
		}

		return Collections.emptyList();
	}
}
