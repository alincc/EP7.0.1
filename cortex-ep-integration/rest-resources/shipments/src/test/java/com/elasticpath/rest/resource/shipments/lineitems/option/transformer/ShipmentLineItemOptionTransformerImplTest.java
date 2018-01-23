/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionValueUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionValueUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionsUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionsUriBuilderFactory;

/**
 * Test cases for {@link ShipmentLineItemOptionTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemOptionTransformerImplTest {

	private static final String SELF_SHOULD_NOT_NULL = "Self should not be null.";
	private static final String SHOULD_MATCH_EXPECTED = "The values should match.";
	private static final String SCOPE = "TEST";
	private static final String PURCHASE_ID = "order-12345";
	private static final String SHIPMENT_ID = "shipment-12345-1";
	private static final String LINE_ITEM_ID = "line-item-12345-1";
	private static final String OPTION_ID = "line-item-option-12345-1";
	private static final String OPTION_VALUE_ID = "line-item-option-value-12345-1";
	private static final String NAME = "name";
	private static final String DISPLAY_NAME = "display-name";

	private static final ShipmentLineItemOptionEntity LINEITEM_OPTION_DTO = ShipmentLineItemOptionEntity.builder()
			.withPurchaseId(PURCHASE_ID)
			.withShipmentId(SHIPMENT_ID)
			.withLineItemId(LINE_ITEM_ID)
			.withLineItemOptionId(OPTION_ID)
			.withLineItemOptionValueId(OPTION_VALUE_ID)
			.withName(NAME)
			.withDisplayName(DISPLAY_NAME)
			.build();

	private static final ResourceState<ShipmentLineItemEntity> SHIPMENT_LINE_ITEM = ResourceState.Builder
			.create(ShipmentLineItemEntity.builder().build())
			.withScope(SCOPE)
			.build();

	@Mock
	private ShipmentLineItemOptionsUriBuilderFactory shipmentLineItemOptionUriBuilderFactory;
	@Mock
	private ShipmentLineItemOptionValueUriBuilderFactory shipmentLineItemOptionValueUriBuilderFactory;
	@Mock
	private ShipmentLineItemOptionsUriBuilder lineItemOptionUriBuilder;
	@Mock
	private ShipmentLineItemOptionValueUriBuilder lineItemOptionValueUriBuilder;
	@Mock
	private ResourceOperationContext operationContext;
	@Mock
	private ResourceOperation resourceOperation;
	@InjectMocks
	private ShipmentLineItemOptionTransformerImpl shipmentLineItemOptionTransformer;

	@Before
	public void setUp() {
		when(operationContext.getResourceOperation()).thenReturn(resourceOperation);
		when(shipmentLineItemOptionUriBuilderFactory.get()).thenReturn(lineItemOptionUriBuilder);
		when(shipmentLineItemOptionValueUriBuilderFactory.get()).thenReturn(lineItemOptionValueUriBuilder);
		when(lineItemOptionUriBuilder.setSourceUri(any(String.class))).thenReturn(lineItemOptionUriBuilder);
		when(lineItemOptionValueUriBuilder.setSourceUri(any(String.class))).thenReturn(lineItemOptionValueUriBuilder);
		when(lineItemOptionValueUriBuilder.setOptionValueId(any(String.class))).thenReturn(lineItemOptionValueUriBuilder);
	}


	@Test
	public void testTransformLineItemOptionDtoToRepresentation() {
		ResourceState<ShipmentLineItemOptionEntity> representation = shipmentLineItemOptionTransformer
				.transform(LINEITEM_OPTION_DTO, SHIPMENT_LINE_ITEM);

		ShipmentLineItemOptionEntity shipmentLineItemOptionEntity = representation.getEntity();
		int expectedLinks = 2; // line item and option value links
		assertEquals(expectedLinks, representation.getLinks().size());
		assertNotNull(SELF_SHOULD_NOT_NULL, representation.getSelf());
		assertEquals(SHOULD_MATCH_EXPECTED, LINE_ITEM_ID, shipmentLineItemOptionEntity.getLineItemId());
		assertEquals(SHOULD_MATCH_EXPECTED, OPTION_ID, shipmentLineItemOptionEntity.getLineItemOptionId());
		assertEquals(SHOULD_MATCH_EXPECTED, NAME, shipmentLineItemOptionEntity.getName());
		assertEquals(SHOULD_MATCH_EXPECTED, DISPLAY_NAME, shipmentLineItemOptionEntity.getDisplayName());
		assertEquals(SHOULD_MATCH_EXPECTED, PURCHASE_ID, shipmentLineItemOptionEntity.getPurchaseId());
		assertEquals(SHOULD_MATCH_EXPECTED, SCOPE, representation.getScope());
		assertEquals(SHOULD_MATCH_EXPECTED, SHIPMENT_ID, shipmentLineItemOptionEntity.getShipmentId());
	}


}
