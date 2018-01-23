/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The Lookup interface for the Discounts resource.
 */
public interface CartDiscountsLookup {


	/**
	 * Gets the cart discounts.
	 *
	 *
	 *
	 * @param cartRepresentation The cart.
	 * @return the collection of cart discounts
	 */
	ExecutionResult<ResourceState<DiscountEntity>> getCartDiscounts(ResourceState<CartEntity> cartRepresentation);
}
