/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

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
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

/**
 * URI test class for {@link ShipmentLineItemsResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ShipmentLineItemsResourceOperatorImpl.class)
public final class ShipmentLineItemsResourceOperatorUriTest extends AbstractUriTest {

	private static final String ENCODED_LINE_ITEM_ID = Base32Util.encode("lineItemId");

	private static final String OTHER_URI = "other/uri=";

	@Mock
	private ShipmentLineItemsResourceOperatorImpl resourceOperator;

	/**
	 * The URI test for {@link ShipmentLineItemsResourceOperatorImpl#processReadShipmentLineItems(ResourceState, ResourceOperation)}.
	 */
	@Test
	public void testProcessReadShipmentLineItems() {

		String shipmentLineItemsUri = new ShipmentLineItemsUriBuilderImpl().setSourceUri(OTHER_URI).build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(shipmentLineItemsUri);

		mediaType(ShipmentsMediaTypes.SHIPMENT);
		readOther(operation);

		when(resourceOperator.processReadShipmentLineItems(anyShipmentEntity(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadShipmentLineItems(anyShipmentEntity(), anyResourceOperation());
	}

	/**
	 * The URI test for {@link ShipmentLineItemsResourceOperatorImpl#processReadShipmentLineItem(ResourceState, String, ResourceOperation)}.
	 */
	@Test
	public void testProcessReadShipmentLineItem() {
		String shipmentLineItemUri = new ShipmentLineItemUriBuilderImpl().setSourceUri(OTHER_URI).setLineItemId(ENCODED_LINE_ITEM_ID).build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(shipmentLineItemUri);

		mediaType(ShipmentsMediaTypes.SHIPMENT);
		readOther(operation);

		when(resourceOperator.processReadShipmentLineItem(anyShipmentEntity(), anyString(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadShipmentLineItem(anyShipmentEntity(), anyString(), anyResourceOperation());
	}

	private ResourceState<ShipmentEntity> anyShipmentEntity() {
		return any();
	}

}
