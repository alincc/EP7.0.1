/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.resource.addresses.integration.addresses.AddressLookupStrategy;
import com.elasticpath.rest.resource.addresses.integration.epcommerce.transform.CustomerAddressTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * EP specific implementation of {@link AddressLookupStrategy}.
 */
@Singleton
@Named("addressLookupStrategy")
public class AddressLookupStrategyImpl implements AddressLookupStrategy {

	private final CustomerRepository customerRepository;
	private final CustomerAddressTransformer customerAddressTransformer;

	/**
	 * Default constructor.
	 *
	 * @param customerRepository the customer repository
	 * @param customerAddressTransformer the customer address transformer
	 */
	@Inject
	public AddressLookupStrategyImpl(
			@Named("customerRepository")
			final CustomerRepository customerRepository,
			@Named("customerAddressTransformer")
			final CustomerAddressTransformer customerAddressTransformer) {

		this.customerRepository = customerRepository;
		this.customerAddressTransformer = customerAddressTransformer;
	}


	@Override
	public ExecutionResult<AddressEntity> find(final String storeCode, final String userGuid, final String addressGuid) {
		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(userGuid));
		CustomerAddress address = Assign.ifNotNull(customer.getAddressByGuid(addressGuid),
				OnFailure.returnNotFound("Address not found."));
		AddressEntity data = customerAddressTransformer.transformToEntity(address);
		return ExecutionResultFactory.createReadOK(data);
	}

	@Override
	public ExecutionResult<Collection<String>> findIdsByUserId(final String storeCode, final String userGuid) {
		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(userGuid));
		Collection<String> addressGuids = new ArrayList<>();
		for (CustomerAddress address : customer.getAddresses()) {
			addressGuids.add(address.getGuid());
		}
		return ExecutionResultFactory.createReadOK(addressGuids);
	}

	@Override
	public ExecutionResult<Collection<String>> findBillingIdsByUserId(final String storeCode, final String userGuid) {
		return findIdsByUserId(storeCode, userGuid);
	}

	@Override
	public ExecutionResult<Collection<String>> findShippingIdsByUserId(final String storeCode, final String userGuid) {
		return findIdsByUserId(storeCode, userGuid);
	}
}
