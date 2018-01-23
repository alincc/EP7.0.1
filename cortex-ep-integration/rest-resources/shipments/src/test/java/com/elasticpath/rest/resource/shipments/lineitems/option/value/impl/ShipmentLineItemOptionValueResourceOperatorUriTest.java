/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.value.impl;

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
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

/**
 * URI test class for {@link ShipmentLineItemOptionValueResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ShipmentLineItemOptionValueResourceOperatorImpl.class)
public final class ShipmentLineItemOptionValueResourceOperatorUriTest extends AbstractUriTest {

	private static final String OPTION_VALUE_ID = "optionValueId";
	private static final String OTHER_URI = "other/uri=";

	@Mock
	private ShipmentLineItemOptionValueResourceOperatorImpl resourceOperator;

	@Test
	public void testProcessReadShipmentLineItem() {
		String uri = new ShipmentLineItemOptionValueUriBuilderImpl().setSourceUri(OTHER_URI).setOptionValueId(OPTION_VALUE_ID).build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		mediaType(ShipmentsMediaTypes.SHIPMENT_LINE_ITEM_OPTION);
		readOther(operation);
		when(resourceOperator.processReadShipmentLineItemOptionValue(anyShipmentLineItemOptionEntity(), anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadShipmentLineItemOptionValue(anyShipmentLineItemOptionEntity(), anyString(), anyResourceOperation());
	}

	private ResourceState<ShipmentLineItemOptionEntity> anyShipmentLineItemOptionEntity() {
		return any();
	}

}
