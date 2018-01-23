/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ShippingOptionUriBuilder;
import com.elasticpath.rest.schema.uri.ShippingOptionUriBuilderFactory;
import com.elasticpath.rest.schema.uri.TestUriBuilderFactory;

/**
 * Tests for {@link ShippingOptionTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionTransformerTest {

	private static final String SCOPE = "testScope";
	private static final String SHIPMENT_URI = "testShipmentUri";
	private static final String SHIPPING_OPTION_URI = "testShippingCostUri";
	private static final double DOLLAR_AMOUNT = 100.1;
	@Mock
	private ShippingOptionUriBuilderFactory shippingOptionUriBuilderFactory;
	@InjectMocks
	private ShippingOptionTransformer transformer;

	private final List<CostEntity> costsList = new ArrayList<>();
	private ShippingOptionEntity shippingOptionEntity;
	private ResourceState<ShipmentEntity> shipmentRepresentation;


	@Before
	public void setUp() {
		mockUriBuilders();

		CostEntity cost = CostEntity.builder()
				.withAmount(new BigDecimal(DOLLAR_AMOUNT))
				.withCurrency("CAD")
				.withDisplay("100.10")
				.build();

		costsList.add(cost);

		shippingOptionEntity = ShippingOptionEntity.builder()
				.withCost(costsList)
				.build();

		shipmentRepresentation = ResourceState.Builder
				.create(ShipmentEntity.builder().build())
				.withSelf(SelfFactory.createSelf(SHIPMENT_URI))
				.withScope(SCOPE)
				.build();
	}

	@Test
	public void testTranslatedFieldsCorrect() {
		ResourceState<ShippingOptionEntity> representation = transformer.transform(shippingOptionEntity, shipmentRepresentation);

		ShippingOptionEntity shippingOptionEntity = representation.getEntity();
		assertNotNull("Returned representation should not be null.", representation);
		assertEquals("Representation's scope should match the input value.", SCOPE, representation.getScope());
		assertEquals("Representation's cost should match the input entity.", costsList, shippingOptionEntity.getCost());
	}


	@Test
	public void testSelfPopulated() {
		ResourceState<ShippingOptionEntity> representation = transformer.transform(shippingOptionEntity, shipmentRepresentation);

		Self self = representation.getSelf();
		assertNotNull("Representation's self should not be null.", self);
		assertEquals("Self should contain the generated shippingoption URI.", SHIPPING_OPTION_URI, self.getUri());
	}

	private void mockUriBuilders() {
		ShippingOptionUriBuilder shippingOptionUriBuilder = TestUriBuilderFactory.mockUriBuilder(ShippingOptionUriBuilder.class, SHIPPING_OPTION_URI);
		when(shippingOptionUriBuilderFactory.get()).thenReturn(shippingOptionUriBuilder);
	}

}
