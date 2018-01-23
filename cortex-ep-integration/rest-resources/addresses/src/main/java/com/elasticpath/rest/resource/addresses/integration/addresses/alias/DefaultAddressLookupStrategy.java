/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.addresses.alias;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Service that provides lookup of default address data from external systems.
 */
public interface DefaultAddressLookupStrategy {

	/**
	 * Finds a default address identifier associated with a user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the execution result
	 */
	ExecutionResult<String> findPreferredAddressId(String scope, String userId);
}
