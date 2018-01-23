/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.addresses;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;

/**
 * The Interface AddressWriterStrategy.
 */
public interface AddressWriterStrategy {

	/**
	 * Updates the Address.
	 *
	 * @param address the address
	 * @param userId the user id
	 * @return the execution result
	 */
	ExecutionResult<Void> update(AddressEntity address, String userId);

	/**
	 * Creates an address.
	 *
	 * @param userId the user id
	 * @param address the address
	 * @param scope the scope
	 * @return the execution result
	 */
	ExecutionResult<String> create(String userId, AddressEntity address, String scope);

	/**
	 * Delete the address from the user.
	 *
	 * @param userId the user id
	 * @param decodedAddressId the decoded address id
	 * @return the execution result
	 */
	ExecutionResult<Void> delete(String userId, String decodedAddressId);
}
