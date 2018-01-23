/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.discounts.DiscountEntity;

/**
 * Carts discounts lookup strategy.
 */
public interface CartDiscountsLookupStrategy {


	/**
	 * Gets the cart discounts.
	 * @param cartGuid The cart guid.
	 * @param scope The scope.
	 * @return The discounts.
	 */
	ExecutionResult<DiscountEntity> getCartDiscounts(String cartGuid, String scope);
}