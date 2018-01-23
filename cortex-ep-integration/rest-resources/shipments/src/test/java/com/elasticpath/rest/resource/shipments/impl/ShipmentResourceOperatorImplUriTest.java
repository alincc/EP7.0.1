/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests URI-related annotations on {@link ShipmentResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ShipmentResourceOperatorImpl.class })
public class ShipmentResourceOperatorImplUriTest extends AbstractUriTest {

	private static final String SHIPMENTS = "shipments";
	private static final String PURCHASE_ID = "12345=";
	private static final String SHIPMENT_ID = "12345-1=";
	private static final String PURCHASE_URI = URIUtil.format("purchases/test/", Base32Util.encode(PURCHASE_ID));
	private final ShipmentsUriBuilderImpl shipmentUriBuilder = new ShipmentsUriBuilderImpl(SHIPMENTS);

	@Mock
	private ShipmentResourceOperatorImpl shipmentResourceOperator;

	@Test
	public void testProcessReadShipments() {
		String uri = shipmentUriBuilder.setSourceUri(PURCHASE_URI).build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		mediaType(PurchasesMediaTypes.PURCHASE);
		readOther(operation);

		when(shipmentResourceOperator.processReadShipments(anyPurchaseEntity(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, shipmentResourceOperator);

		verify(shipmentResourceOperator).processReadShipments(anyPurchaseEntity(), anyResourceOperation());
	}

	@Test
	public void testProcessRead() {
		String uri = shipmentUriBuilder.setSourceUri(PURCHASE_URI).setShipmentId(SHIPMENT_ID).build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		mediaType(PurchasesMediaTypes.PURCHASE);
		readOther(operation);

		when(shipmentResourceOperator.processRead(anyPurchaseEntity(), anyString(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, shipmentResourceOperator);

		verify(shipmentResourceOperator).processRead(anyPurchaseEntity(), anyString(), anyResourceOperation());
	}

	private ResourceState<PurchaseEntity> anyPurchaseEntity() {
		return any();
	}

}
