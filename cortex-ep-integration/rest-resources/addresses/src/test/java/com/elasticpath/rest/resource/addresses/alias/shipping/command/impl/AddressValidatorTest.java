/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.alias.shipping.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.resource.addresses.validator.impl.AddressValidator;

/**
 * Contains tests for {@link com.elasticpath.rest.resource.addresses.validator.impl.AddressValidator}.
 */
public final class AddressValidatorTest {
	private static final String NO_VALID_ADDRESS_FIELDS = "No valid address fields specified.";
	private static final String EXPECTED_EXECUTION_RESULT = "The execution result should be the same as expected.";

	private static final String STREET_ADDRESS = "STREET_ADDRESS";
	private static final String EXTENDED_ADDRESS = "EXTENDED_ADDRESS";
	private static final String LOCALITY = "LOCALITY";
	private static final String REGION = "REGION";
	private static final String COUNTRY_NAME = "COUNTRY_NAME";
	private static final String POSTAL_CODE = "POSTAL_CODE";
	private static final String FIRST_NAME = "FIRST_NAME";
	private static final String LAST_NAME = "LAST_NAME";

	private final AddressValidator validator = new AddressValidator();

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	/**
	 * Test validate given null for the representation argument.
	 */
	@Test
	public void testValidateGivenNull() {
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		validator.validate(null);
	}

	/**
	 * Test validate when address representation not populated.
	 */
	@Test
	public void testValidateWhenAddressRepresentationNotPopulated() {
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, null, null, null, null, null);
		AddressEntity addressEntity = createAddressEntity(null, addressDetailsEntity);
		validator.validate(addressEntity);
	}

	/**
	 * Test validation on address entity with no fields populated within the address representation.
	 */
	@Test
	public void testValidationOnAddressEntityWithNoFieldsPopulatedWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, null, null, null, null, null);

		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		testEntityFieldPopulationWithResult(null, addressDetailsEntity, ExecutionResultFactory.<Void>createBadRequestBody(NO_VALID_ADDRESS_FIELDS));
	}

	/**
	 * Test validation of street address on address entity within the address representation.
	 */
	@Test
	public void testValidationOfStreetAddressOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(STREET_ADDRESS, null, null, null, null, null);
		testEntityFieldPopulationWithResult(null, addressDetailsEntity, ExecutionResultFactory.<Void>createUpdateOK());
	}

	/**
	 * Test validation of extended address on address entity within the address representation.
	 */
	@Test
	public void testValidationOfExtendedAddressOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, EXTENDED_ADDRESS, null, null, null, null);
		testEntityFieldPopulationWithResult(null, addressDetailsEntity, ExecutionResultFactory.<Void>createUpdateOK());
	}

	/**
	 * Test validation of locality on address entity within the address representation.
	 */
	@Test
	public void testValidationOfLocalityOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, null, LOCALITY, null, null, null);
		testEntityFieldPopulationWithResult(null, addressDetailsEntity, ExecutionResultFactory.<Void>createUpdateOK());
	}

	/**
	 * Test validation of region on address entity within the address representation.
	 */
	@Test
	public void testValidationOfRegionOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, null, null, REGION, null, null);
		testEntityFieldPopulationWithResult(null, addressDetailsEntity, ExecutionResultFactory.<Void>createUpdateOK());
	}

	/**
	 * Test validation of country name on address entity within the address representation.
	 */
	@Test
	public void testValidationOfCountryNameOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, null, null, null, COUNTRY_NAME, null);
		testEntityFieldPopulationWithResult(null, addressDetailsEntity, ExecutionResultFactory.<Void>createUpdateOK());
	}

	/**
	 * Test validation of postal code on address entity within the address representation.
	 */
	@Test
	public void testValidationOfPostalCodeOnAddressEntityWithinAddressRepresentation() {
		AddressDetailEntity addressDetailsEntity = createAddressDetailsEntity(null, null, null, null, null, POSTAL_CODE);
		testEntityFieldPopulationWithResult(null, addressDetailsEntity, ExecutionResultFactory.<Void>createUpdateOK());
	}

	/**
	 * Test validation on name entity with no fields populated within the address representation.
	 */
	@Test
	public void testValidationOnNameEntityWithNoFieldsPopulatedWithinAddressRepresentation() {
		NameEntity nameEntity = createNameEntity(null, null);
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		testEntityFieldPopulationWithResult(nameEntity, null, ExecutionResultFactory.<Void>createBadRequestBody(NO_VALID_ADDRESS_FIELDS));
	}

	/**
	 * Test validation of given name on name entity within the address representation.
	 */
	@Test
	public void testValidationOfGivenNameOnNameEntityWithinAddressRepresentation() {
		NameEntity nameEntity = createNameEntity(FIRST_NAME, null);
		testEntityFieldPopulationWithResult(nameEntity, null, ExecutionResultFactory.<Void>createUpdateOK());
	}

	/**
	 * Test validation of family name on name entity within the address representation.
	 */
	@Test
	public void testValidationOfFamilyNameOnNameEntityWithinAddressRepresentation() {
		NameEntity nameEntity = createNameEntity(null, LAST_NAME);
		testEntityFieldPopulationWithResult(nameEntity, null, ExecutionResultFactory.<Void>createUpdateOK());
	}

	private void testEntityFieldPopulationWithResult(final NameEntity nameEntity,
			final AddressDetailEntity addressDetailsEntity,
			final ExecutionResult<Void> expectedResult) {

		AddressEntity addressEntity = createAddressEntity(nameEntity, addressDetailsEntity);

		ExecutionResult<Void> result = validator.validate(addressEntity);

		assertEquals(EXPECTED_EXECUTION_RESULT, expectedResult, result);
	}

	private AddressEntity createAddressEntity(final NameEntity name, final AddressDetailEntity addressDetailsEntity) {
		return AddressEntity.builder().withName(name).withAddress(addressDetailsEntity).build();
	}

	private AddressDetailEntity createAddressDetailsEntity(final String streetAddress,
															final String extendedAddress,
															final String locality,
															final String region,
															final String countryName,
															final String postalCode) {

		return AddressDetailEntity.builder()
				.withStreetAddress(streetAddress)
				.withExtendedAddress(extendedAddress)
				.withLocality(locality)
				.withRegion(region)
				.withCountryName(countryName)
				.withPostalCode(postalCode)
				.build();
	}

	private NameEntity createNameEntity(final String givenName, final String familyName) {
		return NameEntity.builder()
				.withGivenName(givenName)
				.withFamilyName(familyName)
				.build();
	}
}
