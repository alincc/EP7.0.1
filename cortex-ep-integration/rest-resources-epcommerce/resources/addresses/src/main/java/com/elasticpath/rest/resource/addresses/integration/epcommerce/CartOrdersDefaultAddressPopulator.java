/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.epcommerce;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;

/**
 * Set default addresses on cart order belonging to customer.
 */
public interface CartOrdersDefaultAddressPopulator {

	/**
	 * Update all of the customer's cart orders default billing and shipping addreses with the address
	 * if not already set, and boolean is true for type of address.
	 *
	 * @param customer customer to retrieve cart orders for.
	 * @param address address to use to populate
	 * @param scope scope to look up cart orders.
	 * @param updateBillingAddress true if billing address on cart order should be updated.
	 * @param updateShippingAddress true if shipping address on cart order should be updated.
	 */
	void updateAllCartOrdersAddresses(Customer customer, CustomerAddress address, String scope,
			boolean updateBillingAddress, boolean updateShippingAddress);
	
}
