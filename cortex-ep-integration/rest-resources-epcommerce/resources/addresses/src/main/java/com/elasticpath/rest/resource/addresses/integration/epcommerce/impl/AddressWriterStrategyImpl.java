/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.exception.EpValidationException;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.resource.addresses.integration.addresses.AddressWriterStrategy;
import com.elasticpath.rest.resource.addresses.integration.epcommerce.CartOrdersDefaultAddressPopulator;
import com.elasticpath.rest.resource.addresses.integration.epcommerce.transform.CustomerAddressTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;

/**
 * Ep-specific implementation of {@link AddressWriterStrategy}.
 */
@Singleton
@Named("addressWriterStrategy")
public class AddressWriterStrategyImpl implements AddressWriterStrategy {

	private static final String ADDRESS_NOT_FOUND = "Address not found.";

	private final CustomerRepository customerRepository;
	private final CustomerAddressTransformer customerAddressTransformer;
	private final CartOrdersDefaultAddressPopulator cartOrdersDefaultAddressPopulator;
	private final ExceptionTransformer exceptionTransformer;


	/**
	 * Default Constructor.
	 *
	 * @param customerRepository                the customer repository
	 * @param customerAddressTransformer        the customer address transformer
	 * @param cartOrdersDefaultAddressPopulator cart orders default address populator.
	 * @param exceptionTransformer              the exception transformer
	 */
	@Inject
	public AddressWriterStrategyImpl(
			@Named("customerRepository")
			final CustomerRepository customerRepository,
			@Named("customerAddressTransformer")
			final CustomerAddressTransformer customerAddressTransformer,
			@Named("cartOrdersDefaultAddressPopulator")
			final CartOrdersDefaultAddressPopulator cartOrdersDefaultAddressPopulator,
			@Named("exceptionTransformer")
			final ExceptionTransformer exceptionTransformer) {

		this.customerRepository = customerRepository;
		this.customerAddressTransformer = customerAddressTransformer;
		this.cartOrdersDefaultAddressPopulator = cartOrdersDefaultAddressPopulator;
		this.exceptionTransformer = exceptionTransformer;
	}

	@Override
	public ExecutionResult<Void> update(final AddressEntity addressEntity, final String userGuid) {


		String addressGuid = addressEntity.getAddressId();
		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(userGuid));

		CustomerAddress address = Assign.ifNotNull(getExistingAddressFromCustomer(customer, addressGuid),
				OnFailure.returnNotFound(ADDRESS_NOT_FOUND));

		AddressDetailEntity addressDetailEntity = addressEntity.getAddress();
		if (addressDetailEntity != null) {
			updateCountry(address, addressDetailEntity.getCountryName());
			updateState(address, addressDetailEntity.getRegion());
			updateCity(address, addressDetailEntity.getLocality());
			updatePostal(address, addressDetailEntity.getPostalCode());
			updateStreet1(address, addressDetailEntity.getStreetAddress());
			updateStreet2(address, addressDetailEntity.getExtendedAddress());
		}

		NameEntity nameEntity = addressEntity.getName();
		if (nameEntity != null) {
			updateFirstName(address, nameEntity.getGivenName());
			updateLastName(address, nameEntity.getFamilyName());
		}

		return updateCoreCustomerAddress(customer, address);
	}

	private ExecutionResult<Void> updateCoreCustomerAddress(final Customer customer, final CustomerAddress address) {
		ExecutionResult<Void> result;
		try {
			customerRepository.updateAddress(customer, address);
			result = ExecutionResultFactory.createUpdateOK();
		} catch (EpValidationException error) {
			result = exceptionTransformer.getExecutionResult(error);
		} catch (EpSystemException error) {
			result = ExecutionResultFactory.createServerError(error.getMessage());
		}
		return result;
	}

	@Override
	public ExecutionResult<String> create(final String userGuid, final AddressEntity addressEntity, final String scope) {

		final ExecutionResult<String> result;

		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(userGuid));
		CustomerAddress customerAddress = customerAddressTransformer.transformToDomain(addressEntity);
		String existingAddressGuid = checkForExistingCustomerAddress(customer, customerAddress);
		if (StringUtils.isEmpty(existingAddressGuid)) {
			CustomerAddress newAddress;
			try {
				newAddress = createCustomerAddress(customer, customerAddress, scope);
			} catch (EpValidationException error) {
				return exceptionTransformer.getExecutionResult(error);
			}
			result = ExecutionResultFactory.createCreateOKWithData(newAddress.getGuid(), false);
		} else {
			result = ExecutionResultFactory.createCreateOKWithData(existingAddressGuid, true);
		}

		return result;
	}

	@Override
	public ExecutionResult<Void> delete(final String customerGuid, final String addressGuid) {

		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(customerGuid),
				OnFailure.returnNotFound("no customer found"));
		CustomerAddress address = Assign.ifNotNull(customer.getAddressByGuid(addressGuid),
				OnFailure.returnNotFound(ADDRESS_NOT_FOUND));
		customer.removeAddress(address);

		return updateCustomer(customer);
	}

	private ExecutionResult<Void> updateCustomer(final Customer customer) {
		ExecutionResult<Void> result;
		try {
			customerRepository.update(customer);
			result = ExecutionResultFactory.createDeleteOK();
		} catch (EpSystemException error) {
			result = ExecutionResultFactory.createServerError(error.getMessage());
		}
		return result;
	}

	private void updateLastName(final Address address, final String lastName) {
		if (lastName != null) {
			address.setLastName(StringUtils.trimToNull(lastName));
		}
	}

	private void updateFirstName(final Address address, final String firstName) {
		if (firstName != null) {
			address.setFirstName(StringUtils.trimToNull(firstName));
		}
	}

	private void updateStreet2(final Address address, final String street2) {
		if (street2 != null) {
			address.setStreet2(StringUtils.trimToNull(street2));
		}
	}

	private void updateStreet1(final Address address, final String street1) {
		if (street1 != null) {
			address.setStreet1(StringUtils.trimToNull(street1));
		}
	}

	private void updatePostal(final Address address, final String zipOrPostalCode) {
		if (zipOrPostalCode != null) {
			address.setZipOrPostalCode(StringUtils.trimToNull(zipOrPostalCode));
		}
	}

	private void updateCity(final Address address, final String city) {
		if (city != null) {
			address.setCity(StringUtils.trimToNull(city));
		}
	}

	private void updateState(final Address address, final String subCountry) {
		if (subCountry != null) {
			address.setSubCountry(StringUtils.trimToNull(subCountry));
		}
	}

	private void updateCountry(final Address address, final String country) {
		if (country != null) {
			address.setCountry(StringUtils.trimToNull(country));
		}
	}

	private CustomerAddress getExistingAddressFromCustomer(final Customer customer, final String guid) {
		return customer.getAddressByGuid(guid);
	}

	private String checkForExistingCustomerAddress(final Customer customer, final CustomerAddress customerAddress) {
		String existingAddressGuid = StringUtils.EMPTY;

		for (CustomerAddress existingAddress : customer.getAddresses()) {
			if (existingAddress.equals(customerAddress)) {
				existingAddressGuid = existingAddress.getGuid();
				break;
			}
		}

		return existingAddressGuid;
	}

	private CustomerAddress createCustomerAddress(final Customer customer, final CustomerAddress address, final String scope) {
		Customer updatedCustomer = customerRepository.addAddress(customer, address);

		boolean updatePreferredBillingAddress = updatedCustomer.getPreferredBillingAddress() == null;
		if (updatePreferredBillingAddress) {
			updatedCustomer.setPreferredBillingAddress(address);
		}

		boolean updatePreferredShippingAddress = updatedCustomer.getPreferredShippingAddress() == null;
		if (updatePreferredShippingAddress) {
			updatedCustomer.setPreferredShippingAddress(address);
		}

		updatedCustomer = customerRepository.update(updatedCustomer);

		if (updatePreferredBillingAddress || updatePreferredShippingAddress) {
			cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(customer, address, scope, updatePreferredBillingAddress,
					updatePreferredShippingAddress);
		}

		return updatedCustomer.getAddressByGuid(address.getGuid());
	}
}
