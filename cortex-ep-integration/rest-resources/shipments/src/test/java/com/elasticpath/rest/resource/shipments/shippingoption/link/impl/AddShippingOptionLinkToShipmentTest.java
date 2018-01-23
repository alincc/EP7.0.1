/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.link.impl;

import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.shipmentdetails.ShipmentdetailsMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels;
import com.elasticpath.rest.resource.shipments.shippingoption.rel.ShippingOptionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ShippingOptionUriBuilder;
import com.elasticpath.rest.schema.uri.ShippingOptionUriBuilderFactory;
import com.elasticpath.rest.schema.uri.TestUriBuilderFactory;

/**
 * Tests for {@link AddShippingOptionLinkToShipment}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddShippingOptionLinkToShipmentTest {

	private static final String SCOPE = "testScope";

	private static final String PURCHASE_ID = "testPurchaseId";

	private static final String SHIPMENT_ID = "testShipmentId";

	private static final String SHIPPING_OPTION_URI = "testShippingCostUri";

	@Mock
	private ShippingOptionUriBuilderFactory shippingOptionUriBuilderFactory;

	@InjectMocks
	private AddShippingOptionLinkToShipment linkStrategy;

	private ResourceState<ShipmentEntity> shipmentResourceState;

	@Test
	public void testCreateLinksSuccess() {
		mockUriBuilders();
		prepareShipmentRepresentation();

		Iterable<ResourceLink> links = linkStrategy.getLinks(shipmentResourceState);

		assertThat("The created links should be the same as expected", links, Matchers.hasItems(getExpectedLink()));
	}

	private void mockUriBuilders() {
		ShippingOptionUriBuilder shippingOptionUriBuilder = TestUriBuilderFactory.mockUriBuilder(ShippingOptionUriBuilder.class, SHIPPING_OPTION_URI);
		Mockito.when(shippingOptionUriBuilderFactory.get()).thenReturn(shippingOptionUriBuilder);
	}

	private void prepareShipmentRepresentation() {
		ShipmentEntity shipmentEntity = ShipmentEntity.builder()
				.withPurchaseId(PURCHASE_ID)
				.withShipmentId(SHIPMENT_ID)
				.build();
		shipmentResourceState = ResourceState.Builder.create(shipmentEntity)
				.withScope(SCOPE)
				.build();
	}

	private ResourceLink getExpectedLink() {
		return ResourceLinkFactory.create(SHIPPING_OPTION_URI, ShipmentdetailsMediaTypes.SHIPPING_OPTION.id(),
				ShippingOptionResourceRels.SHIPPING_OPTION_REL,	ShipmentsResourceRels.SHIPMENT_REV);
	}

}
