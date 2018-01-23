/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilder;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShipmentLineItemUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentLineItemUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.TestUriBuilderFactory;

/**
 * Unit test.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddShipmentLineItemLinkToShipmentLineItemPriceTest {

	@Mock
	private PurchaseUriBuilderFactory purchaseUriBuilderFactory;

	@Mock
	private ShipmentsUriBuilderFactory shipmentsUriBuilderFactory;

	@Mock
	private ShipmentLineItemUriBuilderFactory shipmentLineItemUriBuilderFactory;

	@InjectMocks
	private AddShipmentLineItemLinkToShipmentLineItemPrice addShipmentLineItemLinkToShipmentLineItemPrice;

	@Mock
	private ShipmentLineItemPriceEntity shipmentLineItemPriceEntity;

	@Test
	public void testLinkHappy() {
		String purchaseUri = "/purchase/scope/id";
		String shipmentUri = "/shipments/" + purchaseUri + "/id";
		String shipmentLineItemUri = shipmentUri + "/lineitems/id";
		String pricesUri = "/prices" + shipmentLineItemUri;
		PurchaseUriBuilder purchaseUriBuilder = TestUriBuilderFactory.mockUriBuilder(PurchaseUriBuilder.class, purchaseUri);
		when(purchaseUriBuilderFactory.get()).thenReturn(purchaseUriBuilder);
		ShipmentsUriBuilder shipmentsUriBuilder = TestUriBuilderFactory.mockUriBuilder(ShipmentsUriBuilder.class, shipmentUri);
		when(shipmentsUriBuilderFactory.get()).thenReturn(shipmentsUriBuilder);
		ShipmentLineItemUriBuilder shipmentLineItemUriBuilder
				= TestUriBuilderFactory.mockUriBuilder(ShipmentLineItemUriBuilder.class, shipmentLineItemUri);
		when(shipmentLineItemUriBuilderFactory.get()).thenReturn(shipmentLineItemUriBuilder);
		when(shipmentLineItemPriceEntity.getPurchaseId()).thenReturn("id");
		ResourceState<ShipmentLineItemPriceEntity> lineItemRep = ResourceState.Builder.create(shipmentLineItemPriceEntity)
				.withSelf(SelfFactory.createSelf(pricesUri))
				.withScope("scope")
				.build();
		ResourceLink expectedLink = ResourceLinkFactory.create(shipmentLineItemUri,
				ShipmentsMediaTypes.SHIPMENT_LINE_ITEM.id(),
				PriceRepresentationRels.SHIPMENT_LINE_ITEM_REL,
				PriceRepresentationRels.PRICE_REV);

		Iterable<ResourceLink> links = addShipmentLineItemLinkToShipmentLineItemPrice.getLinks(lineItemRep);

		assertTrue(links.iterator().hasNext());
		assertEquals(expectedLink, links.iterator().next());
	}
}
