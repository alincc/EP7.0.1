/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableSet;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.shipments.lineitems.impl.ShipmentLineItemUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ShipmentLineItemUriBuilderFactory;

/**
 * Test cases for {@link ShipmentLineItemTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemLinksTransformerImplTest {

	private static final String SELF_SHOULD_NOT_NULL = "Self should not be null.";
	private static final String PURCHASE_ID = "order-12345";
	private static final String SHIPMENT_ID = "shipment-12345-1";
	private static final String URI = "/asd/asd/asd";
	private static final String PARENT_URI = "/parentResource/id";


	private static final Collection<String> SHIPMENT_LINE_ITEM_IDS = ImmutableSet.of("123", "456");

	private static final ResourceState<ShipmentEntity> REQUEST =
			ResourceState.Builder.create(
					ShipmentEntity.builder()
							.withShipmentId(SHIPMENT_ID)
							.withPurchaseId(PURCHASE_ID)
							.build())
					.withSelf(SelfFactory.createSelf(PARENT_URI))
					.build();

	@Mock
	private ShipmentLineItemUriBuilderFactory shipmentLineItemUriBuilderFactory;
	@Mock
	private ResourceOperationContext operationContext;
	@Mock
	private ResourceOperation resourceOperation;
	@InjectMocks
	private ShipmentLineItemLinksTransformerImpl shipmentLineItemTransformer;


	@Before
	public void setUp() {
		when(operationContext.getResourceOperation()).thenReturn(resourceOperation);
		when(resourceOperation.getUri()).thenReturn(URI);
		when(shipmentLineItemUriBuilderFactory.get()).thenAnswer(invocation -> new ShipmentLineItemUriBuilderImpl());
	}

	@Test
	public void testTransformItemIDsToRepresentation() {
		int expectedLinks = SHIPMENT_LINE_ITEM_IDS.size() + 1; // shipment line items and link to shipment

		ResourceState<LinksEntity> representation = shipmentLineItemTransformer.transform(SHIPMENT_LINE_ITEM_IDS, REQUEST);

		assertEquals(expectedLinks, representation.getLinks().size());
		assertNotNull(SELF_SHOULD_NOT_NULL, representation.getSelf());
	}
}
