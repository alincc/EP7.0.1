/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.validator.impl;

import java.util.Objects;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.definitions.validator.Validator;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * The Class KeywordsFormValidator.
 */
@Singleton
@Named("keywordsFormValidator")
public final class KeywordsFormValidator implements Validator<SearchKeywordsEntity> {

	/**
	 * The max length for keyword search keywords.
	 */
	public static final int KEYWORDS_MAX_LENGTH = 500;
	private static final String KEYWORDS_TOO_LONG = String.format("Keywords field is too long, the maximum length is %s.",
			KEYWORDS_MAX_LENGTH);
	private static final String MISSING_FIELD_IN_REQUEST_BODY = "Keywords field is missing a value.";
	private static final Range<Integer> RANGE = Range.between(1, Integer.MAX_VALUE);
	private static final String PAGESIZE_IS_OUTSIDE_THIS_RANGE = "Page Size is outside this range: %s";

	@Override
	public ExecutionResult<Void> validate(final SearchKeywordsEntity searchKeywordsEntity) {

		String keywords = searchKeywordsEntity.getKeywords();
		Ensure.isTrue(StringUtils.isNotEmpty(keywords),	OnFailure.returnBadRequestBody(MISSING_FIELD_IN_REQUEST_BODY));
		Ensure.isTrue(StringUtils.length(keywords) <= KEYWORDS_MAX_LENGTH, OnFailure.returnBadRequestBody(KEYWORDS_TOO_LONG));

		Integer pageSize = searchKeywordsEntity.getPageSize();
		if (pageSize == null) {
			//May not be an Integer; if it is blank, that is the same as non-existent
			Object pageSizeObject = ResourceStateUtil.getRawProperty(searchKeywordsEntity, "page-size");
			Ensure.isTrue(StringUtils.isBlank(Objects.toString(pageSizeObject, StringUtils.EMPTY)),
					OnFailure.returnBadRequestBody("Page size not an integer"));
		} else {
			Ensure.isTrue(RANGE.contains(pageSize), OnFailure.returnBadRequestBody(PAGESIZE_IS_OUTSIDE_THIS_RANGE, RANGE));
		}

		return ExecutionResultFactory.createUpdateOK();
	}
}
