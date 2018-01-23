/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.link.impl;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.constants.DestinationInfoConstants;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.rel.DestinationInfoRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link com.elasticpath.rest.resource.shipmentdetails.destinationinfo.link.impl.AddDestinationInfoLinkToDeliveryStrategy}.
 */
public final class AddDestinationInfoLinkToDeliveryStrategyTest {

	private static final String DELIVERY_ID = "delivery_id";
	private static final String ORDER_ID = "order_id";
	private static final String SHIPMENT_ID = "shipment_id";
	private static final String RESOURCE = "shipmentdetails";
	private static final String SCOPE = "scope";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ShipmentDetailsLookup mockShipmentDetailsLookup = context.mock(ShipmentDetailsLookup.class);
	private final AddDestinationInfoLinkToDeliveryStrategy strategy =
			new AddDestinationInfoLinkToDeliveryStrategy(RESOURCE, mockShipmentDetailsLookup);


	/**
	 * Test create links.
	 */
	@Test
	public void testCreateLinks() {
		final ResourceState<DeliveryEntity> delivery = createDeliveryRepresentation(DestinationInfoConstants.SHIPMENT_TYPE);

		context.checking(new Expectations() {
			{
				allowing(mockShipmentDetailsLookup).findShipmentDetailsIdForDelivery(delivery);
				will(returnValue(ExecutionResultFactory.createReadOK(SHIPMENT_ID)));
			}
		});

		Collection<ResourceLink> links = strategy.getLinks(delivery);

		String destinationInfoUri = URIUtil.format(RESOURCE, SCOPE, SHIPMENT_ID, DestinationInfo.URI_PART);
		ResourceLink destinationInfoLink = ResourceLinkFactory.create(destinationInfoUri, ControlsMediaTypes.INFO.id(),
				DestinationInfoRepresentationRels.DESTINATION_INFO_REL, DestinationInfoRepresentationRels.DELIVERY_REV);

		assertTrue(CollectionUtil.containsOnly(Collections.singleton(destinationInfoLink), links));
	}

	/**
	 * Test create links with shipment lookup error.
	 */
	@Test
	public void testCreateLinksWithShipmentLookupError() {
		final ResourceState<DeliveryEntity> delivery = createDeliveryRepresentation(DestinationInfoConstants.SHIPMENT_TYPE);

		context.checking(new Expectations() {
			{
				allowing(mockShipmentDetailsLookup).findShipmentDetailsIdForDelivery(delivery);
				will(returnValue(ExecutionResultFactory.createNotFound(StringUtils.EMPTY)));
			}
		});

		Collection<ResourceLink> links = strategy.getLinks(delivery);
		assertTrue(CollectionUtil.isEmpty(links));
	}

	/**
	 * Test create links with electronic delivery.
	 */
	@Test
	public void testCreateLinksWithElectronicDelivery() {
		final ResourceState<DeliveryEntity> delivery = createDeliveryRepresentation("ELECTRONIC");

		context.checking(new Expectations() {
			{
				allowing(mockShipmentDetailsLookup).findShipmentDetailsIdForDelivery(delivery);
				will(returnValue(ExecutionResultFactory.createReadOK(SHIPMENT_ID)));
			}
		});

		Collection<ResourceLink> links = strategy.getLinks(delivery);
		assertTrue(CollectionUtil.isEmpty(links));
	}

	private ResourceState<DeliveryEntity> createDeliveryRepresentation(final String deliveryType) {
		return ResourceState.Builder
				.create(DeliveryEntity.builder()
						.withOrderId(ORDER_ID)
						.withDeliveryId(DELIVERY_ID)
						.withDeliveryType(deliveryType)
						.build())
				.withScope(SCOPE)
				.build();
	}
}
