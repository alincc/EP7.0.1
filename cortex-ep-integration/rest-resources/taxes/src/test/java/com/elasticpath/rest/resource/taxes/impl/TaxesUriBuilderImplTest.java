/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for {@link TaxesUriBuilderImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaxesUriBuilderImplTest {

	private static final String RESOURCE_SERVER_NAME = "testTaxes";
	private static final String SOURCE_URI = "testSourceUri";

	private final TaxesUriBuilderImpl uriBuilder = new TaxesUriBuilderImpl(RESOURCE_SERVER_NAME);

	@Test
	public void testBuildComplete() {
		String uri = uriBuilder.setSourceUri(SOURCE_URI)
				.build();

		assertEquals("URI should match expected format.", URIUtil.format(RESOURCE_SERVER_NAME, SOURCE_URI), uri);
	}

	@Test(expected = AssertionError.class)
	public void testBuildEmpty() {
		uriBuilder.build();
	}

}
