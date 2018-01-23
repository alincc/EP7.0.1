/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test.
 */
@RunWith(MockitoJUnitRunner.class)
public class DiscountsUriBuilderImplTest {

	private DiscountsUriBuilderImpl uriBuilder;

	@Before
	public void setUp() {
		uriBuilder = new DiscountsUriBuilderImpl("discounts");
	}

	@Test
	public void testGoodUri() {
		String uri = uriBuilder.setSourceUri("/carts/scope/id").build();
		assertEquals("Unexpected uri", "/discounts/carts/scope/id", uri);
	}

	@Test
	public void testEmptyOtherUri() {
		String uri = uriBuilder.setSourceUri("").build();
		assertEquals("Unexpected uri", "/discounts", uri);
	}

	@Test(expected = AssertionError.class)
	public void testNullUri() {
		uriBuilder.build();
	}
}
