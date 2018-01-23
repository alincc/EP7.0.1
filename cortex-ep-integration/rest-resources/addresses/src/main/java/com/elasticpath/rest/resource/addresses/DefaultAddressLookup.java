/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Default Address Lookup for shipping and billing.
 */
public interface DefaultAddressLookup {

	/**
	 * Gets default address ID for a given scope.
	 *
	 * @param scope the scope
	 * @return the default address
	 */
	ExecutionResult<String> getDefaultAddressId(String scope);
}
