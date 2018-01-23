/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Queries for slots information.
 */
public interface ItemIdLookup {

	/**
	 * Gets the default item id for a product.
	 *
	 * @param scope the scope
	 * @param productId the product id
	 * @return the default item id for a product
	 */
	ExecutionResult<String> getDefaultItemIdForProduct(String scope, String productId);
}
