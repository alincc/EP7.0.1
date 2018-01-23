/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.shipments.addresses.ShippingAddress;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Unit test for {@link ShippingAddressUriBuilderImpl}.
 */
public class ShippingAddressUriBuilderImplTest {

	private static final String SUB_RESOURCE_URI = ShippingAddress.URI_PART;

	private static final String PARENT_URI = "/shipments/purchases/mobee/giydambq=/giydambqfuyq=";

	private final ShippingAddressUriBuilderImpl uriBuilder = new ShippingAddressUriBuilderImpl();

	/** */
	@Test
	public void testCorrectUriCreated() {
		String uri = uriBuilder.setSourceUri(PARENT_URI).build();
		assertEquals("Unexpected uri", URIUtil.format(PARENT_URI, SUB_RESOURCE_URI), uri);
	}

	/** */
	@Test(expected = AssertionError.class)
	public void testParentUriCannotBeNull() {
		uriBuilder.build();
	}

}
