/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipments.lineitems.option.rel.ShipmentLineItemOptionResourceRels;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link ShipmentLineItemOptionUriBuilderImpl}.
 */
public class ShipmentLineItemOptionUriBuilderImplTest {

	private static final String OPTION_ID = "optionId";

	private static final String OTHER_URI = "/resource/scope/id";

	private final ShipmentLineItemOptionUriBuilderImpl uriBuilder = new ShipmentLineItemOptionUriBuilderImpl();

	@Test
	public void testGoodOtherUriWithId() {
		String uri = uriBuilder.setSourceUri(OTHER_URI).setOptionId(OPTION_ID).build();
		assertEquals("Unexpected uri",
				URIUtil.format(OTHER_URI, ShipmentLineItemOptionResourceRels.LINE_ITEMS_OPTIONS_REL, Base32Util.encode(OPTION_ID)), uri);
	}

	@Test(expected = AssertionError.class)
	public void testSourceUriCannotBeNull() {
		uriBuilder.setSourceUri(null).setOptionId(OPTION_ID).build();
	}

}
