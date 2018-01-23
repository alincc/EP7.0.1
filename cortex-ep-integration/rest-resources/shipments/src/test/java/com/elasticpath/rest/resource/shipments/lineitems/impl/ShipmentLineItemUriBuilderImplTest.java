/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipments.lineitems.rel.ShipmentLineItemResourceRels;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link ShipmentLineItemUriBuilderImpl}.
 */
public class ShipmentLineItemUriBuilderImplTest {

	private static final String LINE_ITEM_ID = "lineItemId";
	private static final String ENCODED_LINE_ITEM_ID = Base32Util.encode(LINE_ITEM_ID);

	private static final String OTHER_URI = "/resource/scope/id";

	private final ShipmentLineItemUriBuilderImpl uriBuilder = new ShipmentLineItemUriBuilderImpl();

	@Test
	public void testGoodOtherUriWithId() {
		String uri = uriBuilder.setSourceUri(OTHER_URI).setLineItemId(LINE_ITEM_ID).build();
		assertEquals("Unexpected uri", URIUtil.format(OTHER_URI, ShipmentLineItemResourceRels.LINE_ITEMS_REL, ENCODED_LINE_ITEM_ID), uri);
	}

	@Test(expected = AssertionError.class)
	public void testSourceUriCannotBeNull() {
		uriBuilder.setSourceUri(null).setLineItemId(LINE_ITEM_ID).build();
	}

	@Test(expected = AssertionError.class)
	public void testLineItemIdCannotBeNull() {
		uriBuilder.setSourceUri(OTHER_URI).setLineItemId(null).build();
	}
}
