/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.integration;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * The Item Id For Product Lookup Strategy.
 */
public interface ItemIdLookupStrategy {

	/**
	 * Find item id by decoded product id.
	 *
	 * @param scope the scope
	 * @param decodedProductId the decoded product id
	 * @return the execution result
	 */
	ExecutionResult<String> getDefaultItemIdForProduct(String scope, String decodedProductId);
}
