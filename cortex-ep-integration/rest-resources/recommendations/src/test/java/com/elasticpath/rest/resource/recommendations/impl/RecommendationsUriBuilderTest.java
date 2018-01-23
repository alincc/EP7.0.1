/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.dispatch.operator.annotation.PageNumber;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link RecommendationsUriBuilderImpl}.
 */
public final class RecommendationsUriBuilderTest  {

	private static final String RESOURCE_NAME = "recommendations";
	private static final String OTHER_URI = "/some/other/uri";
	private static final String SCOPE = "scope";
	private static final String RECOMMENDATION_GROUP = "updownleftrightsells";
	private static final String ERROR_MESSAGE = "Constructed URI should match expected";


	@Test
	public void testBuildRootRecommendedItemsUriWithValidSets() {
		String expectedUri = URIUtil.format(RESOURCE_NAME, SCOPE, RECOMMENDATION_GROUP);

		String resultURI  = new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setRecommendationGroup(RECOMMENDATION_GROUP)
				.setScope(SCOPE)
				.build();

		assertEquals(ERROR_MESSAGE, expectedUri, resultURI);
	}

	@Test
	public void testBuildRootRecommendationsUriWhenValid() {
		String expectedUri = URIUtil.format(RESOURCE_NAME, SCOPE);

		String resultURI  = new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setScope(SCOPE)
				.build();

		assertEquals(ERROR_MESSAGE, expectedUri, resultURI);
	}

	@Test
	public void testBuildReadFromOtherRecommendationsUriWhenValid() {
		String expectedUri = URIUtil.format(RESOURCE_NAME,  OTHER_URI);

		String resultURI  = new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setSourceUri(OTHER_URI)
				.build();

		assertEquals(ERROR_MESSAGE, expectedUri, resultURI);
	}

	@Test
	public void testBuildReadFromOtherRecommendedItemsUriWhenValid() {
		String expectedUri = URIUtil.format(RESOURCE_NAME,  OTHER_URI, RECOMMENDATION_GROUP);

		String resultURI  = new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setSourceUri(OTHER_URI)
				.setRecommendationGroup(RECOMMENDATION_GROUP)
				.build();

		assertEquals(ERROR_MESSAGE, expectedUri, resultURI);
	}

	@Test
	public void testBuildReadFromOtherRecommendedItemsWithPageNumberUriWhenValid() {
		String expectedUri = URIUtil.format(RESOURCE_NAME,  OTHER_URI, RECOMMENDATION_GROUP, PageNumber.URI_PART, "2");

		String resultURI  = new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setSourceUri(OTHER_URI)
				.setRecommendationGroup(RECOMMENDATION_GROUP)
				.setPageNumber(2)
				.build();

		assertEquals(ERROR_MESSAGE, expectedUri, resultURI);
	}

	@Test(expected = AssertionError.class)
	public void testBuilUriWhenScopeAndSourceUriIncorrectlySet() {
		new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.setSourceUri(OTHER_URI)
				.setScope(SCOPE)
				.build();
	}


	@Test(expected = AssertionError.class)
	public void testBuildUriWhenBothScopeAndSourceUriMissing() {

		new RecommendationsUriBuilderImpl(RESOURCE_NAME)
				.build();

	}



}
