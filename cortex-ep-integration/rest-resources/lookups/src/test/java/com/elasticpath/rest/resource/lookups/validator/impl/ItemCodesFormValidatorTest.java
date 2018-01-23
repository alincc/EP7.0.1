/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.validator.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.commons.lang3.RandomStringUtils;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.lookups.CodesEntity;

public class ItemCodesFormValidatorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	ItemCodesFormValidator classUnderTest = new ItemCodesFormValidator();

	@Test
	public void testValidateSimpleList() throws Exception {
		CodesEntity codes = CodesEntity.builder()
			.withCodes(Arrays.asList("1", "2", "3"))
			.build();

		ExecutionResult<Void> actual = classUnderTest.validate(codes);

		assertTrue(actual.isSuccessful());
	}

	@Test
	@SuppressWarnings("checkstyle:magicnumber")
	public void testValidateTooLongList() throws Exception {
		Collection<String> codeStrings = new ArrayList<>();
		for (int i = 0; i < 5000; i++) {
			codeStrings.add(RandomStringUtils.random(10));
		}

		CodesEntity codes = CodesEntity.builder()
			.withCodes(codeStrings)
			.build();

		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		classUnderTest.validate(codes);
	}

	@Test
	public void testValidateEmptyList() throws Exception {
		CodesEntity codes = CodesEntity.builder()
			.withCodes(Collections.<String>emptyList())
			.build();

		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		classUnderTest.validate(codes);
	}

	@Test
	public void testValidateNullList() throws Exception {
		CodesEntity codes = CodesEntity.builder()
			.build();

		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		classUnderTest.validate(codes);
	}
}