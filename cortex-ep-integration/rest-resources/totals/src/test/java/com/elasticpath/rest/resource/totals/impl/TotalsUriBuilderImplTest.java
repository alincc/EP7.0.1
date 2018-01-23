/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for {@link TotalsUriBuilderImpl}.
 */
public class TotalsUriBuilderImplTest {

	private static final String RESOURCE_SERVER_NAME = "testTotalsResourceName";
	private static final String SOURCE_URI = "testSourceUri";

	private final TotalsUriBuilderImpl totalsUriBuilderImpl = new TotalsUriBuilderImpl(RESOURCE_SERVER_NAME);

	/**
	 * Test expected success case scenario with all fields populated.
	 */
	@Test
	public void testBuildComplete() {
		String uri = totalsUriBuilderImpl
				.setSourceUri(SOURCE_URI)
				.build();
		assertEquals("Built URI should follow expected format.", URIUtil.format(RESOURCE_SERVER_NAME, SOURCE_URI), uri);
	}
	
	/**
	 * Test use case with no fields populated.
	 */
	@Test(expected = AssertionError.class)
	public void testBuildEmpty() {
		totalsUriBuilderImpl.build();
	}

}
