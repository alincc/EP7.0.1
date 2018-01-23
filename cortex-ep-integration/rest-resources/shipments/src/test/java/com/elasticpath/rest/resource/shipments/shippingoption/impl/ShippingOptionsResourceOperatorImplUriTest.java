/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.resource.shipments.impl.ShipmentsUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * URI test for {@link ShippingOptionsResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ShippingOptionsResourceOperatorImpl.class })
public class ShippingOptionsResourceOperatorImplUriTest extends AbstractUriTest {

	private static final String PURCHASE_ID = Base32Util.encode("testPurchaseId");
	private static final String PURCHASE_URI = URIUtil.format("purchases", PURCHASE_ID);
	private static final String SHIPMENT_ID = Base32Util.encode("testShipmentId");

	private final ShipmentsUriBuilderImpl shipmentsUriBuilderImpl = new ShipmentsUriBuilderImpl("shipments");
	private final ShippingOptionUriBuilderImpl shippingOptionUriBuilder = new ShippingOptionUriBuilderImpl();

	@Mock
	private ShippingOptionsResourceOperatorImpl resourceOperator;

	private String shippingOptionUri;

	/**
	 * Prepare common test conditions.
	 */
	@Before
	public void setUp() {
		String shipmentUri = shipmentsUriBuilderImpl
				.setSourceUri(PURCHASE_URI)
				.setShipmentId(SHIPMENT_ID)
				.build();
		shippingOptionUri = shippingOptionUriBuilder
				.setSourceUri(shipmentUri)
				.build();
	}

	/**
	 * Test {@link ShippingOptionsResourceOperatorImpl#processRead} success case.
	 */
	@Test
	public void testPathAnnotationForProcessRead() {
		ResourceOperation operation = TestResourceOperationFactory.createRead(shippingOptionUri);

		mediaType(ShipmentsMediaTypes.SHIPMENT);
		readOther(operation);

		when(resourceOperator.processRead(anyShipmentEntity(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		Mockito.verify(resourceOperator).processRead(anyShipmentEntity(), anyResourceOperation());
	}

	private ResourceState<ShipmentEntity> anyShipmentEntity() {
		return any();
	}

}
