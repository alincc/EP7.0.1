/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Test cases for {@link ShipmentLineItemOptionTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemOptionValueTransformerImplTest {

	private static final String SELF_SHOULD_NOT_NULL = "Self should not be null.";
	private static final String SHOULD_MATCH_EXPECTED = "The values should match.";
	private static final String SCOPE = "TEST";
	private static final String NAME = "name";
	private static final String DISPLAY_NAME = "display-name";

	private final ResourceState<ShipmentLineItemOptionEntity> lineItemOption =
			ResourceState.Builder
					.create(ShipmentLineItemOptionEntity.builder().build())
					.withScope(SCOPE)
					.build();

	private static final ShipmentLineItemOptionValueEntity LINEITEM_OPTION_VALUE_DTO = ShipmentLineItemOptionValueEntity.builder()
			.withName(NAME)
			.withDisplayName(DISPLAY_NAME)
			.build();

	@Mock
	private ResourceOperationContext operationContext;
	@Mock
	private ResourceOperation resourceOperation;
	@InjectMocks
	private ShipmentLineItemOptionValueTransformerImpl shipmentLineItemOptionValueTransformer;

	@Before
	public void setUp() {
		when(operationContext.getResourceOperation()).thenReturn(resourceOperation);
	}


	@Test
	public void testTransformLineItemOptionValueDtoToRepresentation() {
		ResourceState<ShipmentLineItemOptionValueEntity> representation = shipmentLineItemOptionValueTransformer
				.transform(LINEITEM_OPTION_VALUE_DTO, lineItemOption);

		int expectedLinks = 1; // line item options link
		assertEquals(expectedLinks, representation.getLinks().size());
		assertNotNull(SELF_SHOULD_NOT_NULL, representation.getSelf());
		assertEquals(SHOULD_MATCH_EXPECTED, NAME, representation.getEntity().getName());
		assertEquals(SHOULD_MATCH_EXPECTED, DISPLAY_NAME, representation.getEntity().getDisplayName());
	}

}
