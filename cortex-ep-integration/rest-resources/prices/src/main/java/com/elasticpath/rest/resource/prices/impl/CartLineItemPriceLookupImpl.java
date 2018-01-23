/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.prices.CartLineItemPriceLookup;
import com.elasticpath.rest.resource.prices.integration.CartLineItemPriceLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Look up cart line item prices.
 */
@Singleton
@Named("cartLineItemPriceLookup")
public final class CartLineItemPriceLookupImpl implements CartLineItemPriceLookup {

	private final CartLineItemPriceLookupStrategy cartLineItemPriceLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param cartLineItemPriceLookupStrategy the price lookup strategy
	 */
	@Inject
	CartLineItemPriceLookupImpl(
			@Named("cartLineItemPriceLookupStrategy")
			final CartLineItemPriceLookupStrategy cartLineItemPriceLookupStrategy) {
		this.cartLineItemPriceLookupStrategy = cartLineItemPriceLookupStrategy;
	}


	@Override
	public ExecutionResult<CartLineItemPriceEntity> getLineItemPrice(final ResourceState<LineItemEntity> lineItem) {

		LineItemEntity entity = lineItem.getEntity();
		String decodedCartId = Base32Util.decode(entity.getCartId());
		String decodedLineItemId = Base32Util.decode(entity.getLineItemId());
		CartLineItemPriceEntity cartLineItemPrice = Assign.ifSuccessful(cartLineItemPriceLookupStrategy.getLineItemPrice(
				lineItem.getScope(), decodedCartId, decodedLineItemId));
		return ExecutionResultFactory.createReadOK(cartLineItemPrice);
	}
}
