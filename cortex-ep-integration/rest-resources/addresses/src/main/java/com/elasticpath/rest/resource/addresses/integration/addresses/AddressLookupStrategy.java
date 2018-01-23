/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.addresses;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;

/**
 * Service that provides lookup of address data from external systems.
 */
public interface AddressLookupStrategy {

	/**
	 * Finds a specific address for a user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @param addressId the address id
	 * @return the execution result
	 */
	ExecutionResult<AddressEntity> find(String scope, String userId, String addressId);

	/**
	 * Finds addresses associated with a user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the execution result
	 */
	ExecutionResult<Collection<String>> findIdsByUserId(String scope, String userId);

	/**
	 * Finds billing addresses associated with a user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the execution result
	 */
	ExecutionResult<Collection<String>> findBillingIdsByUserId(String scope, String userId);

	/**
	 * Finds shipping addresses associated with a user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the execution result
	 */
	ExecutionResult<Collection<String>> findShippingIdsByUserId(String scope, String userId);
}
