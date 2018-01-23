/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.validator.impl;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.definitions.validator.Validator;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;

/**
 * Validator for an updated {@link AddressEntity}.
 */
@Singleton
@Named("addressValidator")
public class AddressValidator implements Validator<AddressEntity> {

	private static final String NO_VALID_ADDRESS_FIELDS = "No valid address fields specified.";

	@Override
	public ExecutionResult<Void> validate(final AddressEntity addressEntity) {
		Ensure.notNull(addressEntity, OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		Ensure.isTrue(representationHasFields(addressEntity), OnFailure.returnBadRequestBody(NO_VALID_ADDRESS_FIELDS));
		return ExecutionResultFactory.createUpdateOK();
	}

	private boolean representationHasFields(final AddressEntity address) {
		AddressDetailEntity addressDetailsEntity = address.getAddress();
		NameEntity nameEntity = address.getName();

		return nameEntityHasData(nameEntity)
				|| addressEntityHasData(addressDetailsEntity);
	}

	private boolean nameEntityHasData(final NameEntity nameEntity) {
		return nameEntity != null
				&& (
				nameEntity.getFamilyName() != null
						|| nameEntity.getGivenName() != null
		);
	}

	private boolean addressEntityHasData(final AddressDetailEntity addressDetailsEntity) {
		return addressDetailsEntity != null
				&& (
				addressDetailsEntity.getStreetAddress() != null
						|| addressDetailsEntity.getExtendedAddress() != null
						|| addressDetailsEntity.getLocality() != null
						|| addressDetailsEntity.getRegion() != null
						|| addressDetailsEntity.getCountryName() != null
						|| addressDetailsEntity.getPostalCode() != null
		);
	}
}
