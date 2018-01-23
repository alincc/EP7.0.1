/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.command.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.PaginationEntity;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.searches.TestSearchesResourceConstants;
import com.elasticpath.rest.resource.searches.command.CreateSearchUriCommand;
import com.elasticpath.rest.resource.searches.keywords.Items;
import com.elasticpath.rest.resource.searches.keywords.Keywords;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * The Class CreateSearchUriCommandImplTest.
 */
public final class CreateSearchUriCommandImplTest {

	private static final int PAGE_SIZE = 10;
	private static final String ENCODE_KEYWORDS_TESTS = "encodeKeywordsTests";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	/**
	 * Test execute.
	 */
	@Test
	public void testExecute() {
		SearchKeywordsEntity searchForm = SearchKeywordsEntity.builder()
						.withKeywords(ENCODE_KEYWORDS_TESTS)
						.withPageSize(PAGE_SIZE)
						.build();
		Map<String, String> searchFieldValuePairs = new TreeMap<>();
		searchFieldValuePairs.put(SearchKeywordsEntity.KEYWORDS_PROPERTY, ENCODE_KEYWORDS_TESTS);
		searchFieldValuePairs.put(PaginationEntity.PAGE_SIZE_PROPERTY, String.valueOf(PAGE_SIZE));
		CreateSearchUriCommand createSearchUriCommand = createSearchUriCommand(TestSearchesResourceConstants.TEST_SCOPE,
				Items.URI_PART, searchForm);

		ExecutionResult<ResourceState<ResourceEntity>> result = createSearchUriCommand.execute();

		assertTrue(result.isSuccessful());
		String expectedSearchUri = URIUtil.format(TestSearchesResourceConstants.RESOURCE_NAME, TestSearchesResourceConstants.TEST_SCOPE,
				Keywords.URI_PART, Items.URI_PART,
				CompositeIdUtil.encodeCompositeId(searchFieldValuePairs));
		Self expectedSearchSelf = SelfFactory.createSelf(expectedSearchUri);
		assertEquals("The encoded search query should match", expectedSearchSelf, result.getData().getSelf());
	}


	/**
	 * Test execute with no page size.
	 */
	@Test
	public void testExecuteWithNoPageSizeSet() {
		SearchKeywordsEntity searchForm = SearchKeywordsEntity.builder()
						.withKeywords(ENCODE_KEYWORDS_TESTS)
						.build();
		Map<String, String> searchFieldValuePairs = new TreeMap<>();
		searchFieldValuePairs.put(SearchKeywordsEntity.KEYWORDS_PROPERTY, ENCODE_KEYWORDS_TESTS);
		CreateSearchUriCommand createSearchUriCommand = createSearchUriCommand(TestSearchesResourceConstants.TEST_SCOPE, Items.URI_PART, searchForm);

		ExecutionResult<ResourceState<ResourceEntity>> result = createSearchUriCommand.execute();

		assertTrue(result.isSuccessful());
		String expectedSearchUri = URIUtil.format(TestSearchesResourceConstants.RESOURCE_NAME, TestSearchesResourceConstants.TEST_SCOPE,
				Keywords.URI_PART, Items.URI_PART,
				CompositeIdUtil.encodeCompositeId(searchFieldValuePairs));
		Self expectedSearchSelf = SelfFactory.createSelf(expectedSearchUri);
		assertEquals("The encoded search query should match", expectedSearchSelf, result.getData().getSelf());
	}


	private CreateSearchUriCommand createSearchUriCommand(final String scope, final String searchPath,
			final SearchKeywordsEntity searchForm) {

		CreateSearchUriCommandImpl createSearchUriCommand = new CreateSearchUriCommandImpl(TestSearchesResourceConstants.RESOURCE_NAME
		);
		CreateSearchUriCommand.Builder builder = new CreateSearchUriCommandImpl.BuilderImpl(createSearchUriCommand)
				.setScope(scope)
				.setSearchPath(searchPath)
				.setSearchForm(searchForm);

		return builder.build();
	}
}
