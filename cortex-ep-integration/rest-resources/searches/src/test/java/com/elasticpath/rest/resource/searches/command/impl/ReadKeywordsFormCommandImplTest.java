/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.searches.TestSearchesResourceConstants;
import com.elasticpath.rest.resource.searches.command.ReadKeywordsFormCommand;
import com.elasticpath.rest.resource.searches.keywords.Items;
import com.elasticpath.rest.resource.searches.keywords.Keywords;
import com.elasticpath.rest.resource.searches.keywords.lookup.KeywordsSearchesLookup;
import com.elasticpath.rest.resource.searches.rel.SearchesResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test for {@link ReadKeywordsFormCommandImpl}.
 */
public final class ReadKeywordsFormCommandImplTest {

	private static final String INVALID_PAGE_SIZE = "INVALID PAGE SIZE";
	private static final int TEST_PAGE_SIZE = 20;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private KeywordsSearchesLookup mockKeywordSearchLookup;


	/**
	 * Test execute.
	 */
	@Test
	public void testExecute() {
		ReadKeywordsFormCommand readKeywordsFormCommand = createReadKeywordsFormCommand(TestSearchesResourceConstants.RESOURCE_NAME,
				TestSearchesResourceConstants.TEST_SCOPE);

		context.checking(new Expectations() {
			{
				allowing(mockKeywordSearchLookup).getDefaultPageSize(TestSearchesResourceConstants.TEST_SCOPE);
				will(returnValue(ExecutionResultFactory.createReadOK(TEST_PAGE_SIZE)));
			}
		});

		ExecutionResult<ResourceState<SearchKeywordsEntity>> result = readKeywordsFormCommand.execute();

		assertTrue(result.isSuccessful());

		ResourceState<SearchKeywordsEntity> keywordsFormRepresentation = result.getData();
		String expectedBaseUri = URIUtil.format(TestSearchesResourceConstants.RESOURCE_NAME, TestSearchesResourceConstants.TEST_SCOPE,
				Keywords.URI_PART);
		String expectedUri = URIUtil.format(expectedBaseUri, Form.URI_PART);
		String expectedActionUri = URIUtil.format(expectedBaseUri, Items.URI_PART);

		ResourceLink expectedActionLink = ResourceLinkFactory.createUriRel(expectedActionUri, SearchesResourceRels.ITEM_KEYWORDS_SEARCH_ACTION_REL);

		assertResourceState(keywordsFormRepresentation)
			.self(SelfFactory.createSelf(expectedUri))
			.linkCount(1)
			.containsLink(expectedActionLink);
	}

	/**
	 * Test invalid page size.
	 */
	@Test
	public void testInvalidPageSize() {
		ReadKeywordsFormCommand readKeywordsFormCommand = createReadKeywordsFormCommand(TestSearchesResourceConstants.RESOURCE_NAME,
				TestSearchesResourceConstants.TEST_SCOPE);

		context.checking(new Expectations() {
			{
				allowing(mockKeywordSearchLookup).getDefaultPageSize(TestSearchesResourceConstants.TEST_SCOPE);
				will(returnValue(ExecutionResultFactory.createServerError(INVALID_PAGE_SIZE)));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		ExecutionResult<ResourceState<SearchKeywordsEntity>> result = readKeywordsFormCommand.execute();
		assertEquals(INVALID_PAGE_SIZE, result.getErrorMessage());
	}

	private ReadKeywordsFormCommand createReadKeywordsFormCommand(final String rootResourceName, final String scope) {
		ReadKeywordsFormCommandImpl readKeywordsFormCommand = new ReadKeywordsFormCommandImpl(rootResourceName, mockKeywordSearchLookup);
		ReadKeywordsFormCommand.Builder builder = new ReadKeywordsFormCommandImpl.BuilderImpl(readKeywordsFormCommand)
				.setScope(scope);
		return builder.build();
	}
}
