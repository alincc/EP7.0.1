/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.shipments.lineitems.option.rel.ShipmentLineItemOptionResourceRels;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link ShipmentLineItemOptionsUriBuilderImpl}.
 */
public class ShipmentLineItemOptionsUriBuilderImplTest {

	private static final String OTHER_URI = "/resource/scope/id";

	private final ShipmentLineItemOptionsUriBuilderImpl uriBuilder = new ShipmentLineItemOptionsUriBuilderImpl();

	@Test
	public void testGoodOtherUri() {
		String uri = uriBuilder.setSourceUri(OTHER_URI).build();
		assertEquals("Unexpected uri", URIUtil.format(OTHER_URI, ShipmentLineItemOptionResourceRels.LINE_ITEMS_OPTIONS_REL), uri);
	}

	@Test(expected = AssertionError.class)
	public void testSourceUriCannotBeNull() {
		uriBuilder.setSourceUri(null).build();
	}

}
