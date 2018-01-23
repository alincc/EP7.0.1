/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.epcommerce.alias.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.addresses.integration.addresses.alias.DefaultAddressLookupStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * EP-specific implementation of {@link DefaultAddressLookupStrategy}.
 */
@Singleton
@Named("defaultBillingAddressLookupStrategy")
public class DefaultBillingAddressLookupStrategyImpl implements DefaultAddressLookupStrategy {

	private final CustomerRepository customerRepository;

	/**
	 * Default constructor.
	 *
	 * @param customerRepository the customer repository
	 */
	@Inject
	public DefaultBillingAddressLookupStrategyImpl(
			@Named("customerRepository")
			final CustomerRepository customerRepository) {

		this.customerRepository = customerRepository;
	}

	/**
	 * Returns execution result with preferred billing address if found, or execution result of not found if not found.
	 *
	 * @param storeCode the store code
	 * @param userGuid the user guid
	 *
	 * @return execution result of not found or string
	 */
	@Override
	public ExecutionResult<String> findPreferredAddressId(final String storeCode, final String userGuid) {
		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(userGuid));
		Address defaultAddress = Assign.ifNotNull(getDefaultAddress(customer),
				OnFailure.returnNotFound("Default address not found"));
		return ExecutionResultFactory.createReadOK(defaultAddress.getGuid());
	}

	/**
	 * Gets preferred shipping address for given customer.
	 *
	 * @param customer the customer
	 *
	 * @return Address object for the given customer
	 */
	private Address getDefaultAddress(final Customer customer) {
		return customer.getPreferredBillingAddress();
	}
}
