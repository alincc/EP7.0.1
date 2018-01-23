/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.resource.taxes.impl.TaxesUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.TaxesUriBuilder;

/**
 * URI test for {@link ShipmentTaxesResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentTaxesResourceOperatorImplUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER_NAME = "taxes";
	private static final String ARBITRARY_SOURCE_URI = "i/am/a/resource=";

	@Mock private OperationResult operationResult;

	@Spy private final ShipmentTaxesResourceOperatorImpl resourceOperator = new ShipmentTaxesResourceOperatorImpl(null);

	private final TaxesUriBuilder taxesUriBuilder = new TaxesUriBuilderImpl(RESOURCE_SERVER_NAME);

	@Test
	public void testPathAnnotationForProcessRead() {
		mediaType(ShipmentsMediaTypes.SHIPMENT);
		String taxesUri = taxesUriBuilder.setSourceUri(ARBITRARY_SOURCE_URI).build();
		ResourceOperation operation = createRead(taxesUri);
		readOther(operation);
		doReturn(operationResult)
				.when(resourceOperator)
				.processRead(anyShipmentEntity(), anyResourceOperation());

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processRead(anyShipmentEntity(), anyResourceOperation());
	}

	private static ResourceState<ShipmentEntity> anyShipmentEntity() {
		return Mockito.any();
	}

}
