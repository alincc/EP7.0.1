/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.rel.DestinationInfoRepresentationRels;
import com.elasticpath.rest.resource.shipmentdetails.rel.ShipmentDetailsRels;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.link.impl.AddShippingOptionNeedInfoLinksToOrderStrategy;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ShippingOptionInfoUriBuilder;
import com.elasticpath.rest.schema.uri.ShippingOptionInfoUriBuilderFactory;

/**
 * Tests for {@link AddShippingOptionNeedInfoLinksToOrderStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class AddShippingOptionNeedInfoLinksToOrderStrategyTest {

	public static final String SCOPE = "scope";
	public static final String SHIPMENT_DETAILS_ID = "shipmentDetailsId";
	public static final String SHIPPING_OPTION_INFO_URI = "/shippingOptionInfoUri";
	public static final String ORDER_ID = "orderId";
	@Mock
	private ShipmentDetailsLookup shipmentDetailsLookup;
	@Mock
	private ShippingOptionInfoUriBuilderFactory shippingOptionInfoUriBuilderFactory;
	@Mock
	private ShippingOptionInfoUriBuilder shippingOptionInfoUriBuilder;
	@Mock
	private ResourceLink needInfoLink;
	@Mock
	private NeedInfoHandler needInfoHandler;

	private ResourceState<OrderEntity> orderRepresentation;
	private AddShippingOptionNeedInfoLinksToOrderStrategy strategy;

	@Before
	public void setUpCommonTestComponents() {
		given(shippingOptionInfoUriBuilderFactory.get()).willReturn(shippingOptionInfoUriBuilder);
		given(shippingOptionInfoUriBuilder.setScope(SCOPE)).willReturn(shippingOptionInfoUriBuilder);
		given(shippingOptionInfoUriBuilder.setShipmentDetailsId(SHIPMENT_DETAILS_ID)).willReturn(shippingOptionInfoUriBuilder);
		given(shippingOptionInfoUriBuilder.build()).willReturn(SHIPPING_OPTION_INFO_URI);

		given(shipmentDetailsLookup.findShipmentDetailsIdsForOrder(SCOPE, ORDER_ID))
				.willReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.singleton(SHIPMENT_DETAILS_ID)));

		orderRepresentation = ResourceState.Builder
				.create(OrderEntity.builder()
						.withOrderId(ORDER_ID)
						.build())
				.withScope(SCOPE)
				.build();

		strategy = new AddShippingOptionNeedInfoLinksToOrderStrategy(
				"resourceServerName", shipmentDetailsLookup, shippingOptionInfoUriBuilderFactory, needInfoHandler);
	}

	@Test
	public void ensureShippingOptionNeedInfoAbsentIfSelected() {
		given(needInfoHandler.getNeedInfoLinksForInfo(SHIPPING_OPTION_INFO_URI, ShipmentDetailsRels.SHIPPINGOPTION_REL))
				.willReturn(Collections.<ResourceLink>emptyList());

		Collection<ResourceLink> links = strategy.getLinks(orderRepresentation);

		assertThat(links, empty());
	}

	/**
	 * See COR-2484.
	 */
	@Test
	public void ensureShippingOptionNeedInfoAbsentIfDestinationNeedInfoPresent() {
		given(needInfoHandler.getNeedInfoLinksForInfo(anyString(), eq(DestinationInfoRepresentationRels.DESTINATION_REL)))
				.willReturn(Collections.singleton(needInfoLink));
		ResourceLink resourceLink = ResourceLink.builder().build();
		given(needInfoHandler.getNeedInfoLinksForInfo(SHIPPING_OPTION_INFO_URI, ShipmentDetailsRels.SHIPPINGOPTION_REL))
				.willReturn(Collections.singleton(resourceLink));

		Collection<ResourceLink> links = strategy.getLinks(orderRepresentation);
		assertThat(links, contains(needInfoLink));
	}

	@Test
	public void ensureShippingOptionNeedInfoPresentIfUnselected() {
		given(needInfoHandler.getNeedInfoLinksForInfo(SHIPPING_OPTION_INFO_URI, ShipmentDetailsRels.SHIPPINGOPTION_REL))
				.willReturn(Collections.singleton(needInfoLink));

		Collection<ResourceLink> links = strategy.getLinks(orderRepresentation);

		assertThat(links, hasItems(needInfoLink));
	}

	@Test
	public void ensureNoLinksAreReturnedIfShipmentDetailsLookupFails() {
		given(shipmentDetailsLookup.findShipmentDetailsIdsForOrder(SCOPE, ORDER_ID))
				.willReturn(ExecutionResultFactory.<Collection<String>>createNotFound());

		Collection<ResourceLink> links = strategy.getLinks(orderRepresentation);

		assertThat(links, empty());
	}

}
