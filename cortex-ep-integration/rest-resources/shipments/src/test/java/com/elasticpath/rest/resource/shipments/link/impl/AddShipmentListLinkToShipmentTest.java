/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.link.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.rel.ListElementRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilder;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.TestUriBuilderFactory;

/**
 * Unit test.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddShipmentListLinkToShipmentTest {

	private static final String PURCHASE_URI = "/purchases/scope/purchase-id";
	private static final String SHIPMENTS_LIST_URI = "/shipments/purchases/scope/purchase-id";
	private static final String SHIPMENT_URI = "/shipments/purchases/scope/purchase-id/shipment-id";

	@Mock
	private ShipmentEntity shipmentEntity;

	@Mock
	private PurchaseUriBuilderFactory purchaseUriBuilderFactory;
	@Mock
	private ShipmentsUriBuilderFactory shipmentsUriBuilderFactory;
	@InjectMocks
	private AddShipmentListLinkToShipment linkStrategy;

	@Test
	public void testGetLinks() throws Exception {
		PurchaseUriBuilder purchaseUriBuilder
				= TestUriBuilderFactory.mockUriBuilder(PurchaseUriBuilder.class, PURCHASE_URI);
		when(purchaseUriBuilderFactory.get()).thenReturn(purchaseUriBuilder);
		ShipmentsUriBuilder shipmentsUriBuilder
				= TestUriBuilderFactory.mockUriBuilder(ShipmentsUriBuilder.class, SHIPMENTS_LIST_URI);
		when(shipmentsUriBuilderFactory.get()).thenReturn(shipmentsUriBuilder);
		when(shipmentEntity.getPurchaseId()).thenReturn("purchase-id");
		Self shipmentSelf = SelfFactory.createSelf(SHIPMENT_URI);
		ResourceState<ShipmentEntity> shipmentRepresentation
			= ResourceState.Builder.create(shipmentEntity)
				.withSelf(shipmentSelf)
				.withScope("scope")
				.build();
		ResourceLink expectedLink
			= ResourceLinkFactory.createNoRev(
				SHIPMENTS_LIST_URI,
				CollectionsMediaTypes.LINKS.id(),
				ListElementRels.LIST);

		Iterable<ResourceLink> links = linkStrategy.getLinks(shipmentRepresentation);

		assertEquals(expectedLink, links.iterator().next());
	}
}