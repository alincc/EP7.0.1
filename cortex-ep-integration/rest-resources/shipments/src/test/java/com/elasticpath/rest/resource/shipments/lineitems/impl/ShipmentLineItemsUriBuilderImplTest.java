/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.shipments.lineitems.rel.ShipmentLineItemResourceRels;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link ShipmentLineItemsUriBuilderImpl}.
 */
public class ShipmentLineItemsUriBuilderImplTest {

	private static final String OTHER_URI = "/resource/scope/id";

	private final ShipmentLineItemsUriBuilderImpl uriBuilder = new ShipmentLineItemsUriBuilderImpl();

	@Test
	public void testGoodOtherUri() {
		String uri = uriBuilder.setSourceUri(OTHER_URI).build();
		assertEquals("Unexpected uri", URIUtil.format(OTHER_URI, ShipmentLineItemResourceRels.LINE_ITEMS_REL), uri);
	}

	@Test(expected = AssertionError.class)
	public void testSourceUriCannotBeNull() {
		uriBuilder.setSourceUri(null).build();
	}

}
