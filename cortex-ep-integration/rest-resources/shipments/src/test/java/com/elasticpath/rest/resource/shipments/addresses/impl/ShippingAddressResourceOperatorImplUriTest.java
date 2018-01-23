/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.impl;

import static com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes.SHIPMENT;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.resource.shipments.addresses.ShippingAddress;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests URI-related annotations on {@link ShippingAddressResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ShippingAddressResourceOperatorImpl.class })
public class ShippingAddressResourceOperatorImplUriTest extends AbstractUriTest {

	private static final String SHIPMENTS = "shipments";

	private static final String PURCHASE_ID = Base32Util.encode("12345");

	private static final String SHIPMENT_ID = Base32Util.encode("12345-1");

	private static final String SHIPMENT_URI = URIUtil.format(SHIPMENTS, "/purchases/test/", PURCHASE_ID, SHIPMENT_ID);

	@Mock
	private ShippingAddressResourceOperatorImpl shippingAddressResourceOperator;

	@Test
	public void testProcessShippingAddressRead() {
		String uri = URIUtil.format(SHIPMENT_URI, ShippingAddress.PATH_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		mediaType(SHIPMENT);
		readOther(operation);

		when(shippingAddressResourceOperator.processShippingAddressRead(anyShipmentEntity(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, shippingAddressResourceOperator);

		verify(shippingAddressResourceOperator).processShippingAddressRead(anyShipmentEntity(), anyResourceOperation());
	}

	private ResourceState<ShipmentEntity> anyShipmentEntity() {
		return any();
	}
}
