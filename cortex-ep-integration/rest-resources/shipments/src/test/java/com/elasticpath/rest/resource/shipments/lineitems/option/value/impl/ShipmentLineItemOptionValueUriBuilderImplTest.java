/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.value.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipments.lineitems.option.value.rel.ShipmentLineItemOptionValueResourceRels;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link ShipmentLineItemOptionValueUriBuilderImpl}.
 */
public class ShipmentLineItemOptionValueUriBuilderImplTest {

	private static final String OPTION_VALUE_ID = "optionValueId";

	private static final String OTHER_URI = "/resource/scope/id";

	private final ShipmentLineItemOptionValueUriBuilderImpl uriBuilder = new ShipmentLineItemOptionValueUriBuilderImpl();

	@Test
	public void testGoodOtherUriWithId() {
		String uri = uriBuilder.setSourceUri(OTHER_URI).setOptionValueId(OPTION_VALUE_ID).build();
		assertEquals("Unexpected uri",
				URIUtil.format(OTHER_URI, ShipmentLineItemOptionValueResourceRels.LINE_ITEMS_OPTION_VALUES_REL, Base32Util.encode(OPTION_VALUE_ID)),
				uri);
	}

	@Test(expected = AssertionError.class)
	public void testSourceUriCannotBeNull() {
		uriBuilder.setSourceUri(null).setOptionValueId(OPTION_VALUE_ID).build();
	}

	@Test(expected = AssertionError.class)
	public void testOptionValueIdCannotBeNull() {
		uriBuilder.setSourceUri(OTHER_URI).setOptionValueId(null).build();
	}
}
