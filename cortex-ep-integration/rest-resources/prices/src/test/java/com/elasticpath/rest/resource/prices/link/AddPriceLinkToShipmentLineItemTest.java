/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.prices.PricesMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PricesUriBuilder;
import com.elasticpath.rest.schema.uri.PricesUriBuilderFactory;

/**
 * Unit test.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddPriceLinkToShipmentLineItemTest {

	@Mock
	private PricesUriBuilderFactory pricesUriBuilderFactory;

	@InjectMocks
	private AddPriceLinkToShipmentLineItem addPriceLinkToShipmentLineItem;

	@Mock
	private ShipmentLineItemEntity shipmentLineItemEntity;

	@Mock
	private PricesUriBuilder pricesUriBuilder;

	@Test
	public void testLinkHappy() {
		String shipmentLineItemUri = "/shipments/purchase/scope/id/id/lineitems/id";
		String pricesUri = "/prices" + shipmentLineItemUri;
		when(pricesUriBuilderFactory.get()).thenReturn(pricesUriBuilder);
		when(pricesUriBuilder.setSourceUri(anyString())).thenReturn(pricesUriBuilder);
		when(pricesUriBuilder.build()).thenReturn(pricesUri);
		ResourceState<ShipmentLineItemEntity> lineItemRep = ResourceState.Builder.create(shipmentLineItemEntity)
				.withSelf(SelfFactory.createSelf(shipmentLineItemUri))
				.build();
		ResourceLink expectedLink = ResourceLinkFactory.create(pricesUri,
				PricesMediaTypes.SHIPMENT_LINE_ITEM_PRICE.id(), PriceRepresentationRels.PRICE_REL,
				PriceRepresentationRels.SHIPMENT_LINE_ITEM_REV);

		Iterable<ResourceLink> links = addPriceLinkToShipmentLineItem.getLinks(lineItemRep);

		assertTrue(links.iterator().hasNext());
		assertEquals(expectedLink, links.iterator().next());
	}
}
