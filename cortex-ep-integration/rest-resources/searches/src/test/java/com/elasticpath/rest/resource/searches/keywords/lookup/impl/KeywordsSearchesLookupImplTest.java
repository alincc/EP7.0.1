/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.keywords.lookup.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.definition.collections.PaginationEntity;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.pagination.transform.PaginatedLinksTransformer;
import com.elasticpath.rest.resource.searches.TestSearchesResourceConstants;
import com.elasticpath.rest.resource.searches.keywords.Items;
import com.elasticpath.rest.resource.searches.keywords.Keywords;
import com.elasticpath.rest.resource.searches.keywords.integration.KeywordSearchLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link KeywordsSearchesLookupImpl}.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.crypto.*")  // CompositeIdUtil + SignatureGenerator uses this package and cannot be enhanced
@PrepareForTest(PaginatedLinksTransformer.class)
public final class KeywordsSearchesLookupImplTest {

	private static final int TEST_PAGE_SIZE = 3;
	private static final String SEARCH_STRING = "some search string";
	private static final String SCOPE = "scope";
	private static final String BASE_SEARCH_URI = URIUtil.format(TestSearchesResourceConstants.RESOURCE_NAME, SCOPE,
			Keywords.URI_PART, Items.URI_PART);

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private PaginatedLinksTransformer mockPaginatedLinksTransformer;
	@Mock
	private KeywordSearchLookupStrategy mockKeywordSearchLookupStrategy;

	@InjectMocks
	private KeywordsSearchesLookupImpl keywordSearchesLookup;


	/**
	 * Tests findItemsByKeywords for successful results.
	 */
	@Test
	public void testFindItemsByKeywordSuccess() {
		Map<String, String> searchFieldValuePairs = new TreeMap<>();
		searchFieldValuePairs.put(SearchKeywordsEntity.KEYWORDS_PROPERTY, SEARCH_STRING);
		PaginationDto paginationDto = ResourceTypeFactory.createResourceEntity(PaginationDto.class);
		ExecutionResult<PaginationDto> itemSearchResult = ExecutionResultFactory.createReadOK(paginationDto);
		Self expectedSelf = SelfFactory.createSelf(BASE_SEARCH_URI, CollectionsMediaTypes.PAGINATED_LINKS.id());
		ResourceState<PaginatedLinksEntity> paginatedLinkRepresentation = ResourceState.Builder
				.create(PaginatedLinksEntity.builder().build())
				.withSelf(expectedSelf)
				.build();

		when(mockKeywordSearchLookupStrategy.find(SCOPE, SEARCH_STRING, 1, null))
				.thenReturn(itemSearchResult);
		when(mockPaginatedLinksTransformer.transformToResourceState(paginationDto, SCOPE, BASE_SEARCH_URI))
				.thenReturn(paginatedLinkRepresentation);

		String encodedSearchKeywords = CompositeIdUtil.encodeCompositeId(searchFieldValuePairs);

		ExecutionResult<ResourceState<PaginatedLinksEntity>> lookupResult =
				keywordSearchesLookup.findItemsByKeywords(SCOPE, BASE_SEARCH_URI, encodedSearchKeywords, 1);

		assertTrue(lookupResult.isSuccessful());
		ResourceState<PaginatedLinksEntity> representation = lookupResult.getData();
		assertEquals(expectedSelf, representation.getSelf());
	}

	/**
	 * Tests search with encoded page size.
	 */
	@Test
	public void testSearchWithEncodedPageSize() {
		PaginationDto paginationDto = ResourceTypeFactory.createResourceEntity(PaginationDto.class);
		ExecutionResult<PaginationDto> itemSearchResult = ExecutionResultFactory.createReadOK(paginationDto);
		Self expectedSelf = SelfFactory.createSelf(BASE_SEARCH_URI, CollectionsMediaTypes.PAGINATED_LINKS.id());
		ResourceState<PaginatedLinksEntity> paginatedLinkRepresentation = ResourceState.Builder
				.create(PaginatedLinksEntity.builder().build())
				.withSelf(expectedSelf)
				.build();

		when(mockKeywordSearchLookupStrategy.find(SCOPE, SEARCH_STRING, 1, TEST_PAGE_SIZE))
				.thenReturn(itemSearchResult);
		when(mockPaginatedLinksTransformer.transformToResourceState(paginationDto, SCOPE, BASE_SEARCH_URI))
				.thenReturn(paginatedLinkRepresentation);

		Map<String, String> searchFieldValuePairs = new TreeMap<>();
		searchFieldValuePairs.put(SearchKeywordsEntity.KEYWORDS_PROPERTY, SEARCH_STRING);
		searchFieldValuePairs.put(PaginationEntity.PAGE_SIZE_PROPERTY, Integer.toString(TEST_PAGE_SIZE));
		String encodedSearchKeywords = CompositeIdUtil.encodeCompositeId(searchFieldValuePairs);

		ExecutionResult<ResourceState<PaginatedLinksEntity>> lookupResult =
				keywordSearchesLookup.findItemsByKeywords(SCOPE, BASE_SEARCH_URI, encodedSearchKeywords, 1);
		assertTrue(lookupResult.isSuccessful());
		ResourceState<PaginatedLinksEntity> representation = lookupResult.getData();
		assertEquals(expectedSelf, representation.getSelf());
	}

	/**
	 * Tests findItemsByKeywords for failure results.
	 */
	@Test
	public void testFindItemsByKeywordFailure() {
		ExecutionResult<PaginationDto> itemSearchResult = ExecutionResultFactory.createNotFound("Failure result");

		when(mockKeywordSearchLookupStrategy.find(anyString(), anyString(), anyInt(), anyInt()))
				.thenReturn(itemSearchResult);

		Map<String, String> searchFieldValuePairs = new TreeMap<>();
		searchFieldValuePairs.put(SearchKeywordsEntity.KEYWORDS_PROPERTY, SEARCH_STRING);
		searchFieldValuePairs.put(PaginationEntity.PAGE_SIZE_PROPERTY, "5");
		String searchKeywords = CompositeIdUtil.encodeCompositeId(searchFieldValuePairs);
		String searchUri = URIUtil.format(BASE_SEARCH_URI, searchKeywords);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		keywordSearchesLookup.findItemsByKeywords(SCOPE, searchUri, searchKeywords, 1);
	}
}


