/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for line item price.
 */
public interface CartLineItemPriceLookup {

	/**
	 * Gets the price information for a given line item in a given cart.
	 *
	 *
	 * @param lineItem @return the line item price information
	 * @return {@link com.elasticpath.rest.definition.prices.CartLineItemPriceEntity} for {@link com.elasticpath.rest.definition.carts.LineItemEntity}
	 */
	ExecutionResult<CartLineItemPriceEntity> getLineItemPrice(ResourceState<LineItemEntity> lineItem);
}
