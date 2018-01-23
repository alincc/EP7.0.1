/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.validator.impl;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.definitions.validator.Validator;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;

/**
 * The Class ItemCodeFormValidator.
 */
@Singleton
@Named("itemCodeFormValidator")
public final class ItemCodeFormValidator implements Validator<CodeEntity> {

	/**
	 * The max length for item code search.
	 */
	public static final int CODE_MAX_LENGTH = 500;
	private static final String CODE_TOO_LONG = String.format("Code field is too long, the maximum length is %s.",
			CODE_MAX_LENGTH);
	private static final String MISSING_FIELD_IN_REQUEST_BODY = "Code field is missing a value.";

	@Override
	public ExecutionResult<Void> validate(final CodeEntity codeEntity) {
		Ensure.notNull(codeEntity, OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		String code = codeEntity.getCode();
		Ensure.isTrue(StringUtils.isNotEmpty(code), OnFailure.returnBadRequestBody(MISSING_FIELD_IN_REQUEST_BODY));
		Ensure.isTrue(StringUtils.length(code) <= CODE_MAX_LENGTH, OnFailure.returnBadRequestBody(CODE_TOO_LONG));

		return ExecutionResultFactory.createUpdateOK();
	}
}
