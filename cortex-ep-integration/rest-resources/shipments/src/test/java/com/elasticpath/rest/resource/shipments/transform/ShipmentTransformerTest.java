/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.transform;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.StatusEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test cases for {@link ShipmentTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentTransformerTest {

	private static final String SCOPE = "TEST";
	private static final String STATUS_CODE = "TEST_STATUS";
	private static final String PURCHASE_ID = "order-12345";
	private static final String SHIPMENT_ID = "shipment-12345-1";
	private static final String URI = "/asd/asd/asd";

	private final StatusEntity inputStatusEntity = StatusEntity.builder()
			.withCode(STATUS_CODE)
			.build();
	private final ShipmentEntity inputShipmentEntity = ShipmentEntity.builder()
			.withStatus(inputStatusEntity)
			.withPurchaseId(PURCHASE_ID)
			.withShipmentId(SHIPMENT_ID)
			.build();

	@Mock
	private ResourceOperation resourceOperation;
	@Mock
	private ResourceOperationContext operationContext;
	@InjectMocks
	private ShipmentTransformer shipmentTransformer;


	@Before
	public void setUp() {
		when(resourceOperation.getUri()).thenReturn(URI);
		when(operationContext.getResourceOperation()).thenReturn(resourceOperation);
	}

	@Test
	public void testRepresentationCorrectWhenTransforming() {

		ResourceState<ShipmentEntity> representation = shipmentTransformer.transform(SCOPE, inputShipmentEntity);

		assertEquals("Representation scope should match input scope.", SCOPE, representation.getScope());
		StatusEntity status = representation.getEntity().getStatus();
		assertEquals("Representation purchase ID should be entity purchase ID.",
				PURCHASE_ID, representation.getEntity().getPurchaseId());
		assertEquals("Representation shipment ID should be entity shipment ID.",
				SHIPMENT_ID, representation.getEntity().getShipmentId());
		assertEquals("Representation status code should match entity status code.", STATUS_CODE, status.getCode());
	}

	@Test
	public void testSelfCorrectWhenTransforming() {
		Self expectedSelf = SelfFactory.createSelf(URI);

		ResourceState<ShipmentEntity> representation = shipmentTransformer.transform(SCOPE, inputShipmentEntity);

		assertEquals(expectedSelf, representation.getSelf());
	}


	@Test
	public void testShipmentsLinkCorrectWhenTransforming() {
		ResourceState<ShipmentEntity> representation = shipmentTransformer.transform(SCOPE, inputShipmentEntity);

		List<ResourceLink> links = representation.getLinks();
		assertEquals("Shipment should contain no links", 0, links.size());
	}

}
