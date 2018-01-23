/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.epcommerce.impl;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.exception.EpValidationException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.resource.addresses.integration.epcommerce.CartOrdersDefaultAddressPopulator;
import com.elasticpath.rest.resource.addresses.integration.epcommerce.transform.CustomerAddressTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorMessageTransformer;

/**
 * Tests the {@link AddressWriterStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public class AddressWriterStrategyImplTest {

	private static final String STORE_CODE = "STORE_CODE";
	private static final String FIRST_NAME = "FIRST_NAME";
	private static final String ALTERNATE_FIRST_NAME = "ALTERNATE_FIRST_NAME";
	private static final String LAST_NAME = "LAST_NAME";
	private static final String STREET_1 = "STREET 1";
	private static final String STREET_2 = "STREET 2";
	private static final String CITY = "CITY";
	private static final String SUBCOUNTRY = "SUBCOUNTRY";
	private static final String COUNTRY = "COUNTRY";
	private static final String ZIPCODE = "ZIPCODE";
	private static final String EXPECTED_RESOURCE_STATUS = "The resource status should be as expected.";
	private static final String FAILED_OPERATION = "This should result in failure.";
	private static final String DATA_AS_EXPECTED = "The result data should be as expected.";
	private static final String SUCCESSFUL_OPERATION = "This should be a successful operation.";
	private static final String ERROR = "Error";
	private static final String ADDRESS_GUID = "ADDRESS_GUID";
	private static final String ALTERNATE_ADDRESS_GUID = "ALTERNATE_ADDRESS_GUID";
	private static final String CUSTOMER_GUID = "CUSTOMER_GUID";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private CustomerAddressTransformer customerAddressTransformer;
	@Mock
	private CartOrdersDefaultAddressPopulator cartOrdersDefaultAddressPopulator;
	@Mock
	private ExceptionTransformer exceptionTransformer;
	@Mock
	private Customer customerMock;
	@Mock
	private StructuredErrorMessageTransformer structuredErrorMessageTransformer;
	@InjectMocks
	private AddressWriterStrategyImpl addressWriterStrategy;

	private static final List<StructuredErrorMessage> STRUCTURED_ERROR_MESSAGES = asList(mock(StructuredErrorMessage.class));

	/**
	 * Test update non existing address guid.
	 */
	@Test
	public void testUpdateWithNonExistingAddressGuid() {
		Customer customer = createMockCustomer(ADDRESS_GUID, null);
		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));
		AddressEntity addressEntity = createAddressEntity(ADDRESS_GUID, FIRST_NAME);

		addressWriterStrategy.update(addressEntity, CUSTOMER_GUID);
	}

	/**
	 * Test update with invalid address with address entity fields unpopulated.
	 */
	@Test
	public void testUpdateWithInvalidAddressWithAddressEntityFieldsUnpopulated() {
		CustomerAddress invalidCustomerAddress = createCustomerAddress(ADDRESS_GUID, FIRST_NAME);
		Customer customer = createMockCustomer(ADDRESS_GUID, invalidCustomerAddress);
		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);

		String message = "invalid address";
		when(customerRepository.updateAddress(customer, invalidCustomerAddress))
				.thenThrow(new EpValidationException(message, STRUCTURED_ERROR_MESSAGES));

		when(exceptionTransformer.getExecutionResult(any(EpValidationException.class))).thenReturn(ExecutionResultFactory
				.createBadRequestBody(message));

		AddressEntity addressEntity = createAddressEntity(ADDRESS_GUID, null);
		ExecutionResult result = addressWriterStrategy.update(addressEntity, CUSTOMER_GUID);

		assertEquals("ResoureStatus should be BAD_REQUEST_BODY", result.getResourceStatus(), ResourceStatus.BAD_REQUEST_BODY);
		assertNull(result.getData());
		assertTrue(result.getStructuredErrorMessages().isEmpty());
		assertEquals(result.getErrorMessage(), message);
	}

	/**
	 * Test update with valid address with address entity fields populated.
	 */
	@Test
	public void testUpdateWithValidAddressWithAddressEntityFieldsPopulated() {
		CustomerAddress customerAddress = createCustomerAddress(ADDRESS_GUID, FIRST_NAME);
		Customer customer = createMockCustomer(ADDRESS_GUID, customerAddress);

		Customer updatedCustomer = createMockCustomer(ADDRESS_GUID, customerAddress);
		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);
		when(customerRepository.updateAddress(customer, customerAddress)).thenReturn(customer);
		shouldAddAddressWithResult(customer, customerAddress, updatedCustomer);

		AddressDetailEntity addressDetailEntity = AddressDetailEntity.builder()
				.withStreetAddress(STREET_1)
				.withExtendedAddress(STREET_2)
				.withLocality(CITY)
				.withRegion(SUBCOUNTRY)
				.withCountryName(COUNTRY)
				.withPostalCode(ZIPCODE)
				.build();

		AddressEntity addressEntity = AddressEntity.builderFrom(createAddressEntity(ADDRESS_GUID, ALTERNATE_FIRST_NAME))
				.withAddress(addressDetailEntity)
				.build();

		ExecutionResult<Void> result = addressWriterStrategy.update(addressEntity, CUSTOMER_GUID);

		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
	}

	/**
	 * Test update with non-existent address.
	 */
	@Test
	public void testUpdateWithNonExistentAddress() {
		updateCustomerWithExceptionWithResourceStatus(new EpPersistenceException(ERROR), ResourceStatus.SERVER_ERROR);
	}

	/**
	 * Test update with customer group not set.
	 */
	@Test
	public void testUpdateWithCustomerGroupNotSet() {
		updateCustomerWithExceptionWithResourceStatus(new EpServiceException(ERROR), ResourceStatus.SERVER_ERROR);
	}

	private void updateCustomerWithExceptionWithResourceStatus(final EpSystemException exception, final ResourceStatus resourceStatus) {
		CustomerAddress customerAddress = createCustomerAddressWithAllFieldsPopulated(ADDRESS_GUID, FIRST_NAME);
		Customer customer = createMockCustomer(ADDRESS_GUID, customerAddress);
		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);
		when(customerRepository.updateAddress(customer, customerAddress)).thenReturn(customer);
		shouldUpdateAddressWithException(customer, customerAddress, exception);

		AddressEntity addressEntity = createAddressEntity(ADDRESS_GUID, FIRST_NAME);
		ExecutionResult<Void> result = addressWriterStrategy.update(addressEntity, CUSTOMER_GUID);

		assertTrue(FAILED_OPERATION, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, resourceStatus, result.getResourceStatus());
		assertEquals("This should have the expected error message.", ERROR, result.getErrorMessage());
	}

	/**
	 * Test create customer address.
	 */
	@Test
	public void testCreateAddressWithNoPreferredAddress() {
		Customer customer = createCustomer(CUSTOMER_GUID, Collections.<CustomerAddress>emptyList());

		CustomerAddress newCustomerAddress = createCustomerAddressWithAllFieldsPopulated(ADDRESS_GUID, FIRST_NAME);
		Customer updatedCustomer = createCustomer(CUSTOMER_GUID, Collections.singletonList(newCustomerAddress));

		AddressEntity newAddressEntity = createAddressEntity(ADDRESS_GUID, FIRST_NAME);

		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);
		shouldTransformToDomain(newAddressEntity, newCustomerAddress);
		shouldAddAddressWithResult(customer, newCustomerAddress, updatedCustomer);
		shouldUpdateWithResult(updatedCustomer);

		ExecutionResult<String> result = addressWriterStrategy.create(CUSTOMER_GUID, newAddressEntity, STORE_CODE);

		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.CREATE_OK, result.getResourceStatus());
		assertEquals(DATA_AS_EXPECTED, ADDRESS_GUID, result.getData());
		assertEquals("The preferred billing address should be set with the new address.",
				newCustomerAddress,
				updatedCustomer.getPreferredBillingAddress());
		assertEquals("The preferred shipping address should be set with the new address.",
				newCustomerAddress,
				updatedCustomer.getPreferredShippingAddress());
		verify(cartOrdersDefaultAddressPopulator, times(1)).updateAllCartOrdersAddresses(any(Customer.class), any(CustomerAddress.class),
				any(String.class), any(Boolean.class), any(Boolean.class));
	}

	/**
	 * Test create address with preferred shipping address set.
	 */
	@Test
	public void testCreateAddressWithPreferredShippingAddressSet() {
		CustomerAddress existingPreferredShippingAddress = createCustomerAddress(ADDRESS_GUID, FIRST_NAME);
		Customer customer = createCustomer(CUSTOMER_GUID, Collections.singletonList(existingPreferredShippingAddress));
		customer.setPreferredShippingAddress(existingPreferredShippingAddress);

		CustomerAddress newCustomerAddress = createCustomerAddress(ALTERNATE_ADDRESS_GUID, ALTERNATE_FIRST_NAME);
		Customer updatedCustomer = createCustomer(CUSTOMER_GUID, asList(existingPreferredShippingAddress, newCustomerAddress));
		updatedCustomer.setPreferredShippingAddress(existingPreferredShippingAddress);

		AddressEntity newAddressEntity = createAddressEntity(ADDRESS_GUID, ALTERNATE_FIRST_NAME);

		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);
		shouldTransformToDomain(newAddressEntity, newCustomerAddress);
		shouldAddAddressWithResult(customer, newCustomerAddress, updatedCustomer);
		shouldUpdateWithResult(updatedCustomer);

		ExecutionResult<String> result = addressWriterStrategy.create(CUSTOMER_GUID, newAddressEntity, STORE_CODE);

		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.CREATE_OK, result.getResourceStatus());
		assertEquals(DATA_AS_EXPECTED, ALTERNATE_ADDRESS_GUID, result.getData());
		assertEquals("The preferred billing address should be set with the new address.",
				newCustomerAddress,
				updatedCustomer.getPreferredBillingAddress());
		assertEquals("The preferred shipping address should not change on the updated customer.",
				existingPreferredShippingAddress,
				updatedCustomer.getPreferredShippingAddress());
		verify(cartOrdersDefaultAddressPopulator, times(1)).updateAllCartOrdersAddresses(any(Customer.class), any(CustomerAddress.class),
				any(String.class), any(Boolean.class), any(Boolean.class));
	}

	/**
	 * Test create address with preferred billing address set.
	 */
	@Test
	public void testCreateAddressWithPreferredBillingAddressSet() {
		CustomerAddress existingPreferredBillingAddress = createCustomerAddress(ADDRESS_GUID, FIRST_NAME);
		Customer customer = createCustomer(CUSTOMER_GUID, Collections.singletonList(existingPreferredBillingAddress));
		customer.setPreferredBillingAddress(existingPreferredBillingAddress);

		CustomerAddress newCustomerAddress = createCustomerAddress(ALTERNATE_ADDRESS_GUID, ALTERNATE_FIRST_NAME);
		Customer updatedCustomer = createCustomer(CUSTOMER_GUID, asList(existingPreferredBillingAddress, newCustomerAddress));
		updatedCustomer.setPreferredBillingAddress(existingPreferredBillingAddress);

		AddressEntity newAddressEntity = createAddressEntity(ALTERNATE_ADDRESS_GUID, ALTERNATE_FIRST_NAME);

		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);
		shouldTransformToDomain(newAddressEntity, newCustomerAddress);
		shouldAddAddressWithResult(customer, newCustomerAddress, updatedCustomer);
		shouldUpdateWithResult(updatedCustomer);

		ExecutionResult<String> result = addressWriterStrategy.create(CUSTOMER_GUID, newAddressEntity, STORE_CODE);

		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.CREATE_OK, result.getResourceStatus());
		assertEquals(DATA_AS_EXPECTED, ALTERNATE_ADDRESS_GUID, result.getData());
		assertEquals("The preferred billing address should not change on the updated customer.",
				existingPreferredBillingAddress,
				updatedCustomer.getPreferredBillingAddress());
		assertEquals("The preferred shipping address should be set with the new address.",
				newCustomerAddress,
				updatedCustomer.getPreferredShippingAddress());
		verify(cartOrdersDefaultAddressPopulator, times(1)).updateAllCartOrdersAddresses(any(Customer.class), any(CustomerAddress.class),
				any(String.class), any(Boolean.class), any(Boolean.class));
	}

	/**
	 * Test create address when customer not found.
	 */
	@Test
	public void testCreateAddressWhenCustomerNotFound() {
		when(customerRepository.findCustomerByGuid(CUSTOMER_GUID)).thenReturn(ExecutionResultFactory.<Customer>createNotFound());

		AddressEntity newAddressEntity = createAddressEntity(ADDRESS_GUID, FIRST_NAME);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		addressWriterStrategy.create(CUSTOMER_GUID, newAddressEntity, null);
	}

	/**
	 * Test create address when address validation fails.
	 */
	@Test
	public void testCreateAddressWhenAddressValidationFails() {

		Map<String, String> data = ImmutableMap.of("field-name", "firstname");
		Message message = Message.builder()
				.withId("message id")
				.withDebugMessage("firstname cannot be empty")
				.withData(data)
				.build();
		List<Message> messageList = asList(message);

		Customer customer = createCustomer(CUSTOMER_GUID, Collections.<CustomerAddress>emptyList());
		CustomerAddress newCustomerAddress = createCustomerAddress(ADDRESS_GUID, FIRST_NAME);

		AddressEntity newAddressEntity = createAddressEntity(ADDRESS_GUID, FIRST_NAME);

		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);
		shouldTransformToDomain(newAddressEntity, newCustomerAddress);

		when(customerRepository.addAddress(customer, newCustomerAddress)).thenReturn(customerMock);
		when(customerMock.getPreferredBillingAddress()).thenReturn(null);
		when(customerMock.getAddressByGuid(newCustomerAddress.getGuid())).thenReturn(newCustomerAddress);
		when(structuredErrorMessageTransformer.transform(STRUCTURED_ERROR_MESSAGES)).thenReturn(messageList);
		when(customerRepository.update(customerMock)).thenThrow(new EpValidationException("Validation Error", STRUCTURED_ERROR_MESSAGES));
		when(exceptionTransformer.getExecutionResult(any(EpValidationException.class))).thenReturn(ExecutionResultFactory
				.createBadRequestBodyWithMessages("Validation Error", messageList));

		ExecutionResult result = addressWriterStrategy.create(CUSTOMER_GUID, newAddressEntity, null);

		// Verify that we are receiving the proper data regarding the failure.
		assertEquals("ResourceStatus should be BAD_REQUEST_BODY", result.getResourceStatus(), ResourceStatus.BAD_REQUEST_BODY);
		assertTrue("Result should contain the defined message", result.getStructuredErrorMessages().contains(message));
		assertEquals("ErrorMessage should return 'Validation Error'", "Validation Error", result.getErrorMessage());
	}

	/**
	 * Test create address when created address equals existing address.
	 */
	@Test
	public void testCreateAddressWhenCreatedAddressEqualsExistingAddress() {
		CustomerAddress existingAddress = createCustomerAddress(ADDRESS_GUID, FIRST_NAME);
		Customer customer = createCustomer(CUSTOMER_GUID, Collections.singletonList(existingAddress));

		CustomerAddress newCustomerAddress = createCustomerAddress(ALTERNATE_ADDRESS_GUID, FIRST_NAME);

		AddressEntity newAddressEntity = createAddressEntity(ALTERNATE_ADDRESS_GUID, FIRST_NAME);

		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);
		shouldTransformToDomain(newAddressEntity, newCustomerAddress);
		when(customerRepository.updateAddress(customer, newCustomerAddress)).thenReturn(customer);

		ExecutionResult<String> result = addressWriterStrategy.create(CUSTOMER_GUID, newAddressEntity, null);

		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.READ_OK, result.getResourceStatus());
		assertEquals(DATA_AS_EXPECTED, ADDRESS_GUID, result.getData());
	}

	/**
	 * Test delete when customer not found.
	 */
	@Test
	public void testDeleteWhenCustomerNotFound() {
		when(customerRepository.findCustomerByGuid(CUSTOMER_GUID)).thenReturn(ExecutionResultFactory.<Customer>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		addressWriterStrategy.delete(CUSTOMER_GUID, ADDRESS_GUID);
	}

	/**
	 * Test delete when address not found.
	 */
	@Test
	public void testDeleteWhenAddressNotFound() {
		Customer customer = createCustomer(CUSTOMER_GUID, Collections.<CustomerAddress>emptyList());

		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		addressWriterStrategy.delete(CUSTOMER_GUID, ADDRESS_GUID);
	}

	/**
	 * Test delete when address found.
	 */
	@Test
	public void testDeleteWhenAddressFound() {
		CustomerAddress existingAddress = createCustomerAddress(ADDRESS_GUID, FIRST_NAME);
		List<CustomerAddress> addresses = new ArrayList<>();
		addresses.add(existingAddress);
		Customer customer = createCustomer(CUSTOMER_GUID, addresses);

		Customer updatedCustomer = createCustomer(CUSTOMER_GUID, Collections.<CustomerAddress>emptyList());

		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);
		shouldUpdateWithResult(updatedCustomer);

		ExecutionResult<Void> result = addressWriterStrategy.delete(CUSTOMER_GUID, ADDRESS_GUID);

		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.DELETE_OK, result.getResourceStatus());
	}

	/**
	 * Test delete with ep persistence exception.
	 */
	@Test
	public void testDeleteWithEpPersistenceException() {
		deleteWithExceptionWithResourceStatus(new EpPersistenceException("exception"), ResourceStatus.SERVER_ERROR);
	}

	/**
	 * Test delete with ep service exception.
	 */
	@Test
	public void testDeleteWithEpServiceException() {
		deleteWithExceptionWithResourceStatus(new EpServiceException("exception"), ResourceStatus.SERVER_ERROR);
	}

	/**
	 * Test delete when address found.
	 */
	private void deleteWithExceptionWithResourceStatus(final EpSystemException exception, final ResourceStatus resourceStatus) {
		CustomerAddress existingAddress = createCustomerAddress(ADDRESS_GUID, FIRST_NAME);
		List<CustomerAddress> addresses = new ArrayList<>();
		addresses.add(existingAddress);
		Customer customer = createCustomer(CUSTOMER_GUID, addresses);

		Customer updatedCustomer = createCustomer(CUSTOMER_GUID, Collections.<CustomerAddress>emptyList());

		shouldFindByGuidWithResult(CUSTOMER_GUID, customer);
		shouldUpdateWithException(updatedCustomer, exception);

		ExecutionResult<Void> result = addressWriterStrategy.delete(CUSTOMER_GUID, ADDRESS_GUID);

		assertTrue(FAILED_OPERATION, result.isFailure());
		assertEquals(EXPECTED_RESOURCE_STATUS, resourceStatus, result.getResourceStatus());
	}

	private void shouldFindByGuidWithResult(final String customerGuid, final Customer customer) {
		when(customerRepository.findCustomerByGuid(customerGuid)).thenReturn(ExecutionResultFactory.createReadOK(customer));
	}

	private void shouldAddAddressWithResult(final Customer customer, final CustomerAddress customerAddress, final Customer result) {
		when(customerRepository.addAddress(customer, customerAddress)).thenReturn(result);
	}

	private void shouldUpdateAddressWithException(final Customer customer,
			final CustomerAddress customerAddress,
			final EpSystemException exception) {

		when(customerRepository.updateAddress(customer, customerAddress)).thenThrow(exception);
	}

	private void shouldUpdateWithResult(final Customer updatedCustomer) {
		when(customerRepository.update(updatedCustomer)).thenReturn(updatedCustomer);
	}

	private void shouldUpdateWithException(final Customer customer, final EpSystemException exception) {
		when(customerRepository.update(customer)).thenThrow(exception);
	}

	private void shouldTransformToDomain(final AddressEntity addressEntity, final CustomerAddress customerAddress) {
		when(customerAddressTransformer.transformToDomain(addressEntity)).thenReturn(customerAddress);
	}

	private Customer createMockCustomer(final String addressGuid, final CustomerAddress customerAddress) {
		final Customer customer = mock(Customer.class);
		when(customer.getAddressByGuid(addressGuid)).thenReturn(customerAddress);
		return customer;
	}

	private Customer createCustomer(final String customerGuid, final List<CustomerAddress> addresses) {
		Customer customer = new CustomerImpl();
		customer.setGuid(customerGuid);
		customer.setAddresses(addresses);
		return customer;
	}

	/**
	 * Creates the address entity.<br>
	 * First name is included to stress that the address equals method is not driven form the address guid.
	 *
	 * @param addressGuid the address guid
	 * @param firstName   the first name
	 * @return the address entity
	 */
	private AddressEntity createAddressEntity(final String addressGuid, final String firstName) {
		NameEntity nameEntity = NameEntity.builder().withGivenName(firstName).build();
		AddressDetailEntity addressEntity = AddressDetailEntity.builder().build();
		return AddressEntity.builder()
				.withAddressId(addressGuid)
				.withName(nameEntity)
				.withAddress(addressEntity)
				.build();
	}

	/**
	 * Creates the customer address with guid and first name.<br>
	 * First name is included since the address.equals method does not determine equality based on guid.<br>
	 * Address guid is provided to identify different addresses.
	 *
	 * @param addressGuid the address guid
	 * @param firstName   the first name
	 * @return the customer address
	 */
	private CustomerAddress createCustomerAddress(final String addressGuid, final String firstName) {
		CustomerAddress customerAddress = new CustomerAddressImpl();
		customerAddress.setGuid(addressGuid);
		customerAddress.setFirstName(firstName);
		return customerAddress;
	}

	/**
	 * Creates the customer address with all fields populated.<br>
	 * First name is included since the address.equals method does not determine equality based on guid.<br>
	 * Address guid is provided to identify different addresses.
	 *
	 * @param addressGuid the address guid
	 * @param firstName   the first name
	 * @return the customer address
	 */
	private CustomerAddress createCustomerAddressWithAllFieldsPopulated(final String addressGuid, final String firstName) {
		CustomerAddress customerAddress = createCustomerAddress(addressGuid, firstName);
		customerAddress.setLastName(LAST_NAME);
		customerAddress.setStreet1(STREET_1);
		customerAddress.setStreet2(STREET_2);
		customerAddress.setCity(CITY);
		customerAddress.setSubCountry(SUBCOUNTRY);
		customerAddress.setCountry(COUNTRY);
		customerAddress.setZipOrPostalCode(ZIPCODE);
		return customerAddress;
	}
}
