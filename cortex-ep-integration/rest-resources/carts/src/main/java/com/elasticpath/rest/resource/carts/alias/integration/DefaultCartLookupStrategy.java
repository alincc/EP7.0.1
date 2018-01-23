/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.alias.integration;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Strategy for default cart look up.
 */
public interface DefaultCartLookupStrategy {

	/**
	 * Get the default cart id for the currently logged-in user.
	 *
	 * @param scope the scope
	 * @return Execution result with default cart ID
	 */
	ExecutionResult<String> getDefaultCartId(String scope);
}
