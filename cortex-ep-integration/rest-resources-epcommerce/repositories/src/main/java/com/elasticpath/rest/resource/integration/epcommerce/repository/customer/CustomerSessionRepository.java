/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Encapsulates operations on customer session.
 */
public interface CustomerSessionRepository {

	/**
	 * Find or create a customer session. The customer session will have a shopper with a valid TagSet.
	 *
	 * @return the customer session
	 */
	ExecutionResult<CustomerSession> findOrCreateCustomerSession();

	/**
	 * Creates a customer session for a given customer.
	 *
	 * @param customerGuid the customer guid.
	 * @return a customer session.
	 */
	ExecutionResult<CustomerSession> findCustomerSessionByGuid(String customerGuid);

	/**
	 * Creates a customer session for a given customer.
	 *
	 * @param storeCode the storeCode.
	 * @param customerUserId  the customer guid.
	 * @return a customer session.
	 */
	ExecutionResult<CustomerSession> findCustomerSessionByUserId(String storeCode, String customerUserId);

	/**
	 * Triggers invalidation of CustomerSession instance associated with given customer guid.
	 *
	 * @param customerGuid the customer guid.
	 */
	void invalidateCustomerSessionByGuid(String customerGuid);
}