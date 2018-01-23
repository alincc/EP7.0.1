/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.transform;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.shipments.impl.ShipmentsUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilderFactory;

/**
 * Unit test for {@link ShipmentLinksTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLinksTransformerTest {

	@Mock
	private ResourceState<PurchaseEntity> purchase;
	@Mock
	private ShipmentsUriBuilderFactory shipmentsUriBuilderFactory;

	private ShipmentLinksTransformer shipmentLinksTransformer;

	@Before
	public void setUp() {
		when(shipmentsUriBuilderFactory.get()).thenAnswer(invocation -> new ShipmentsUriBuilderImpl("test"));

		Self mockSelf = mock(Self.class);
		when(purchase.getSelf()).thenReturn(mockSelf);
		when(mockSelf.getUri()).thenReturn("purchases/qwerty");

		shipmentLinksTransformer = new ShipmentLinksTransformer(shipmentsUriBuilderFactory);
	}

	@Test
	public void testTransformShipmentIdsToLinks() {
		List<String> shipmentIds = Arrays.asList("1", "2", "3");

		ResourceState<LinksEntity> linksRepresentation = shipmentLinksTransformer.transform(shipmentIds, purchase);
		List<ResourceLink> links = linksRepresentation.getLinks();

		final int expectedLinkCount = shipmentIds.size() + 1; // shipments, and purchase links
		assertEquals("The returned list should contain link for Purchase, and one link per shipmentId.", expectedLinkCount, links.size());
	}
}
