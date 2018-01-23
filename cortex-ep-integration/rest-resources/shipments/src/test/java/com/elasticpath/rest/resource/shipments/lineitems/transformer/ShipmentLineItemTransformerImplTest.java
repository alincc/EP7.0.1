/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ShipmentLineItemUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentLineItemUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShipmentLineItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentLineItemsUriBuilderFactory;

/**
 * Test cases for {@link ShipmentLineItemTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemTransformerImplTest {

	private static final String SHOULD_MATCH_EXPECTED = "The values should match.";
	private static final String SCOPE = "TEST";
	private static final String PURCHASE_ID = "order-12345";
	private static final String SHIPMENT_ID = "shipment-12345-1";
	private static final String LINE_ITEM_ID = "line-item-12345-1";
	private static final String URI = "/asd/asd/asd";
	private static final String PARENT_URI = "/parentResource/id";
	private static final String NAME = "name";
	private static final Integer QUANTITY = 123;

	private static final ShipmentLineItemEntity SHIPMENT_LINEITEM_ENTITY = ShipmentLineItemEntity.builder()
			.withPurchaseId(PURCHASE_ID)
			.withShipmentId(SHIPMENT_ID)
			.withLineItemId(LINE_ITEM_ID)
			.withName(NAME)
			.withQuantity(QUANTITY)
			.withParentUri(PARENT_URI)
			.build();

	@Mock
	private ShipmentLineItemUriBuilderFactory shipmentLineItemUriBuilderFactory;

	@Mock
	private ShipmentLineItemsUriBuilderFactory shipmentLineItemsUriBuilderFactory;

	@Mock
	private ShipmentLineItemUriBuilder shipmentLineItemUriBuilder;

	@Mock
	private ShipmentLineItemsUriBuilder shipmentLineItemsUriBuilder;

	@Mock
	private ResourceOperationContext operationContext;

	@Mock
	private ResourceOperation resourceOperation;

	@InjectMocks
	private ShipmentLineItemTransformerImpl shipmentLineItemTransformer;


	@Before
	public void setUp() {
		when(operationContext.getResourceOperation()).thenReturn(resourceOperation);
		when(resourceOperation.getUri()).thenReturn(URI);
		when(shipmentLineItemUriBuilderFactory.get()).thenReturn(shipmentLineItemUriBuilder);
		when(shipmentLineItemsUriBuilderFactory.get()).thenReturn(shipmentLineItemsUriBuilder);
		when(shipmentLineItemUriBuilder.setSourceUri(any(String.class))).thenReturn(shipmentLineItemUriBuilder);
		when(shipmentLineItemUriBuilder.setLineItemId(any(String.class))).thenReturn(shipmentLineItemUriBuilder);

		when(shipmentLineItemsUriBuilder.setSourceUri(any(String.class))).thenReturn(shipmentLineItemsUriBuilder);
	}

	@Test
	public void testTransformLineItemDtoToRepresentation() {
		ResourceState<ShipmentLineItemEntity> representation = shipmentLineItemTransformer.transform(SCOPE, SHIPMENT_LINEITEM_ENTITY);
		assertEquals(SHOULD_MATCH_EXPECTED, buildExpectedSelf(), representation.getSelf());
		assertEquals(SHOULD_MATCH_EXPECTED, buildExpectedLinks(), representation.getLinks());
		assertEquals(SHOULD_MATCH_EXPECTED, NAME, representation.getEntity().getName());
		assertEquals(SHOULD_MATCH_EXPECTED, PURCHASE_ID, representation.getEntity().getPurchaseId());
		assertEquals(SHOULD_MATCH_EXPECTED, QUANTITY, representation.getEntity().getQuantity());
		assertEquals(SHOULD_MATCH_EXPECTED, SCOPE, representation.getScope());
		assertEquals(SHOULD_MATCH_EXPECTED, SHIPMENT_ID, representation.getEntity().getShipmentId());
	}


	private Self buildExpectedSelf() {
		return SelfFactory.createSelf(URI);
	}

	private List<ResourceLink> buildExpectedLinks() {
		String lineItemsUri = shipmentLineItemsUriBuilderFactory.get().setSourceUri(PARENT_URI).build();
		ResourceLink lineItemsLink = ResourceLinkFactory.createNoRev(lineItemsUri, CollectionsMediaTypes.LINKS.id(), "list");
		ResourceLink shipmentLink =
				ResourceLinkFactory.createNoRev(PARENT_URI, ShipmentsMediaTypes.SHIPMENT.id(), ShipmentsResourceRels.SHIPMENT_REL);
		return ImmutableList.of(lineItemsLink, shipmentLink); // shipment and line items links
	}
}
