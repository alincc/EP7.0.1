/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.transform;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.shipments.addresses.impl.ShippingAddressUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ShippingAddressUriBuilderFactory;

/**
 * Unit test for {@link ShippingAddressTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingAddressTransformerTest {

	@InjectMocks
	private ShippingAddressTransformer transformer;

	@Mock
	private ShippingAddressUriBuilderFactory shippingAddressUriBuilderFactory;

	private static final String SCOPE = "mobee";

	private static final String SHIPMENT_URI = "/shipments/shipment/mobee/giydamjr=/giydamjrfuyq=";
	private static final String SHIPMENT_ADDRESS_URI = "/shipments/shipment/mobee/giydamjr=/giydamjrfuyq=/shippingaddress";

	private static final String COUNTRY = "UK";

	private static final String FIRST_NAME = "Harry";

	@Before
	public void init() {
		when(shippingAddressUriBuilderFactory.get()).thenAnswer(invocation -> new ShippingAddressUriBuilderImpl());
	}

	@Test
	public void testShippingAddressTransformer() {
		AddressEntity addressEntity = createShippingAddressEntity(createAddressDetailEntity(), createNameEntity());
		ResourceState<ShipmentEntity> shipmentRepresentation = createShipmentRepresentation();

		ResourceState<AddressEntity> addressRepresentation = transformer.transform(addressEntity, shipmentRepresentation);

		assertEquals(COUNTRY, addressRepresentation.getEntity().getAddress().getCountryName());
		assertEquals(FIRST_NAME, addressRepresentation.getEntity().getName().getGivenName());
		assertEquals(SHIPMENT_ADDRESS_URI, addressRepresentation.getSelf().getUri());
		assertEquals("mobee", addressRepresentation.getScope());
	}

	private ResourceState<ShipmentEntity> createShipmentRepresentation() {
		Self self = SelfFactory.createSelf(SHIPMENT_URI);
		return ResourceState.Builder.create(ShipmentEntity.builder().build())
				.withSelf(self)
				.withScope(SCOPE)
				.build();
	}

	private AddressEntity createShippingAddressEntity(final AddressDetailEntity addressDetailEntity, final NameEntity nameEntity) {
		return AddressEntity.builder()
				.withAddress(addressDetailEntity)
				.withName(nameEntity)
				.withAddressId("ADDRESS_ID")
				.build();
	}

	private AddressDetailEntity createAddressDetailEntity() {
		return AddressDetailEntity.builder()
				.withStreetAddress("The Cupboard Under the Stairs")
				.withExtendedAddress("4 Privet Dr")
				.withLocality("Little Whinging")
				.withCountryName(COUNTRY)
				.withRegion("Surrey")
				.withPostalCode("123456")
				.build();
	}

	private NameEntity createNameEntity() {
		return NameEntity.builder()
				.withGivenName(FIRST_NAME)
				.withFamilyName("Potter")
				.build();
	}
}
