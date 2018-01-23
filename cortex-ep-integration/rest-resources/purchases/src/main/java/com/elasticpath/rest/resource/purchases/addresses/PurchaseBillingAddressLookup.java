/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.addresses;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup for a purchase billing address.
 */
public interface PurchaseBillingAddressLookup {
	/**
	 * Returns an {@link ResourceState} that represents the given order's billing address.
	 *
	 * @param scope the scope in which the order exists
	 * @param purchaseId the id of the purchase
	 * @return the billing address for the specified purchase
	 */
	ExecutionResult<ResourceState<AddressEntity>> getBillingAddress(String scope, String purchaseId);
}
