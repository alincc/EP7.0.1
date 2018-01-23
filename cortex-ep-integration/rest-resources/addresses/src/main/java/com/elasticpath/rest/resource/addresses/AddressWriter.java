/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;


/**
 * Writer interface for updating Address data.
 */
public interface AddressWriter {

	/**
	 * Create an address.
	 *
	 * @param address the address
	 * @param scope the scope
	 * @return the execution result
	 */
	ExecutionResult<String> createAddress(AddressEntity address, String scope);

	/**
	 * Updates the address.
	 *
	 * @param addressId the address id
	 * @param address the address
	 * @return the execution result
	 */
	ExecutionResult<Void> updateAddress(String addressId, AddressEntity address);

	/**
	 * Delete address.
	 *
	 * @param addressId the address id
	 * @return the execution result
	 */
	ExecutionResult<Void> deleteAddress(String addressId);
}
