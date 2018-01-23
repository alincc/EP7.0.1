/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.discounts.CartDiscountsLookup;
import com.elasticpath.rest.resource.discounts.integration.CartDiscountsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * The Lookup for Discounts resource.
 */
@Singleton
@Named("cartDiscountsLookup")
public final class CartDiscountsLookupImpl implements CartDiscountsLookup {

	private final TransformRfoToResourceState<DiscountEntity, DiscountEntity, CartEntity> discountCartTransformer;
	private final CartDiscountsLookupStrategy cartDiscountsLookupStrategy;

	/**
	 * Constructor.
	 * @param discountsCartTransformer The transformer.
	 * @param cartDiscountsLookupStrategy the cart order repository
	 */
	@Inject
	public CartDiscountsLookupImpl(
			@Named("discountsCartTransformer")
			final TransformRfoToResourceState<DiscountEntity, DiscountEntity, CartEntity> discountsCartTransformer,
			@Named("cartDiscountsLookupStrategy")
			final CartDiscountsLookupStrategy cartDiscountsLookupStrategy) {
		this.discountCartTransformer = discountsCartTransformer;
		this.cartDiscountsLookupStrategy = cartDiscountsLookupStrategy;
	}


	@Override
	public ExecutionResult<ResourceState<DiscountEntity>> getCartDiscounts(final ResourceState<CartEntity> cartRepresentation) {

		CartEntity cartEntity = cartRepresentation.getEntity();
		String cartId = cartEntity.getCartId();
		String scope = cartRepresentation.getScope();
		final String cartGuid = Base32Util.decode(cartId);

		DiscountEntity cartDiscount = Assign.ifSuccessful(cartDiscountsLookupStrategy.getCartDiscounts(cartGuid, scope));

		ResourceState<DiscountEntity> discountRepresentation = discountCartTransformer.transform(cartDiscount,
				cartRepresentation);
		return ExecutionResultFactory.createReadOK(discountRepresentation);
	}
}
