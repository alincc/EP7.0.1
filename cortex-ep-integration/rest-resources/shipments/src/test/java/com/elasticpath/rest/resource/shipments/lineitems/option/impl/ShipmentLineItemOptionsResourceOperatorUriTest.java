/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.impl;

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
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

/**
 * URI test class for {@link ShipmentLineItemOptionsResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ShipmentLineItemOptionsResourceOperatorImpl.class })
public final class ShipmentLineItemOptionsResourceOperatorUriTest extends AbstractUriTest {

	private static final String OPTION_ID = "optionId";

	private static final String OTHER_URI = "other/uri=";

	@Mock
	private ShipmentLineItemOptionsResourceOperatorImpl resourceOperator;

	/**
	 * The URI test for
	 * {@link ShipmentLineItemOptionsResourceOperatorImpl#processReadShipmentLineItemOptions(ResourceState, ResourceOperation)}.
	 */
	@Test
	public void testProcessReadShipmentLineItemOptions() {

		String shipmentLineItemsUri = new ShipmentLineItemOptionsUriBuilderImpl().setSourceUri(OTHER_URI).build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(shipmentLineItemsUri);

		mediaType(ShipmentsMediaTypes.SHIPMENT_LINE_ITEM);
		readOther(operation);

		when(resourceOperator.processReadShipmentLineItemOptions(anyShipmentLineItemEntity(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadShipmentLineItemOptions(anyShipmentLineItemEntity(), anyResourceOperation());
	}

	/**
	 * The URI test for
	 * {@link ShipmentLineItemOptionsResourceOperatorImpl#processReadShipmentLineItemOption(ResourceState, String, ResourceOperation)}.
	 */
	@Test
	public void testProcessReadShipmentLineItemOption() {
		String shipmentLineItemUri = new ShipmentLineItemOptionUriBuilderImpl().setSourceUri(OTHER_URI).setOptionId(OPTION_ID).build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(shipmentLineItemUri);

		mediaType(ShipmentsMediaTypes.SHIPMENT_LINE_ITEM);
		readOther(operation);

		when(resourceOperator.processReadShipmentLineItemOption(
				anyShipmentLineItemEntity(), anyString(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadShipmentLineItemOption(anyShipmentLineItemEntity(), anyString(), anyResourceOperation());
	}

	private ResourceState<ShipmentLineItemEntity> anyShipmentLineItemEntity() {
		return any();
	}

}
