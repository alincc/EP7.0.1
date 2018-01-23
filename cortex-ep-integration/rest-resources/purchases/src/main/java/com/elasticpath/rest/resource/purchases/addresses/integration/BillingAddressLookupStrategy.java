/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.addresses.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;

/**
 * Lookup strategy for a purchase billing address.
 */
public interface BillingAddressLookupStrategy {
	/**
	 * Returns a {@link AddressEntity} that represents the given order's billing address.
	 *
	 * @param scope the scope in which the order exists
	 * @param purchaseId the ID of the purchase
	 * @return the billing address for the specified purchase
	 */
	ExecutionResult<AddressEntity> getBillingAddress(String scope, String purchaseId);
}
