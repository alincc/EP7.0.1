/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for {@link PurchaseUriBuilderImpl}.
 */
public class PurchaseUriBuilderImplTest {

	private static final String RESOURCE_SERVER_NAME = "purchases";
	private static final String SCOPE = "testScope";
	private static final String PURCHASE_ID = "testPurchaseId";
	
	private final PurchaseUriBuilder uriBuilder = new PurchaseUriBuilderImpl(RESOURCE_SERVER_NAME);

	@Test
	public void testBuildComplete() {
		String uri = uriBuilder
				.setScope(SCOPE)
				.setPurchaseId(PURCHASE_ID)
				.build();
		
		assertEquals("Built URI should match expected URI format.", URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, PURCHASE_ID), uri);
	}

	@Test
	public void testBuildCompleteWhenDecodedId() {
		String uri = uriBuilder
				.setScope(SCOPE)
				.setDecodedPurchaseId(PURCHASE_ID)
				.build();

		assertEquals("Built URI should match expected URI format.",
				URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, Base32Util.encode(PURCHASE_ID)), uri);
	}

	@Test(expected = AssertionError.class)
	public void testBuildEmpty() {
		uriBuilder.build();
	}

	@Test(expected = AssertionError.class)
	public void testBuildMissingScope() {
		uriBuilder
		.setPurchaseId(PURCHASE_ID)
				.build();
	}

	@Test(expected = AssertionError.class)
	public void testBuildMissingPurchaseId() {
		uriBuilder
				.setScope(SCOPE)
				.build();
	}

}
