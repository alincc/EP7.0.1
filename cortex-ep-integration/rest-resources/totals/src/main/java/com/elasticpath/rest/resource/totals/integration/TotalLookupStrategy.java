/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.totals.TotalEntity;


/**
 * The Interface TotalLookupStrategy.
 */
public interface TotalLookupStrategy {

	/**
	 * Gets the order total.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order id
	 * @return the cart order total
	 */
	ExecutionResult<TotalEntity> getOrderTotal(String scope, String decodedOrderId);

	/**
	 * Gets the cart total.
	 *
	 * @param scope the scope
	 * @param decodedCartId the decoded cart id
	 * @return the cart total
	 */
	ExecutionResult<TotalEntity> getCartTotal(String scope, String decodedCartId);

	/**
	 * Get the line item total.
	 *
 	 * @param scope the scope
	 * @param decodedCartId the decoded cart id
	 * @param decodedLineItemId the decoded line item id
	 * @return the line item total
	 */
	ExecutionResult<TotalEntity> getLineItemTotal(String scope, String decodedCartId, String decodedLineItemId);

}