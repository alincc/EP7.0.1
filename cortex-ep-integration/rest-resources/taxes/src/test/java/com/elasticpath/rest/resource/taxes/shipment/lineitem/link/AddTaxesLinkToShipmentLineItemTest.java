/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.link;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.taxes.TaxesMediaTypes;
import com.elasticpath.rest.resource.taxes.rel.TaxesResourceRels;
import com.elasticpath.rest.resource.taxes.shipment.rel.ShipmentTaxesResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.TaxesUriBuilder;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;
import com.elasticpath.rest.schema.uri.TestUriBuilderFactory;
import com.elasticpath.rest.test.AssertResourceLink;
import com.elasticpath.rest.uri.URIUtil;

import com.google.common.collect.Lists;

/**
 * Unit test for {@link AddTaxesLinkToShipmentLineItem}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddTaxesLinkToShipmentLineItemTest {

	private static final String SOURCE_URI = "/test/source/uri=";
	private static final String TAXES_URI = URIUtil.format("taxes", SOURCE_URI);

	@Mock private TaxesUriBuilderFactory taxesUriBuilderFactory;

	@InjectMocks private AddTaxesLinkToShipmentLineItem linkStrategy;

	private final TaxesUriBuilder taxesUriBuilder = TestUriBuilderFactory.mockUriBuilder(TaxesUriBuilder.class, TAXES_URI);

	@Test
	public void testGetLinks() {

		arrangeTaxUriBuilder();
		ResourceState<ShipmentLineItemEntity> shipmentRepresentation = createResourceState();

		ArrayList<ResourceLink> links = Lists.newArrayList(linkStrategy.getLinks(shipmentRepresentation));

		assertEquals("Exactly 1 link should be created.", 1, links.size());
		AssertResourceLink.assertResourceLink(links.get(0))
				.type(TaxesMediaTypes.TAXES.id())
				.rel(TaxesResourceRels.TAX_REL)
				.rev(getResultLinkRev())
				.uri(TAXES_URI);
	}

	private void arrangeTaxUriBuilder() {
		when(taxesUriBuilderFactory.get()).thenReturn(taxesUriBuilder);
	}

	private ResourceState<ShipmentLineItemEntity> createResourceState() {
		ShipmentLineItemEntity shipmentEntity = mock(ShipmentLineItemEntity.class);
		Self self = SelfFactory.createSelf(SOURCE_URI);
		return ResourceState.Builder.create(shipmentEntity)
				.withSelf(self)
				.build();
	}

	private String getResultLinkRev() {
		return ShipmentTaxesResourceRels.SHIPMENT_LINE_ITEM_REV;
	}
}
