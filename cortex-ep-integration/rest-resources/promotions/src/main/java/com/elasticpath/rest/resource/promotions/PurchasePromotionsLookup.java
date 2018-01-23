/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Promotions look up for Purchase.
 */
public interface PurchasePromotionsLookup {

	/**
	 * Gets the promotions applied to a given purchase.
	 *
	 *
	 * @param purchaseRepresentation the purchase line representation to read promotions
	 * @return the links representation with promotions links.
	 */
	ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForPurchase(ResourceState<PurchaseEntity> purchaseRepresentation);

}
