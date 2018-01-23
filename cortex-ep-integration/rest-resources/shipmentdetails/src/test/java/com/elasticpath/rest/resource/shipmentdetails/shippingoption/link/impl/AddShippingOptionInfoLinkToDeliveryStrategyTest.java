/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.link.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.given;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.rel.ShipmentDetailsRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ShippingOptionInfoUriBuilder;
import com.elasticpath.rest.schema.uri.ShippingOptionInfoUriBuilderFactory;

/**
 * Tests {@link AddShippingOptionInfoLinkToDeliveryStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddShippingOptionInfoLinkToDeliveryStrategyTest {

	public static final String SHIPMENT_DETAILS_ID = "shipmentDetailsId";
	public static final String SCOPE = "scope";
	public static final String SHIPPING_OPTION_INFO_URI = "/shippingOptionInfoUri";
	@Mock
	private ShippingOptionInfoUriBuilderFactory shippingOptionInfoUriBuilderFactory;
	@Mock
	private ShipmentDetailsLookup shipmentDetailsLookup;
	@InjectMocks
	private AddShippingOptionInfoLinkToDeliveryStrategy strategy;
	@Mock
	private ShippingOptionInfoUriBuilder shippingOptionInfoUriBuilder;

	private ResourceState<DeliveryEntity> deliveryRepresentation;

	@Before
	public void setUpTestComponents() {
		given(shippingOptionInfoUriBuilderFactory.get()).willReturn(shippingOptionInfoUriBuilder);
		given(shippingOptionInfoUriBuilder.setShipmentDetailsId(SHIPMENT_DETAILS_ID)).willReturn(shippingOptionInfoUriBuilder);
		given(shippingOptionInfoUriBuilder.setScope(SCOPE)).willReturn(shippingOptionInfoUriBuilder);
		given(shippingOptionInfoUriBuilder.build()).willReturn(SHIPPING_OPTION_INFO_URI);

		deliveryRepresentation = ResourceState.Builder.create(DeliveryEntity.builder().build())
				.withScope(SCOPE)
				.build();
	}

	@Test
	public void ensureShippingOptionInfoLinkIsAttachedForValidDelivery() {
		given(shipmentDetailsLookup.findShipmentDetailsIdForDelivery(deliveryRepresentation))
				.willReturn(ExecutionResultFactory.createReadOK(SHIPMENT_DETAILS_ID));

		Collection<ResourceLink> links = strategy.getLinks(deliveryRepresentation);

		assertThat(links, hasItems(ResourceLinkFactory.create(SHIPPING_OPTION_INFO_URI, ControlsMediaTypes.INFO.id(),
															ShipmentDetailsRels.SHIPPING_OPTION_INFO_REL, ShipmentDetailsRels.DELIVERY_REV)));
	}

	@Test
	public void ensureNoLinksAreReturnedIfShipmentDetailsLookupFails() {
		given(shipmentDetailsLookup.findShipmentDetailsIdForDelivery(deliveryRepresentation))
				.willReturn(ExecutionResultFactory.<String>createNotFound());

		Collection<ResourceLink> links = strategy.getLinks(deliveryRepresentation);

		assertThat(links, empty());
	}
}
