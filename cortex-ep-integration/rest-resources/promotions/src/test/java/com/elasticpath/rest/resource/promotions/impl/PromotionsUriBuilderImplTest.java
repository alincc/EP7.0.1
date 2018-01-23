/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.promotions.PossiblePromotions;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link PromotionsUriBuilderImpl}.
 */
public final class PromotionsUriBuilderImplTest {

	private static final String PROMOTIONS = "promotions";
	private static final String OTHER_URI = "/some/other/uri";
	private static final String PROMOTION_ID = "promotionId";
	private static final String SCOPE = "scope";
	private static final String ERROR_MESSAGE = "Constructed URI should match expected";

	@Test
	public void testBuildRootPromotionsUriWhenValid() {
		PromotionsUriBuilderImpl uriBuilder = new PromotionsUriBuilderImpl(PROMOTIONS);

		String promotionsUri = uriBuilder.setPromotionId(PROMOTION_ID)
				.setScope(SCOPE)
				.build();

		String expectedPromotionsUri = URIUtil.format(PROMOTIONS, SCOPE, PROMOTION_ID);
		assertEquals(ERROR_MESSAGE, expectedPromotionsUri, promotionsUri);
	}

	@Test
	public void testBuildSpecificPromotionUriWhenValid() {
		PromotionsUriBuilderImpl uriBuilder = new PromotionsUriBuilderImpl(PROMOTIONS);

		String promotionsUri = uriBuilder.setPromotionId(PROMOTION_ID)
				.setScope(SCOPE)
				.build();

		String expectedPromotionsUri = URIUtil.format(PROMOTIONS, SCOPE, PROMOTION_ID);

		assertEquals(ERROR_MESSAGE, expectedPromotionsUri, promotionsUri);
	}

	@Test
	public void testBuildSpecificReadFromOtherPromotionUriWhenValid() {
		PromotionsUriBuilderImpl uriBuilder = new PromotionsUriBuilderImpl(PROMOTIONS);

		String promotionsUri = uriBuilder.setPromotionId(PROMOTION_ID)
				.setSourceUri(OTHER_URI)
				.setPromotionId(PROMOTION_ID)
				.build();

		String expectedPromotionsUri = URIUtil.format(PROMOTIONS, OTHER_URI, PROMOTION_ID);

		assertEquals(ERROR_MESSAGE, expectedPromotionsUri, promotionsUri);
	}


	@Test
	public void testBuildReadFromOtherPromotionsUriWhenValid() {
		String expectedUri = URIUtil.format(PROMOTIONS,  OTHER_URI);

		String resultURI  = new PromotionsUriBuilderImpl(PROMOTIONS)
				.setSourceUri(OTHER_URI)
				.build();

		assertEquals(ERROR_MESSAGE, expectedUri, resultURI);
	}

	@Test
	public void testBuildReadFromOtherPossiblePromotionsUriWhenValid() {
		String expectedUri = URIUtil.format(PROMOTIONS,  OTHER_URI, PossiblePromotions.URI_PART);

		String resultURI  = new PromotionsUriBuilderImpl(PROMOTIONS)
				.setSourceUri(OTHER_URI)
				.setPromotionType(PossiblePromotions.URI_PART)
				.build();

		assertEquals(ERROR_MESSAGE, expectedUri, resultURI);
	}

	@Test(expected = AssertionError.class)
	public void testAssertPromotionIdNotSetAlone() {
		new PromotionsUriBuilderImpl(PROMOTIONS)
				.setPromotionId(PROMOTION_ID)
				.build();
	}

	@Test(expected = AssertionError.class)
	public void testBuildReadFromOtherPromotionsUriWhenScopeIncorrectlySet() {
		new PromotionsUriBuilderImpl(PROMOTIONS)
				.setSourceUri(OTHER_URI)
				.setScope(SCOPE)
				.build();
	}

	@Test(expected = AssertionError.class)
	public void testAssertSourceUriWhenScopeAndPromotionIdAreSet() {
		new PromotionsUriBuilderImpl(PROMOTIONS)
				.setSourceUri(OTHER_URI)
				.setScope(SCOPE)
				.setPromotionId(PROMOTION_ID)
				.build();
	}

	@Test(expected = AssertionError.class)
	public void testAssertSourceUriWhenPromotionsStatusSet() {
		new PromotionsUriBuilderImpl(PROMOTIONS)
				.setPromotionType(PossiblePromotions.URI_PART)
				.setPromotionId(PROMOTION_ID)
				.build();
	}

	@Test(expected = AssertionError.class)
	public void testAssertPromotionTypeWithNoScopeSet() {
		new PromotionsUriBuilderImpl(PROMOTIONS)
				.setPromotionType(PossiblePromotions.URI_PART)
				.build();
	}
}
