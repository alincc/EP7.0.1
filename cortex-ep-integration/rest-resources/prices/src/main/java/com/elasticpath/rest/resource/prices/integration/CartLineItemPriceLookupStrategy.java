/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;

/**
 * Queries for the Price information.
 */
public interface CartLineItemPriceLookupStrategy {

	/**
	 * Gets the price of a line item.
	 *
	 *
	 * @param scope the scope
	 * @param decodedCartId the decoded cart id
	 * @param decodedLineItemId the decoded line item id
	 * @return the result
	 */
	ExecutionResult<CartLineItemPriceEntity> getLineItemPrice(String scope, String decodedCartId, String decodedLineItemId);
}
