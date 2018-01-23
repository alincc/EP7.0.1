/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.resource.addresses.integration.epcommerce.transform.CustomerAddressTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link AddressLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressLookupStrategyImplTest {

	private static final String ADDRESS_GUID_2 = "address_guid_2";
	private static final String ADDRESS_GUID = "address_guid";
	private static final String USER_GUID = "user_guid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CustomerRepository mockCustomerRepository;

	@Mock
	private CustomerAddressTransformer mockCustomerAddressTransformer;

	@InjectMocks
	private AddressLookupStrategyImpl strategy;

	/**
	 * Test find single address.
	 */
	@Test
	public void testFindAddress() {
		Customer customer = new CustomerImpl();
		CustomerAddress customerAddress = new CustomerAddressImpl();
		customerAddress.setGuid(ADDRESS_GUID);
		customer.setAddresses(Collections.singletonList(customerAddress));

		AddressEntity addressEntity = AddressEntity.builder()
				.withAddressId(ADDRESS_GUID)
				.build();

		when(mockCustomerRepository.findCustomerByGuid(USER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(customer));
		when(mockCustomerAddressTransformer.transformToEntity(customerAddress)).thenReturn(addressEntity);

		ExecutionResult<AddressEntity> findResult = strategy.find(null, USER_GUID, ADDRESS_GUID);
		assertTrue(findResult.isSuccessful());
		AddressEntity resultDto = findResult.getData();
		assertEquals(ADDRESS_GUID, resultDto.getAddressId());
	}

	/**
	 * Test find address guids for user.
	 */
	@Test
	public void testFindAddressGuidsForUser() {
		Customer customer = new CustomerImpl();
		CustomerAddress customerAddress = new CustomerAddressImpl();
		customerAddress.setGuid(ADDRESS_GUID);
		CustomerAddress customerAddress2 = new CustomerAddressImpl();
		customerAddress2.setGuid(ADDRESS_GUID_2);

		customer.setAddresses(Arrays.asList(customerAddress, customerAddress2));

		when(mockCustomerRepository.findCustomerByGuid(USER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(customer));

		ExecutionResult<Collection<String>> findResult = strategy.findIdsByUserId(null, USER_GUID);
		assertTrue(findResult.isSuccessful());
		Collection<String> addressGuids = findResult.getData();
		assertTrue(CollectionUtil.containsOnly(Arrays.asList(ADDRESS_GUID, ADDRESS_GUID_2), addressGuids));
	}

	/**
	 * Tests find address guids for user when customer not found.
	 */
	@Test
	public void testFindAddressGuidsForUserWhenCustomerNotFound() {
		when(mockCustomerRepository.findCustomerByGuid(USER_GUID)).thenReturn(ExecutionResultFactory.<Customer>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.findIdsByUserId(null, USER_GUID);
	}

	/**
	 * Tests find address when customer not found.
	 */
	@Test
	public void testFindAddressWhenCustomerNotFound() {
		when(mockCustomerRepository.findCustomerByGuid(USER_GUID)).thenReturn(ExecutionResultFactory.<Customer>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.find(null, USER_GUID, ADDRESS_GUID);
	}
}
