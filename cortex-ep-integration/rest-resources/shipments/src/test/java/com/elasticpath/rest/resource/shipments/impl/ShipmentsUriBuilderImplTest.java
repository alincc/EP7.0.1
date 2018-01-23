/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link ShipmentsUriBuilderImpl}.
 */
public class ShipmentsUriBuilderImplTest {

	private static final String RESOURCE_NAME = "shipments";

	private static final String SHIPMENT_ID = "/shipmentId";

	private static final String OTHER_URI = "/purchases/scope/purchaseId";

	private final ShipmentsUriBuilderImpl uriBuilder = new ShipmentsUriBuilderImpl(RESOURCE_NAME);

	@Test
	public void testGoodOtherUriWithId() {
		String uri = uriBuilder.setSourceUri(OTHER_URI).setShipmentId(SHIPMENT_ID).build();
		assertEquals("Unexpected uri", URIUtil.format(RESOURCE_NAME, OTHER_URI, Base32Util.encode(SHIPMENT_ID)), uri);
	}

	@Test(expected = AssertionError.class)
	public void testSourceUriCannotBeNull() {
		uriBuilder.setShipmentId(SHIPMENT_ID).build();
	}

	@Test
	public void testShipmentsForPurchaseUriCreatedWhenShipmentIdIsNull() {
		String uri = uriBuilder.setSourceUri(OTHER_URI).build();
		assertEquals("Unexpected uri", URIUtil.format(RESOURCE_NAME, OTHER_URI), uri);
	}
}
