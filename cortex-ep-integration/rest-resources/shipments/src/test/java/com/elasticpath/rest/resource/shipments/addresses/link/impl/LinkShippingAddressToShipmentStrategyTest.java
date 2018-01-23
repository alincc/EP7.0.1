/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.link.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.shipments.addresses.ShippingAddress;
import com.elasticpath.rest.resource.shipments.addresses.ShippingAddressLookup;
import com.elasticpath.rest.resource.shipments.addresses.impl.ShippingAddressUriBuilderImpl;
import com.elasticpath.rest.resource.shipments.addresses.rel.ShippingAddressResourceRels;
import com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.uri.ShippingAddressUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Unit test for {@link LinkShippingAddressToShipmentStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkShippingAddressToShipmentStrategyTest {

	private static final String SHIPMENT_URI = "/shipments/purchase/mobee/giydambq=/giydambqfuyq=";

	@InjectMocks
	private LinkShippingAddressToShipmentStrategy linkStrategy;

	@Mock
	private ShippingAddressLookup shippingAddressLookup;

	@Mock
	private ResourceState<AddressEntity> addressRepresentation;

	@Mock
	private ShippingAddressUriBuilderFactory shippingAddressUriBuilderFactory;

	@Before
	public void init() {
		when(shippingAddressUriBuilderFactory.get()).thenAnswer(invocation -> new ShippingAddressUriBuilderImpl());
	}

	@Test
	public void testShippingAddressLinkCreatedSuccessfully() {

		ResourceState<ShipmentEntity> shipment = createOtherRepresentation();
		when(shippingAddressLookup.getShippingAddress(shipment))
				.thenReturn(ExecutionResultFactory.createReadOK(addressRepresentation));

		Iterable<ResourceLink> links = linkStrategy.getLinks(shipment);

		assertThat("The created links should be the same as expected", links, Matchers.hasItems(createExpectedLink()));
	}

	@Test
	public void testNoShippingAddressLinkCreatedWhenLookupFails() {

		ResourceState<ShipmentEntity> shipment = createOtherRepresentation();
		when(shippingAddressLookup.getShippingAddress(shipment))
				.thenReturn(ExecutionResultFactory.<ResourceState<AddressEntity>>createNotFound());

		Iterable<ResourceLink> links = linkStrategy.getLinks(shipment);

		assertThat("No link should be created when the address is not found.", links, Matchers.emptyIterable());
	}

	private ResourceLink createExpectedLink() {
		return ResourceLinkFactory.create(
				URIUtil.format(SHIPMENT_URI, ShippingAddress.URI_PART),
				AddressesMediaTypes.ADDRESS.id(),
				ShippingAddressResourceRels.SHIPPING_ADDRESS_REL,
				ShipmentsResourceRels.SHIPMENT_REV);
	}

	private ResourceState<ShipmentEntity> createOtherRepresentation() {
		Self self = mock(Self.class);
		when(self.getUri()).thenReturn(SHIPMENT_URI);

		return ResourceState.Builder.create(ShipmentEntity.builder().build())
				.withSelf(self)
				.build();
	}
}
