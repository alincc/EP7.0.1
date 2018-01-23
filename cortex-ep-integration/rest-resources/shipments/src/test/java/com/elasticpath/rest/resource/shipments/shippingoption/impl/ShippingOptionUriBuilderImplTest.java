/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.impl;


import org.junit.Assert;
import org.junit.Test;

import com.elasticpath.rest.resource.shipments.shippingoption.ShippingOption;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for {@link ShippingOptionUriBuilderImpl}.
 */
public class ShippingOptionUriBuilderImplTest {

	private final ShippingOptionUriBuilderImpl builder = new ShippingOptionUriBuilderImpl();
	
	/**
	 * Test builder with all (1) fields populated.
	 */
	@Test
	public void testBuildComplete() {
		String shipmentUri = "testShipmentUri/abcdefg=";
		String shippingOptionUri = builder
				.setSourceUri(shipmentUri)
				.build();
		
		Assert.assertEquals("Shipping option URI should match expected format.",
				URIUtil.format(shipmentUri, ShippingOption.URI_PART), shippingOptionUri);
	}
	
	/**
	 * Test builder with no fields populated.
	 */
	@Test(expected = AssertionError.class)
	public void testBuildEmpty() {
		builder.build();
	}
	
}
