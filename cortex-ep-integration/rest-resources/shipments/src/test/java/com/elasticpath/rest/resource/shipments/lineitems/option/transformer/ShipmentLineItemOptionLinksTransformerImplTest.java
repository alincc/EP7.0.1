/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.transformer;

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
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.shipments.lineitems.option.impl.ShipmentLineItemOptionUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionUriBuilderFactory;

/**
 * Test cases for {@link ShipmentLineItemOptionLinksTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemOptionLinksTransformerImplTest {

	private static final String SELF_SHOULD_NOT_NULL = "Self should not be null.";
	private static final String SCOPE = "TEST";
	private static final Collection<String> LINE_ITEM_OPTION_IDS = ImmutableSet.of("123", "456");
	private static final ResourceState<ShipmentLineItemEntity> SHIPMENT_LINE_ITEM = ResourceState.Builder
			.create(ShipmentLineItemEntity.builder().build())
			.withScope(SCOPE)
			.withSelf(SelfFactory.createSelf("uri"))
			.build();
	@Mock
	private ShipmentLineItemOptionUriBuilderFactory shipmentLineItemOptionUriBuilderFactory;
	@Mock
	private ShipmentLineItemOptionUriBuilder lineItemOptionUriBuilder;
	@Mock
	private ResourceOperationContext operationContext;
	@Mock
	private ResourceOperation resourceOperation;
	@InjectMocks
	private ShipmentLineItemOptionLinksTransformerImpl shipmentLineItemOptionLinksTransformer;

	@Before
	public void setUp() {
		when(operationContext.getResourceOperation()).thenReturn(resourceOperation);
		when(shipmentLineItemOptionUriBuilderFactory.get()).thenReturn(lineItemOptionUriBuilder);
		when(shipmentLineItemOptionUriBuilderFactory.get()).thenAnswer(invocation -> new ShipmentLineItemOptionUriBuilderImpl());
	}

	@Test
	public void testTransformOptionIDsToRepresentation() {
		ResourceState<LinksEntity> representation =
				shipmentLineItemOptionLinksTransformer.transform(LINE_ITEM_OPTION_IDS, SHIPMENT_LINE_ITEM);

		int expectedLinks = LINE_ITEM_OPTION_IDS.size() + 1; // shipment line item options and link to shipment line item
		assertEquals(expectedLinks, representation.getLinks().size());
		assertNotNull(SELF_SHOULD_NOT_NULL, representation.getSelf());
	}
}
