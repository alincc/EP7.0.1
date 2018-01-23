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
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

/**
 * URI test class for {@link ShipmentResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ShipmentResourceOperatorImpl.class })
public final class ShipmentResourceOperatorUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER_NAME = "shipments";
	private static final String SHIPMENT_ID = "shipmentId=";
	private static final String OTHER_URI = "/other/uri=";

	@Mock
	private ShipmentResourceOperatorImpl resourceOperator;

	@Test
	public void testProcessReadMethodIsInvokedWhenValidRfoUriSupplied() {
		String couponsUri = new ShipmentsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(OTHER_URI)
				.setShipmentId(SHIPMENT_ID)
				.build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(couponsUri);

		mediaType(PurchasesMediaTypes.PURCHASE);
		readOther(operation);

		when(resourceOperator.processRead(anyPurchaseEntity(), anyString(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processRead(anyPurchaseEntity(), anyString(), anyResourceOperation());
	}

	private ResourceState<PurchaseEntity> anyPurchaseEntity() {
		return any();
	}
}
