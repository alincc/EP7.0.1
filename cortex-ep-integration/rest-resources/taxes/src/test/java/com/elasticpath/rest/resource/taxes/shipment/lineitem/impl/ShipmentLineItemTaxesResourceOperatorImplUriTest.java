/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.resource.taxes.impl.TaxesUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.TaxesUriBuilder;

/**
 * URI test for {@link ShipmentLineItemTaxesResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemTaxesResourceOperatorImplUriTest extends AbstractUriTest {

	@Spy
	private final ShipmentLineItemTaxesResourceOperatorImpl resourceOperator = new ShipmentLineItemTaxesResourceOperatorImpl(null);

	@Test
	public void testPathAnnotationForProcessRead() {

		mediaType(ShipmentsMediaTypes.SHIPMENT_LINE_ITEM);
		String taxesUri = buildTaxesUri();

		ResourceOperation operation = createRead(taxesUri);
		readOther(operation);
		doReturn(operationResult).when(resourceOperator).processRead(anyShipmentLineItemEntity(), anyResourceOperation());

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processRead(anyShipmentLineItemEntity(), anyResourceOperation());
	}

	private static ResourceState<ShipmentLineItemEntity> anyShipmentLineItemEntity() {
		return Mockito.any();
	}

	private String buildTaxesUri() {
		final String resourceServerName = "taxes";
		final String sourceUri = "i/am/a/resource=";

		TaxesUriBuilder taxesUriBuilder = new TaxesUriBuilderImpl(resourceServerName);
		return taxesUriBuilder.setSourceUri(sourceUri).build();
	}
}
