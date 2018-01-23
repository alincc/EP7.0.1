/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.link;

import static com.elasticpath.rest.definition.taxes.TaxesMediaTypes.TAXES;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.shipments.ShipmentEntity;
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

/**
 * Tests for {@link LinkShipmentToShipmentTaxesStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkShipmentToShipmentTaxesStrategyTest {

	private static final String SOURCE_URI = "/test/source/uri=";
	private static final String TAXES_URI = URIUtil.format("taxes", SOURCE_URI);

	@Mock private TaxesUriBuilderFactory taxesUriBuilderFactory;
	@Mock private ShipmentEntity shipmentEntity;

	@InjectMocks private LinkShipmentToShipmentTaxesStrategy linkStrategy;

	private final TaxesUriBuilder taxesUriBuilder = TestUriBuilderFactory.mockUriBuilder(TaxesUriBuilder.class, TAXES_URI);
	private ResourceState<ShipmentEntity> shipmentRepresentation;

	@Test
	public void testGetLinks() {
		when(taxesUriBuilderFactory.get()).thenReturn(taxesUriBuilder);
		Self self = SelfFactory.createSelf(SOURCE_URI);
		shipmentRepresentation = ResourceState.Builder.create(shipmentEntity).withSelf(self).build();

		ArrayList<ResourceLink> links = Lists.newArrayList(linkStrategy.getLinks(shipmentRepresentation));

		assertEquals("Exactly 1 link should be created.", 1, links.size());
		AssertResourceLink.assertResourceLink(links.get(0))
				.type(TAXES.id())
				.rel(TaxesResourceRels.TAX_REL)
				.rev(ShipmentTaxesResourceRels.SHIPMENT_REV)
				.uri(TAXES_URI);
	}

}
