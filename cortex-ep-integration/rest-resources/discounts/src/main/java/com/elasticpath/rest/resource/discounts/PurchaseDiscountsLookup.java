/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The Lookup interface for the Discounts resource.
 */
public interface PurchaseDiscountsLookup {


	/**
	 * Get the discounts for the purchase.
	 * 
	 *
	 *
	 * @param purchaseRepresentation the purchase representation
	 * @return the discounts for the purchase.
	 */
	ExecutionResult<ResourceState<DiscountEntity>> getPurchaseDiscounts(ResourceState<PurchaseEntity> purchaseRepresentation);
}
