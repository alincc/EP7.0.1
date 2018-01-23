/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc;

import com.elasticpath.money.Money;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Performs calculations on the shopping cart.
 */
public interface TotalsCalculator {

	/**
	 * Calculates the total for the given shopping cart.
	 * @param storeCode the store code.
	 * @param shoppingCartGuid the shopping cart to total
	 * @return the execution result containing the total or an error
	 */
	ExecutionResult<Money> calculateTotalForShoppingCart(String storeCode, String shoppingCartGuid);

	/**
	 * Calculates the total for the given cart order.
	 * @param storeCode the store code.
	 * @param cartOrderGuid the cart order to total
	 * @return the execution result containing the total or an error
	 */
	ExecutionResult<Money> calculateTotalForCartOrder(String storeCode, String cartOrderGuid);

	/**
	 * Calculates the total without tax for the given cart order.
	 * @param storeCode the store code.
	 * @param cartOrderGuid the cart order to total
	 * @return the execution result containing the total or an error
	 */
	ExecutionResult<Money> calculateSubTotalForCartOrder(String storeCode, String cartOrderGuid);

	/**
	 * Calculates the total for a line item.
	 * @param storeCode the store code.
	 * @param shoppingCartGuid the shopping cart guid
	 * @param cartItemGuid the cart item guid
	 * @return the execution result containing the line item total or an error
	 */
	ExecutionResult<Money> calculateTotalForLineItem(String storeCode, String shoppingCartGuid, String cartItemGuid);

}
