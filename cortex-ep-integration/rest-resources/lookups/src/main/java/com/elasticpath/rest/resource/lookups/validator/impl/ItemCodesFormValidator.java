/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.validator.impl;

import static java.lang.String.format;

import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.lookups.CodesEntity;
import com.elasticpath.rest.definitions.validator.Validator;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;

/**
 * This class validates the codes form.
 */
@Singleton
@Named("itemCodesFormValidator")
public final class ItemCodesFormValidator implements Validator<CodesEntity> {

	/**
	 * The max length for code list chars.
	 */
	public static final int CODES_MAX_LENGTH = 5000;

	private static final String CODE_LIST_TOO_LONG = format("Code list is too long; must contain less than %s characters.", CODES_MAX_LENGTH);
	private static final String MISSING_FIELD_IN_REQUEST_BODY = "Codes field is missing a value.";

	@Override
	public ExecutionResult<Void> validate(final CodesEntity codesEntity) {
		Ensure.notNull(codesEntity, OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		Collection<String> codes = codesEntity.getCodes();
		Assign.ifNotEmpty(codes, OnFailure.returnBadRequestBody(MISSING_FIELD_IN_REQUEST_BODY));
		int length = 0;
		for (String code : codes) {
			length += code.length();
		}
		Ensure.isTrue(length < CODES_MAX_LENGTH, OnFailure.returnBadRequestBody(CODE_LIST_TOO_LONG));

		return ExecutionResultFactory.createUpdateOK();
	}
}
