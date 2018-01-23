/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.discounts.DiscountEntity;

/**
 * Integration point for purchase discounts lookup.
 */
public interface PurchaseDiscountsLookupStrategy {


	/**
	 * Return the discounts for this purchase.
	 * 
	 * @param purchaseIdDecoded the decoded purchase id.
	 * @param scope scope
	 * @return the discounts for this purchase
	 */
	ExecutionResult<DiscountEntity> getPurchaseDiscounts(String purchaseIdDecoded, String scope);

}
