/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes.SHIPMENT_LINE_ITEM;
import static com.elasticpath.rest.uri.URIUtil.format;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ShipmentLineItemTotalsResourceOperatorImpl.class})
public final class ShipmentLineItemTotalsResourceOperatorUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER_NAME = "totals";
	private static final String OTHER_URI = "items/other/uri=";

	@Mock
	private ShipmentLineItemTotalsResourceOperatorImpl resourceOperator;

	@Before
	public void setUp() {
		mediaType(SHIPMENT_LINE_ITEM);
	}

	@Test
	public void testProcessRead() {

		ResourceOperation operation = createRead(format(RESOURCE_SERVER_NAME, OTHER_URI));
		readOther(operation);
		when(resourceOperator.processRead(anyShipmentLineItemEntity(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processRead(anyShipmentLineItemEntity(), anyResourceOperation());
	}

	private static ResourceState<ShipmentLineItemEntity> anyShipmentLineItemEntity() {

		return Mockito.any();
	}

}
