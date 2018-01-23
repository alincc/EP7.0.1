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
@Named("defaultShippingAddressLookupStrategy")
public class DefaultShippingAddressLookupStrategyImpl implements DefaultAddressLookupStrategy {

	private final CustomerRepository customerRepository;

	/**
	 * Default Constructor.
	 *
	 * @param customerRepository the customer repository
	 */
	@Inject
	public DefaultShippingAddressLookupStrategyImpl(
			@Named("customerRepository")
			final CustomerRepository customerRepository) {

		this.customerRepository = customerRepository;
	}

	@Override
	public ExecutionResult<String> findPreferredAddressId(final String storeCode, final String userGuid) {
		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(userGuid));
		Address defaultAddress = Assign.ifNotNull(getDefaultAddress(customer),
				OnFailure.returnNotFound("Default address not found"));
		return ExecutionResultFactory.createReadOK(defaultAddress.getGuid());
	}

	private Address getDefaultAddress(final Customer customer) {
		return customer.getPreferredShippingAddress();
	}
}
