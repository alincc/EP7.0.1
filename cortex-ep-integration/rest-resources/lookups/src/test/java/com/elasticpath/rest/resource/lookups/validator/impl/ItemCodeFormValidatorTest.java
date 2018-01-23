/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.validator.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.lookups.CodeEntity;

/**
 * Tests {@link ItemCodeFormValidator}.
 */
public final class ItemCodeFormValidatorTest  {

	private static final String CODE = "AllWorkAndNoPlayMakeHomerSomethingSomethingAllWorkAndNoPlayMakeHomerSomethingSomething";
	private static final String TOO_LONG = CODE + CODE + CODE + CODE + CODE + CODE;
	private final ItemCodeFormValidator itemCodeFormValidator = new ItemCodeFormValidator();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void validateGoodRequest() {
		CodeEntity codeEntity = CodeEntity.builder().withCode(CODE).build();

		ExecutionResult result = itemCodeFormValidator.validate(codeEntity);

		assertExecutionResult(result)
				.isSuccessful();
	}

	@Test
	public void validateNullRepresentation() {
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		itemCodeFormValidator.validate(null);

	}

	@Test
	public void validateRepresentationWithOverLengthCode() {
		CodeEntity codeEntity = CodeEntity.builder().withCode(TOO_LONG).build();
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		itemCodeFormValidator.validate(codeEntity);
	}
}
