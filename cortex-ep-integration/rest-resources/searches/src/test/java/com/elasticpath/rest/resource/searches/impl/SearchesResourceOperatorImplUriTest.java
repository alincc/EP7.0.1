/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.PageNumber;
import com.elasticpath.rest.resource.searches.keywords.Items;
import com.elasticpath.rest.resource.searches.keywords.Keywords;
import com.elasticpath.rest.resource.searches.keywords.impl.KeywordsSearchesResourceOperatorImpl;
import com.elasticpath.rest.resource.searches.navigations.Navigations;
import com.elasticpath.rest.resource.searches.navigations.impl.NavigationsSearchesResourceOperatorImpl;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests URI-related annotations on {@link SearchesResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SearchesResourceOperatorImpl.class,
		KeywordsSearchesResourceOperatorImpl.class,
		NavigationsSearchesResourceOperatorImpl.class })
public final class SearchesResourceOperatorImplUriTest extends AbstractResourceOperatorUriTest {

	private static final String SEARCHES = "searches";
	private static final String SCOPE = "scope";
	private static final String SEARCH_KEYWORDS_STRING = "base32encodedkeywordsstring=";
	private static final String ENCODED_NAVIGATION_NODE_ID = "nodeidvj56tghtoierwhw456otwerhw45624trgfwe=";

	@Spy
	private final SearchesResourceOperatorImpl searchesResourceOperator = new SearchesResourceOperatorImpl(null, null, null, null);

	@Spy
	private final KeywordsSearchesResourceOperatorImpl keywordsSearchesResourceOperator = new KeywordsSearchesResourceOperatorImpl(null, null);

	@Spy
	private final NavigationsSearchesResourceOperatorImpl navigationsSearchesResourceOperator =	new NavigationsSearchesResourceOperatorImpl(
			null, null);

	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Tests that the uri for searches read dispatches to
	 * {@link SearchesResourceOperatorImpl#processRead(String, ResourceOperation)}.
	 */
	@Test
	public void testGetSearchUriDispatchesToProcessRead() {
		String uri = URIUtil.format(SEARCHES, SCOPE);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(searchesResourceOperator)
				.processRead(SCOPE, operation);

		dispatch(operation);

		verify(searchesResourceOperator).processRead(SCOPE, operation);
	}

	/**
	 * Tests that the uri for keywords search read dispatches to
	 * {@link KeywordsSearchesResourceOperatorImpl#processRead(String, String, ResourceOperation)}.
	 */
	@Test
	public void testGetKeywordsSearchUriDispatchesToProcessRead() {
		String uri = URIUtil.format(SEARCHES, SCOPE, Keywords.URI_PART, Items.URI_PART, SEARCH_KEYWORDS_STRING);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(keywordsSearchesResourceOperator)
				.processRead(SCOPE, SEARCH_KEYWORDS_STRING, operation);

		dispatch(operation);

		verify(keywordsSearchesResourceOperator).processRead(SCOPE, SEARCH_KEYWORDS_STRING, operation);
	}

	/**
	 * Tests that the uri for navigations search read dispatches to
	 * {@link NavigationsSearchesResourceOperatorImpl#processRead(String, String, ResourceOperation)}.
	 */
	@Test
	public void testGetNavigationsSearchUriDispatchesToProcessRead() {
		String uri = URIUtil.format(SEARCHES, SCOPE, Navigations.URI_PART, Items.URI_PART,
				ENCODED_NAVIGATION_NODE_ID);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(navigationsSearchesResourceOperator)
				.processRead(SCOPE, ENCODED_NAVIGATION_NODE_ID, operation);

		dispatch(operation);

		verify(navigationsSearchesResourceOperator).processRead(SCOPE, ENCODED_NAVIGATION_NODE_ID, operation);
	}

	/**
	 * Tests that the uri for navigations search with page number read dispatches to
	 * {@link NavigationsSearchesResourceOperatorImpl#processReadWithPageNumber(String, String, String, ResourceOperation)}.
	 */
	@Test
	public void testGetNavigationsSearchUriDispatchesToProcessReadWithPageNumber() {
		String uri = URIUtil.format(SEARCHES, SCOPE, Navigations.URI_PART, Items.URI_PART,
				ENCODED_NAVIGATION_NODE_ID, PageNumber.URI_PART, "1");
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(navigationsSearchesResourceOperator)
				.processReadWithPageNumber(SCOPE, ENCODED_NAVIGATION_NODE_ID, "1", operation);

		dispatch(operation);

		verify(navigationsSearchesResourceOperator).processReadWithPageNumber(SCOPE, ENCODED_NAVIGATION_NODE_ID, "1", operation);
	}

	/**
	 * Tests that the uri for keywords search with page number dispatches to
	 * {@link KeywordsSearchesResourceOperatorImpl#processPagedRead(String, String, String, ResourceOperation)}.
	 */
	@Test
	public void testGetKeywordsSearchWithPageNumberUriDispatchesToProcessPagedRead() {
		String pageNumber = "1";
		String uri = URIUtil.format(SEARCHES, SCOPE, Keywords.URI_PART, Items.URI_PART,
				SEARCH_KEYWORDS_STRING, PageNumber.URI_PART, pageNumber);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(keywordsSearchesResourceOperator)
				.processPagedRead(SCOPE, SEARCH_KEYWORDS_STRING, pageNumber, operation);

		dispatch(operation);

		verify(keywordsSearchesResourceOperator).processPagedRead(SCOPE, SEARCH_KEYWORDS_STRING, pageNumber, operation);
	}

	/**
	 * Tests that the uri for keywords search with 0 page number does not dispatch to
	 * {@link KeywordsSearchesResourceOperatorImpl#processPagedRead(String, String, String, ResourceOperation)}.
	 */
	@Test
	public void testGetKeywordsSearchWithZeroPageNumberDoesNotDispatchProcessPagedRead() {
		String pageNumber = "0";
		String uri = URIUtil.format(SEARCHES, SCOPE, Keywords.URI_PART, Items.URI_PART,
				SEARCH_KEYWORDS_STRING, PageNumber.URI_PART, pageNumber);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		verifyZeroInteractions(keywordsSearchesResourceOperator);

		dispatch(operation);
	}

	/**
	 * Tests that the uri for keywords search with -1 page number does not dispatch to
	 * {@link KeywordsSearchesResourceOperatorImpl#processPagedRead(String, String, String, ResourceOperation)}.
	 */
	@Test
	public void testGetKeywordsSearchWithNegativePageNumberDoesNotDispatchProcessPagedRead() {
		String pageNumber = "-1";
		String uri = URIUtil.format(SEARCHES, SCOPE, Keywords.URI_PART, Items.URI_PART,
				SEARCH_KEYWORDS_STRING, PageNumber.URI_PART, pageNumber);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		verifyZeroInteractions(keywordsSearchesResourceOperator);

		dispatch(operation);
	}

	/**
	 * Test that the uri for keywords search form dispatches
	 * {@link SearchesResourceOperatorImpl#processKeywordsFormRead(String, ResourceOperation)}.
	 */
	@Test
	public void testGetKeywordsFormDispatchesProcessKeywordsFormRead() {
		String uri = URIUtil.format(SEARCHES, SCOPE, Keywords.URI_PART, Form.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(searchesResourceOperator)
				.processKeywordsFormRead(SCOPE, operation);

		dispatch(operation);

		verify(searchesResourceOperator).processKeywordsFormRead(SCOPE, operation);
	}

	/**
	 * Test that post with uri for keywords search dispatches
	 * {@link SearchesResourceOperatorImpl#processSearchItemKeywords(String, ResourceOperation)}.
	 */
	@Test
	public void testPostKeywordsSearchDispatchesProcessSearchItemKeywords() {
		String uri = URIUtil.format(SEARCHES, SCOPE, Keywords.URI_PART, Items.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createCreate(uri, null);
		doReturn(mockOperationResult)
				.when(searchesResourceOperator)
				.processSearchItemKeywords(SCOPE, operation);

		dispatch(operation);

		verify(searchesResourceOperator).processSearchItemKeywords(SCOPE, operation);
	}

	private void dispatch(final ResourceOperation operation) {
		dispatchMethod(operation,
				searchesResourceOperator,
				keywordsSearchesResourceOperator,
				navigationsSearchesResourceOperator);
	}
}
