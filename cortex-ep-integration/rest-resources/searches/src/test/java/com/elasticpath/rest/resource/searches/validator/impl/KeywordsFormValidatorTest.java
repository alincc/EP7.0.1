/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.validator.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertTrue;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;

/**
 * Test {@link KeywordsFormValidator}.
 */
public final class KeywordsFormValidatorTest {

	private static final String VALID_KEYWORD = "valid_keyword";
	private static final String TOO_LONG_KEYWORDS = StringUtils.leftPad("", 501, "ThisIsLong");

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final KeywordsFormValidator keywordsFormValidator = new KeywordsFormValidator();


	@Test
	public void testValidKeywordsAndPagination() {
		SearchKeywordsEntity searchKeywordsEntity = SearchKeywordsEntity.builder()
						.withKeywords(VALID_KEYWORD)
						.withPageSize(1)
						.build();

		ExecutionResult<Void> result = keywordsFormValidator.validate(searchKeywordsEntity);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testEmptyKeyword() {
		SearchKeywordsEntity searchKeywordsEntity = SearchKeywordsEntity.builder()
						.withPageSize(1)
				.build();
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		keywordsFormValidator.validate(searchKeywordsEntity);
	}

	@Test
	public void testEmptyPagination() {
		SearchKeywordsEntity searchKeywordsEntity = SearchKeywordsEntity.builder()
						.withPageSize(1)
						.build();
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		keywordsFormValidator.validate(searchKeywordsEntity);
	}

	@Test
	public void testTooLongKeywords() {
		SearchKeywordsEntity searchKeywordsEntity = SearchKeywordsEntity.builder()
						.withKeywords(TOO_LONG_KEYWORDS)
						.withPageSize(1)
				.build();
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		keywordsFormValidator.validate(searchKeywordsEntity);
	}

	@Test
	public void testPageSizeOutOfRange() {
		SearchKeywordsEntity searchKeywordsEntity = SearchKeywordsEntity.builder()
						.withKeywords(VALID_KEYWORD)
						.withPageSize(0)
						.build();
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		keywordsFormValidator.validate(searchKeywordsEntity);
	}


}
