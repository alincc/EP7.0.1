/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.stock.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.resource.stock.items.Items;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for {@link StockUriBuilderImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class StockUriBuilderImplTest {

	private static final String RESOURCE_SERVER_NAME = "stocks";
	private static final String ITEM_ID = "testItemId";
	private static final String SCOPE = "testScope";
	
	
	private StockUriBuilderImpl stockUriBuilderImpl;
	
	/**
	 * Set up common test conditions.
	 */
	@Before
	public void setUp() {
		stockUriBuilderImpl = new StockUriBuilderImpl(RESOURCE_SERVER_NAME);
	}
	
	/**
	 * Test expected success scenario with all fields populated.
	 */
	@Test
	public void testBuildComplete() {
		String uri = stockUriBuilderImpl
				.setItemId(ITEM_ID)
				.setScope(SCOPE)
				.build();
		
		Assert.assertEquals("Generated URI should match expected URI.", URIUtil.format(RESOURCE_SERVER_NAME, Items.URI_PART, SCOPE, ITEM_ID), uri);
	}
	
	/**
	 * Test use case with no fields populated.
	 */
	@Test(expected = AssertionError.class)
	public void testBuildEmpty() {
		stockUriBuilderImpl
				.build();
	}
	
	/**
	 * Test use case with only item id populated (scope missing).
	 */
	@Test(expected = AssertionError.class)
	public void testBuildIdOnly() {
		stockUriBuilderImpl
				.setItemId(ITEM_ID)
				.build();
	}
	
	/**
	 * Test use case with only scope populated (item id missing).
	 */
	@Test(expected = AssertionError.class)
	public void testBuildScopeOnly() {
		stockUriBuilderImpl
				.setScope(SCOPE)
				.build();
	}
	
}
